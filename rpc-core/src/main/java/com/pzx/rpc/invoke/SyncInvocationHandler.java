package com.pzx.rpc.invoke;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.RpcError;
import com.pzx.rpc.exception.RpcException;
import com.pzx.rpc.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncInvocationHandler extends AbstractInvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(SyncInvocationHandler.class);

    public SyncInvocationHandler(RpcClient rpcClient, long timeout) {
        super(rpcClient, timeout);
    }

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = null;
        CompletableFuture<RpcResponse> resultFuture =  rpcClient.sendRequest(rpcRequest);
        try {
            rpcResponse = resultFuture.get(timeout, TimeUnit.MILLISECONDS);
        }catch (TimeoutException e){
            logger.error("RPC调用超时 ：" + rpcRequest);
        }catch (InterruptedException | ExecutionException e) {
            logger.error("RPC调用失败 ：" + e);
            throw new RpcException(RpcError.RPC_INVOKER_FAILED);
        }
        return rpcResponse;
    }
}
