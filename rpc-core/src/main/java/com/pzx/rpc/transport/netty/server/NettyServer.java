package com.pzx.rpc.transport.netty.server;

import com.pzx.rpc.annotation.Service;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.AbstractRpcServer;
import com.pzx.rpc.transport.RpcServer;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyDecoder;
import com.pzx.rpc.transport.netty.codec.ProtocolNettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final InetSocketAddress inetSocketAddress;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    private final RpcSerDe rpcSerDe;


    private NettyServer(Builder builder){
        this.inetSocketAddress = builder.inetSocketAddress;
        this.serviceRegistry = builder.serviceRegistry;
        this.serviceProvider = builder.serviceProvider == null ? new MemoryServiceProvider() : builder.serviceProvider;
        this.rpcSerDe = builder.rpcSerDe == null ? RpcSerDe.getByCode(DEFAULT_SERDE_CODE) : builder.rpcSerDe;
        if (builder.autoScanService)
            scanAndPublishServices();
    }

    public static class Builder{
        private InetSocketAddress inetSocketAddress;
        private ServiceRegistry serviceRegistry;
        private ServiceProvider serviceProvider;
        private RpcSerDe rpcSerDe;
        private boolean autoScanService = true;

        public Builder(InetSocketAddress inetSocketAddress) {
            this.inetSocketAddress = inetSocketAddress;
        }

        public Builder serviceRegistry(ServiceRegistry serviceRegistry){
            this.serviceRegistry = serviceRegistry;
            return this;
        }

        public Builder serviceProvider(ServiceProvider serviceProvider){
            this.serviceProvider = serviceProvider;
            return this;
        }

        public Builder rpcSerDe(RpcSerDe rpcSerDe){
            this.rpcSerDe = rpcSerDe;
            return this;
        }

        public Builder autoScanService(boolean autoScanService){
            this.autoScanService = autoScanService;
            return this;
        }

        public NettyServer build(){
            return new NettyServer(this);
        }

    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addService(service, serviceName);
        if (serviceRegistry != null)
            serviceRegistry.registerService(serviceName, inetSocketAddress);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
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
            ChannelFuture future = serverBootstrap.bind(inetSocketAddress.getPort()).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
