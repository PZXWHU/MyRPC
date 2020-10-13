package com.pzx.rpc.transport.socket.server;

import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.transport.AbstractRpcServer;
import com.pzx.rpc.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * rpc服务端类，持有服务注册表，接收RPC请求对象。
 */
public class SocketServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;
    private final RpcSerDe rpcSerDe;

    public SocketServer() {
        this(RpcSerDe.getByCode(DEFAULT_SERDE_CODE), true);
    }

    public SocketServer(RpcSerDe rpcSerDe) {
        this(rpcSerDe, true);
    }

    public SocketServer(boolean autoScan) {
        this(RpcSerDe.getByCode(DEFAULT_SERDE_CODE), autoScan);
    }

    public SocketServer(RpcSerDe rpcSerDe, boolean autoScan) {
        this.serviceProvider = new MemoryServiceProvider();
        this.rpcSerDe = rpcSerDe;
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), Executors.defaultThreadFactory());
        scanAndPublishServices();
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addService(service, serviceName);
    }

    @Override
    public void start(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, serviceProvider, rpcSerDe));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }


}
