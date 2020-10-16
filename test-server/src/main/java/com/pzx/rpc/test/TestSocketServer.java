package com.pzx.rpc.test;

import com.pzx.rpc.annotation.ServiceScan;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.enumeration.SerDeCode;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.socket.server.SocketServer;

import java.net.InetSocketAddress;

@ServiceScan
public class TestSocketServer {

    public static void main(String[] args) {

        SocketServer socketServer = new SocketServer.Builder(new InetSocketAddress("127.0.0.1", 9000))
                .serviceProvider(new MemoryServiceProvider())
                .serviceRegistry(new NacosServiceRegistry(new InetSocketAddress("192.168.99.100", 8848)))
                .rpcSerDe(RpcSerDe.getByCode(SerDeCode.valueOf("KRYO").getCode()))
                .autoScanService(true)
                .build();
        socketServer.start();



    }
}
