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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

public class CallbackInvocationHandler extends AbstractInvocationHandler {

    private final static Logger logger = LoggerFactory.getLogger(CallbackInvocationHandler.class);

    public CallbackInvocationHandler(RpcClient rpcClient, long timeout) {
        super(rpcClient, timeout);
    }

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture =  rpcClient.sendRequest(rpcRequest);

        RpcResponseCallBack rpcResponseCallBack = RpcInvokeContext.getContext().getResponseCallback();
        if (rpcResponseCallBack != null){
            resultFuture.whenCompleteAsync((RpcResponse rpcResponse, Throwable throwable)->{
                if (throwable != null)
                    rpcResponseCallBack.onClientException(throwable);
                else {
                    if (rpcResponse.getStatusCode() == ResponseCode.METHOD_INVOKER_SUCCESS.getCode())
                        rpcResponseCallBack.onResponse(rpcResponse);
                    else
                        rpcResponseCallBack.onServerException(rpcResponse);
                }
            }, AsyncRuntime.getAsyncThreadPool());
        }

        return null;
    }
}
