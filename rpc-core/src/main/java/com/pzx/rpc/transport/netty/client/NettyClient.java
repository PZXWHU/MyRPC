package com.pzx.rpc.transport.netty.client;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyDecoder;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final String host;
    private final int port;
    private final Bootstrap bootstrap;
    private final RpcSerDe rpcSerDe;

    public NettyClient(String host, int port) {
        this(host, port, RpcSerDe.getByCode(DEFAULT_SERDE_CODE));
    }

    public NettyClient(String host, int port, RpcSerDe rpcSerDe) {
        this.host = host;
        this.port = port;
        this.rpcSerDe = rpcSerDe;
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
        try {
            long t1 = System.currentTimeMillis();
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("连接耗时:" + (System.currentTimeMillis() - t1));
            logger.info("客户端连接到服务器 {}:{}", host, port);
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
