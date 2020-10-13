package com.pzx.rpc.test;

import com.pzx.rpc.api.HelloService;
import com.pzx.rpc.enumeration.SerDeCode;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.transport.netty.server.NettyServer;

public class TestNettyServer {

    public static void main(String[] args) {
        ServiceProvider serviceProvider = new MemoryServiceProvider();
        HelloService helloService = new HelloServiceImpl();
        serviceProvider.addService(helloService, HelloService.class.getCanonicalName());
        RpcSerDe rpcSerDe = RpcSerDe.getByCode(SerDeCode.valueOf("JSON").getCode());
        NettyServer server = new NettyServer(serviceProvider, rpcSerDe);
        server.start(9999);
    }

}
