package com.pzx.rpc.transport.socket.server;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.factory.SingletonFactory;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.handler.ServiceRequestHandler;

import com.pzx.rpc.service.register.ServiceRegistry;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketReader;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class SocketRequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private final Socket socket;
    private final ServiceRequestHandler serviceRequestHandler;
    private final ServiceRegistry serviceRegistry;
    private final RpcSerDe rpcSerDe;

    public SocketRequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RpcSerDe rpcSerDe) {
        this.socket = socket;
        this.serviceRequestHandler = SingletonFactory.getInstance(ServiceRequestHandler.class);
        this.serviceRegistry = serviceRegistry;
        this.rpcSerDe = rpcSerDe;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ProtocolSocketReader.read(inputStream);
            RpcResponse<Object> rpcResponse = serviceRequestHandler.handle(rpcRequest, serviceRegistry);
            ProtocolSocketWriter.write(rpcResponse, outputStream, rpcSerDe);
            outputStream.flush();
            socket.close();
            logger.info("RPC调用完成: {}:{}", socket.getInetAddress(), socket.getPort());
        } catch (IOException e) {
            logger.error("RPC调用失败：发送时有错误发生：", e);
        }
    }
}
