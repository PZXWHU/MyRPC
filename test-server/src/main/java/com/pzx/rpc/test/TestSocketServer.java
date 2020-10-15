package com.pzx.rpc.test;

import com.pzx.rpc.annotation.ServiceScan;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.transport.socket.server.SocketServer;

import java.net.InetSocketAddress;

@ServiceScan
public class TestSocketServer {

    public static void main(String[] args) {
        /*
        HelloService helloService = new HelloServiceImpl();
        ServiceProvider serviceProvider = new MemoryServiceProvider();
        serviceProvider.addService(helloService, HelloService.class.getCanonicalName());
         */

        SocketServer socketServer = new SocketServer.Builder(new InetSocketAddress("127.0.0.1", 9000)).autoScanService(true).build();
        socketServer.start();



    }
}
