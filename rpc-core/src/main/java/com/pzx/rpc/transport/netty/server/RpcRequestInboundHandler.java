package com.pzx.rpc.transport.netty.server;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.factory.SingletonFactory;
import com.pzx.rpc.service.handler.ServiceRequestHandler;
import com.pzx.rpc.service.register.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRequestInboundHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestInboundHandler.class);
    private final ServiceRequestHandler serviceRequestHandler;
    private final ServiceRegistry serviceRegistry;

    public RpcRequestInboundHandler(ServiceRegistry serviceRegistry) {
        this.serviceRequestHandler = SingletonFactory.getInstance(ServiceRequestHandler.class);
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        logger.info("服务器接收到请求: {}", rpcRequest);
        RpcResponse rpcResponse = serviceRequestHandler.handle(rpcRequest, serviceRegistry);
        ChannelFuture future = ctx.channel().writeAndFlush(rpcResponse);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}