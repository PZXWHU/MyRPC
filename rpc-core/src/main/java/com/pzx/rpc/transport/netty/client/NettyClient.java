package com.pzx.rpc.transport.netty.client;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyDecoder;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyEncoder;
import com.pzx.rpc.transport.netty.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final InetSocketAddress serverAddress;
    private final Bootstrap bootstrap;
    private final RpcSerDe rpcSerDe;
    private final ServiceRegistry serviceRegistry;

    public NettyClient(InetSocketAddress serverAddress) {
        this(serverAddress, RpcSerDe.getByCode(DEFAULT_SERDE_CODE), null);
    }

    public NettyClient(InetSocketAddress serverAddress, RpcSerDe rpcSerDe) {
        this(serverAddress, rpcSerDe, null);
    }

    public NettyClient(ServiceRegistry serviceRegistry) {
        this(null, RpcSerDe.getByCode(DEFAULT_SERDE_CODE), serviceRegistry);
    }

    public NettyClient( RpcSerDe rpcSerDe, ServiceRegistry serviceRegistry) {
        this(null, rpcSerDe, serviceRegistry);
    }

    private NettyClient(InetSocketAddress serverAddress, RpcSerDe rpcSerDe, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.rpcSerDe = rpcSerDe;
        this.serviceRegistry = serviceRegistry;
        this.bootstrap = createBootstrap(this.rpcSerDe);
    }

    private Bootstrap createBootstrap(RpcSerDe rpcSerDe){
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ProtocolNettyDecoder())
                                .addLast(new ProtocolNettyEncoder(rpcSerDe))
                                .addLast(new RpcResponseInboundHandler());
                    }
                });

        return bootstrap;
    }


    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest) {

        RpcResponse rpcResponse = null;
        InetSocketAddress requestAddress = serviceRegistry != null ? serviceRegistry.lookupService(rpcRequest.getInterfaceName()) : serverAddress;
        try {
            long t1 = System.currentTimeMillis();
            ChannelFuture future = bootstrap.connect(requestAddress).sync();
            logger.info("连接耗时:" + (System.currentTimeMillis() - t1));
            logger.info("客户端连接到服务器 {}:{}", requestAddress.getAddress(), requestAddress.getPort());
            Channel channel = future.channel();
            if(channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                rpcResponse = channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }

        return rpcResponse;

    }
}
