package com.pzx.rpc.service.proxy;

import com.google.common.base.Preconditions;
import com.pzx.rpc.enumeration.InvokeType;
import com.pzx.rpc.invoke.CallbackInvocationHandler;
import com.pzx.rpc.invoke.FutureInvocationHandler;
import com.pzx.rpc.invoke.OneWayInvocationHandler;
import com.pzx.rpc.invoke.SyncInvocationHandler;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.service.registry.ZkServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.client.NettyClient;
import lombok.Getter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Getter
public class ProxyConfig {

    private InvokeType invokeType = InvokeType.SYNC;
    private long timeout = Long.MAX_VALUE;
    private String directServerUrl;
    private String registryCenterUrl;

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        RpcClient rpcClient = createRpcClient();
        InvocationHandler invocationHandler = null;
        switch (invokeType){
            case ONEWAY:
                invocationHandler = new OneWayInvocationHandler(rpcClient, timeout);
                break;
            case SYNC:
                invocationHandler = new SyncInvocationHandler(rpcClient, timeout);
                break;
            case FUTURE:
                invocationHandler = new FutureInvocationHandler(rpcClient, timeout);
                break;
            case CALLBACK:
                invocationHandler = new CallbackInvocationHandler(rpcClient, timeout);
                break;
        }

        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, invocationHandler);

        return proxy;
    }

    public ProxyConfig setInvokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
        return this;
    }

    public ProxyConfig setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public ProxyConfig setDirectServerUrl(String directServerUrl) {
        this.directServerUrl = directServerUrl;
        return this;
    }

    public ProxyConfig setRegistryCenterUrl(String registryCenterUrl) {
        this.registryCenterUrl = registryCenterUrl;
        return this;
    }

    public ProxyFactory createProxyFactory(){
        return new ProxyFactory(this);
    }

    private RpcClient createRpcClient(){
        Preconditions.checkArgument(directServerUrl != null || registryCenterUrl != null,
                "directServerUrl and registryCenterUrl can not be null at the same time!");
        RpcClient rpcClient;
        if (this.directServerUrl != null){
            String[] serverUrlArray = this.directServerUrl.split(":");
            InetSocketAddress serverAddress = new InetSocketAddress(serverUrlArray[0], Integer.parseInt(serverUrlArray[1]));
            rpcClient = new NettyClient(serverAddress);
        }else {
            String[] registryUrlArray = this.registryCenterUrl.split(":");
            ServiceRegistry serviceRegistry;
            InetSocketAddress registryAddress = new InetSocketAddress(registryUrlArray[1], Integer.parseInt(registryUrlArray[2]));
            switch (registryUrlArray[0]){
                case "nacos":
                    serviceRegistry = new NacosServiceRegistry(registryAddress);
                    break;
                case "zookeeper":
                    serviceRegistry = new ZkServiceRegistry(registryAddress);
                    break;
                default:
                    throw new RuntimeException("No such ServiceRegistry!");
            }
            rpcClient = new NettyClient(serviceRegistry);
        }
        return rpcClient;
    }

}
