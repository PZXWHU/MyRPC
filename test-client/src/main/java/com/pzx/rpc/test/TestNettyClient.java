package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloObject;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.InvokeType;
import com.pzx.rpc.invoke.RpcResponseCallBack;
import com.pzx.rpc.service.proxy.ProxyConfig;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class TestNettyClient {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyClient.class);

    public static void main(String[] args) throws Exception {

        ProxyConfig proxyConfig = new ProxyConfig()
                .setDirectServerUrl("127.0.0.1:9999")
                .setInvokeType(InvokeType.CALLBACK)
                .setTimeout(5000);
        RpcInvokeContext.getContext().setResponseCallback(new RpcResponseCallBack() {
            @Override
            public void onResponse(RpcResponse rpcResponse) {
                System.out.println(rpcResponse.getData() + " 这是异步获取的");
            }

            @Override
            public void onServerException(RpcResponse rpcResponse) {

            }

            @Override
            public void onClientException(Throwable throwable) {

            }
        });
        HelloService helloService = proxyConfig.getProxy(HelloService.class);
        String res = helloService.hello(new HelloObject(12,"dasdsa"));
        System.out.println(res);


        proxyConfig.setInvokeType(InvokeType.FUTURE);
        helloService = proxyConfig.getProxy(HelloService.class);
        res = helloService.hello(new HelloObject(13,"dasdsa"));
        System.out.println(res);
        Future future = RpcInvokeContext.getContext().getFuture();
        System.out.println(future.get()+ "这是future获取的");

        proxyConfig.setInvokeType(InvokeType.SYNC);
        helloService = proxyConfig.getProxy(HelloService.class);
        res = helloService.hello(new HelloObject(14,"dasdsa"));
        System.out.println(res + "这是同步获取的");


        System.out.println(RpcInvokeContext.uncompletedFutures.size());




    }
}
