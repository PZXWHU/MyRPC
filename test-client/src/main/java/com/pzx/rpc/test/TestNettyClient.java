package com.pzx.rpc.test;

import com.pzx.rpc.api.ByeService;
import com.pzx.rpc.api.HelloObject;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.InvokeType;
import com.pzx.rpc.factory.ThreadPoolFactory;
import com.pzx.rpc.invoke.RpcResponseCallBack;
import com.pzx.rpc.service.proxy.ProxyConfig;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.client.ChannelPool;
import com.pzx.rpc.transport.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestNettyClient {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyClient.class);

    public static void main(String[] args) throws Exception {

        ProxyConfig proxyConfig = new ProxyConfig()
                .setDirectServerUrl("127.0.0.1:9999")
                //.setRegistryCenterUrl("nacos://192.168.99.100:8488")
                .setInvokeType(InvokeType.CALLBACK)
                .setTimeout(5000);

        HelloService helloService = proxyConfig.getProxy(HelloService.class);
        RpcInvokeContext.getContext().setResponseCallback(new RpcResponseCallBack() {
            @Override
            public void onResponse(Object data) {
                System.out.println("callback :" + data);
            }
            @Override
            public void onException(Throwable throwable) {

            }
        });
        String res = helloService.hello(new HelloObject(1,"hello1"));
        System.out.println("it's null : " + res);



        proxyConfig.setInvokeType(InvokeType.FUTURE);
        helloService = proxyConfig.getProxy(HelloService.class);
        res = helloService.hello(new HelloObject(2,"hello2"));
        System.out.println("it's null : " + res);
        Future future = RpcInvokeContext.getContext().getFuture();
        System.out.println("future : " + future.get());

        proxyConfig.setInvokeType(InvokeType.SYNC);
        helloService = proxyConfig.getProxy(HelloService.class);
        res = helloService.hello(new HelloObject(3,"hello3"));
        System.out.println("sync : " + res);


        proxyConfig.setInvokeType(InvokeType.ONEWAY);
        ByeService byeService = proxyConfig.getProxy(ByeService.class);
        byeService.bye("bye");



        Thread.sleep(5000);
        ChannelPool.close();
        ThreadPoolFactory.close();





    }
}
