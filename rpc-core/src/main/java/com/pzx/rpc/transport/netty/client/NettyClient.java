package com.pzx.rpc.transport.netty.client;

import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.ResponseCode;
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
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final InetSocketAddress serverAddress;
    private final RpcSerDe rpcSerDe;
    private final ServiceRegistry serviceRegistry;

    public NettyClient(InetSocketAddress serverAddress) {
        this(serverAddress,  null);
    }

    public NettyClient(ServiceRegistry serviceRegistry) {
        this(null,  serviceRegistry);
    }

    private NettyClient(InetSocketAddress serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.rpcSerDe = RpcSerDe.getByCode(DEFAULT_SERDE_CODE);
        this.serviceRegistry = serviceRegistry;

    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {

        InetSocketAddress requestAddress = serviceRegistry != null ? serviceRegistry.lookupService(rpcRequest.getInterfaceName()) : serverAddress;
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            Channel channel = ChannelPool.get(requestAddress, rpcSerDe);
            RpcInvokeContext.putUncompletedFuture(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFuture future1) -> {
                if(future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.complete(RpcResponse.fail(rpcRequest.getRequestId(), ResponseCode.METHOD_INVOKER_FAIL, future1.cause().toString()));
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }

        return resultFuture;

    }
}
