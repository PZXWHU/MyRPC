package com.pzx.rpc.service.proxy;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.socket.client.SocketClient;

import javax.xml.ws.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端代理类，代理服务接口。
 * 服务接口的实现类只在rpc服务端存在，所以客户端使用动态代理服务接口，客户端调用服务接口则rpc调用服务端的服务接口实现对象的方法
 */
public class ServiceProxy implements InvocationHandler {


    private final RpcClient rpcClient;

    public ServiceProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcResponse rpcResponse = rpcClient.sendRequest(rpcRequest);
        return rpcResponse == null ? null : rpcResponse.getData();

    }


}
