package com.pzx.rpc.transport.netty.server;

import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.transport.RpcServer;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyDecoder;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final ServiceProvider serviceProvider;
    private final RpcSerDe rpcSerDe;

    public NettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.rpcSerDe = RpcSerDe.getByCode(DEFAULT_SERDE_CODE);
    }

    public NettyServer(ServiceProvider serviceProvider, RpcSerDe rpcSerDe) {
        this.serviceProvider = serviceProvider;
        this.rpcSerDe = rpcSerDe;
    }


    @Override
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtocolNettyEncoder(rpcSerDe));
                            pipeline.addLast(new ProtocolNettyDecoder());
                            pipeline.addLast(new RpcRequestInboundHandler(serviceProvider));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
