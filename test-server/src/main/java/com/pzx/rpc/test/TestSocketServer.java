package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.transport.socket.server.SocketServer;

public class TestSocketServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        ServiceProvider serviceProvider = new MemoryServiceProvider();
        serviceProvider.addService(helloService, HelloService.class.getCanonicalName());
        SocketServer socketServer = new SocketServer(serviceProvider);
        socketServer.start(9000);
    }
}
