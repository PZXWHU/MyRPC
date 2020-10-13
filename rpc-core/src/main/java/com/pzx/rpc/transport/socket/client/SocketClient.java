package com.pzx.rpc.transport.socket.client;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.SerDeCode;
import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.transport.RpcClient;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketReader;
import com.pzx.rpc.transport.socket.codec.ProtocolSocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * rpc客户端类，在客户端代理对象中被使用，发送rpc请求信息到服务端，并接受rpc响应信息
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;
    private final RpcSerDe rpcSerDe;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.rpcSerDe = RpcSerDe.getByCode(DEFAULT_SERDE_CODE);
    }

    public SocketClient(String host, int port, RpcSerDe rpcSerDe) {
        this.host = host;
        this.port = port;
        this.rpcSerDe = rpcSerDe;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        long t = System.currentTimeMillis();
        try (Socket socket = new Socket(host, port)) {
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
