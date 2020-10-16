package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloObject;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.service.proxy.ServiceProxy;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class TestSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(TestSocketClient.class);

    public static void main(String[] args) {
        long t = System.currentTimeMillis();

        ServiceRegistry serviceRegistry = new NacosServiceRegistry(new InetSocketAddress("192.168.99.100", 8848));
        SocketClient socketClient = new SocketClient(serviceRegistry);
        ServiceProxy proxy = new ServiceProxy(socketClient);//生成代理工具类实例
        HelloService helloService = proxy.getProxy(HelloService.class);//利用代理工具实例代理服务接口
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

        logger.info("RPC调用耗时：" + (System.currentTimeMillis() - t));

    }
}
