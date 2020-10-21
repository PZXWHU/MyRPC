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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestNettyClient {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyClient.class);

    public static void main(String[] args) throws InterruptedException {

        int i = 0;

        ProxyConfig proxyConfig = new ProxyConfig()
                .setDirectServerUrl("127.0.0.1:9999")
                //.setRegistryCenterUrl("nacos://192.168.99.100:8488")
                .setInvokeType(InvokeType.CALLBACK)
                .setTimeout(5000);

        HelloService helloService;
        String res;



        while (i < 2){
            proxyConfig.setInvokeType(InvokeType.CALLBACK);
            RpcInvokeContext.getContext().setResponseCallback(new RpcResponseCallBack() {
                @Override
                public void onResponse(Object data) {
                    System.out.println("callback :" + data);
                }
                @Override
                public void onException(Throwable throwable) {
                    logger.error("callback :Rpc调用出错: " + throwable.toString());
                }
            });
            HelloService helloService1 = proxyConfig.getProxy(HelloService.class);
            String res1 = helloService1.hello(new HelloObject(i++,"hello" + 1));
            System.out.println("it's null : " + res1);

            proxyConfig.setInvokeType(InvokeType.FUTURE);
            helloService = proxyConfig.getProxy(HelloService.class);
            res = helloService.hello(new HelloObject(i++,"hello" + 1));
            System.out.println("it's null : " + res);
            Future future = RpcInvokeContext.getContext().getFuture();
            try {
                System.out.println("future : " + future.get());
            }catch (ExecutionException e){
                logger.error("future : Rpc调用出错:" + e);
            }

            proxyConfig.setInvokeType(InvokeType.SYNC);
            helloService = proxyConfig.getProxy(HelloService.class);
            res = helloService.hello(new HelloObject(i++,"hello" + 1));
            System.out.println("sync : " + res);


            proxyConfig.setInvokeType(InvokeType.ONEWAY);
            ByeService byeService = proxyConfig.getProxy(ByeService.class);
            byeService.bye("bye");

        }





        Thread.sleep(5000);
        ChannelPool.close();
        ThreadPoolFactory.close();





    }
}
