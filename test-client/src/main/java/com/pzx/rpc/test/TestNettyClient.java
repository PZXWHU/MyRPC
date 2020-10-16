package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloObject;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.service.proxy.ServiceProxy;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TestNettyClient {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyClient.class);

    public static void main(String[] args) {
        long t = System.currentTimeMillis();

        ServiceRegistry serviceRegistry = new NacosServiceRegistry(new InetSocketAddress("192.168.99.100", 8848));
        RpcClient rpcClient = new NettyClient(serviceRegistry);
        ServiceProxy serviceProxy = new ServiceProxy(rpcClient);
        HelloService helloService = serviceProxy.getProxy(HelloService.class);

        String res = helloService.hello(new HelloObject(12, "This is a message"));
        System.out.println(res);
        logger.info("RPC调用耗时：" + (System.currentTimeMillis() - t));



    }
}
