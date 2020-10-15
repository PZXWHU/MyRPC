package com.pzx.rpc.transport.socket.server;

import com.pzx.rpc.serde.RpcSerDe;
import com.pzx.rpc.service.provider.MemoryServiceProvider;
import com.pzx.rpc.service.provider.ServiceProvider;
import com.pzx.rpc.service.registry.ServiceRegistry;
import com.pzx.rpc.transport.AbstractRpcServer;
import com.pzx.rpc.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
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
    private final InetSocketAddress inetSocketAddress;
    private ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    private final RpcSerDe rpcSerDe;

    private SocketServer(Builder builder){
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), Executors.defaultThreadFactory());
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

        public SocketServer build(){
            return new SocketServer(this);
        }

    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addService(service, serviceName);
        if (serviceRegistry != null)
            serviceRegistry.registerService(serviceName, inetSocketAddress);
    }

    @Override
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(inetSocketAddress.getPort())) {

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
