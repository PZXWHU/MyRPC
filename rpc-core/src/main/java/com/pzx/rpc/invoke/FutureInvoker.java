package com.pzx.rpc.invoke;

import com.pzx.rpc.context.AsyncRuntime;
import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
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
        CompletableFuture<RpcResponse> resultFuture =  rpcClient.sendRequest(rpcRequest);

        //FutureInvoke ： 在 RpcInvokeContext设置调用结果Future
        CompletableFuture<Object> dataFuture = resultFuture.thenApplyAsync(rpcResponse -> {
            checkRpcResponse(rpcResponse);
            return rpcResponse.getData();
        }, AsyncRuntime.getAsyncThreadPool());
        RpcInvokeContext.getContext().setFuture(dataFuture);

        ////超时清除resultFuture
        ThreadPoolFactory.getScheduledThreadPool().schedule(()->{
            CompletableFuture completableFuture;
            if ((completableFuture = RpcInvokeContext.removeUncompletedFuture(rpcRequest.getRequestId())) != null){
                completableFuture.complete(RpcResponse.EMPTY_RESPONSE);
                logger.error("Rpc调用超时：" + rpcRequest);
            }
        }, timeout, TimeUnit.MILLISECONDS);

        return RpcResponse.EMPTY_RESPONSE;
    }

}
