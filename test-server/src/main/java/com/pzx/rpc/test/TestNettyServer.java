package com.pzx.rpc.test;

import com.pzx.rpc.annotation.ServiceScan;
import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.enumeration.SerDeCode;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.service.registry.NacosServiceRegistry;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.netty.server.NettyServer;

import java.net.InetSocketAddress;

@ServiceScan
public class TestNettyServer {

    public static void main(String[] args) {

        NettyServer server = new NettyServer.Builder(new InetSocketAddress("127.0.0.1",9999))
                .autoScanService(true)
                .build();
        server.start();




    }

}
