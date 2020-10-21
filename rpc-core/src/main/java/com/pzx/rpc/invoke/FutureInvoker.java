package com.pzx.rpc.invoke;

import com.pzx.rpc.context.AsyncRuntime;
import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.RpcError;
import com.pzx.rpc.exception.RpcException;
import com.pzx.rpc.factory.ThreadPoolFactory;
import com.pzx.rpc.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureInvoker extends AbstractInvoker {

    private final static Logger logger = LoggerFactory.getLogger(FutureInvoker.class);

    public FutureInvoker(RpcClient rpcClient, long timeout) {
        super(rpcClient, timeout);
    }

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest) {

        CompletableFuture<Object> contextFuture = new CompletableFuture<>();
        RpcInvokeContext.getContext().setFuture(contextFuture);

        AsyncRuntime.getAsyncThreadPool().submit(()->{
            CompletableFuture<RpcResponse> resultFuture =  rpcClient.sendRequest(rpcRequest);
            //FutureInvoke ： 在 RpcInvokeContext设置调用结果Future
            resultFuture.whenCompleteAsync((rpcResponse, throwable)->{
                checkRpcServerError(rpcRequest, rpcResponse);
                if (rpcResponse != null)
                    contextFuture.complete(rpcResponse.getData());
                else
                    contextFuture.completeExceptionally(throwable);
            }, AsyncRuntime.getAsyncThreadPool());
        });
        setTimeoutCheckAsync(rpcRequest, timeout);

        return RpcResponse.EMPTY_RESPONSE;
    }

}
