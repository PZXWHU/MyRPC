package com.pzx.rpc.invoke;

import com.pzx.rpc.context.AsyncRuntime;
import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.ResponseCode;
import com.pzx.rpc.enumeration.RpcError;
import com.pzx.rpc.exception.RpcException;
import com.pzx.rpc.factory.ThreadPoolFactory;
import com.pzx.rpc.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureInvocationHandler extends AbstractInvocationHandler {

    private final static Logger logger = LoggerFactory.getLogger(FunctionalInterface.class);

    public FutureInvocationHandler(RpcClient rpcClient, long timeout) {
        super(rpcClient, timeout);
    }

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture =  rpcClient.sendRequest(rpcRequest);

        CompletableFuture<Object> dataFuture = resultFuture.thenApplyAsync(rpcResponse -> rpcResponse.getData(),
                AsyncRuntime.getAsyncThreadPool());
        RpcInvokeContext.getContext().setFuture(dataFuture);



        return null;
    }
}
