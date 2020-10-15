package com.pzx.rpc.transport.socket.client;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.SerDeCode;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketReader;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * rpc客户端类，在客户端代理对象中被使用，发送rpc请求信息到服务端，并接受rpc响应信息
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final InetSocketAddress inetSocketAddress;
    private final RpcSerDe rpcSerDe;
    private final ServiceRegistry serviceRegistry;

    public SocketClient(InetSocketAddress inetSocketAddress) {
        this(inetSocketAddress, RpcSerDe.getByCode(DEFAULT_SERDE_CODE), null);
    }

    public SocketClient(InetSocketAddress inetSocketAddress, RpcSerDe rpcSerDe) {
        this(inetSocketAddress, rpcSerDe, null);
    }

    public SocketClient(ServiceRegistry serviceRegistry){
        this(null, RpcSerDe.getByCode(DEFAULT_SERDE_CODE), serviceRegistry);
    }

    public SocketClient(RpcSerDe rpcSerDe, ServiceRegistry serviceRegistry){
        this(null, rpcSerDe, serviceRegistry);
    }

    private SocketClient(InetSocketAddress inetSocketAddress, RpcSerDe rpcSerDe, ServiceRegistry serviceRegistry) {
        this.inetSocketAddress = inetSocketAddress;
        this.rpcSerDe = rpcSerDe;
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        long t = System.currentTimeMillis();
        InetSocketAddress requestAddress = serviceRegistry != null ? serviceRegistry.lookupService(rpcRequest.getInterfaceName()) : inetSocketAddress;
        try (Socket socket = new Socket()) {
            socket.connect(requestAddress);
            logger.info("连接耗时：" + (System.currentTimeMillis() - t));
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ProtocolSocketWriter.write(rpcRequest, outputStream, rpcSerDe);
            outputStream.flush();
            rpcResponse = (RpcResponse) ProtocolSocketReader.read(inputStream);
        } catch (IOException e) {
            logger.error("RpcClient调用sendRequest时有错误发生：", e);
        }
        return rpcResponse;
    }

}
