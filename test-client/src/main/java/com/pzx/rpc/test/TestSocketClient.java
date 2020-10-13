package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloObject;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.service.proxy.ServiceProxy;
import com.pzx.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(TestSocketClient.class);

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        SocketClient socketClient = new SocketClient("127.0.0.1", 9000);
        //生成代理工具类实例
        ServiceProxy proxy = new ServiceProxy(socketClient);
        //利用代理工具实例代理服务接口
        HelloService helloService = proxy.getProxy(HelloService.class);

        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        logger.info("RPC调用耗时：" + (System.currentTimeMillis() - t));

    }
}
