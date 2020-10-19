package com.pzx.rpc.invoke;

import com.pzx.rpc.context.RpcInvokeContext;
import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.transport.RpcClient;

public class OneWayInvocationHandler extends AbstractInvocationHandler {

    public OneWayInvocationHandler(RpcClient rpcClient, long timeout) {
        super(rpcClient, timeout);
    }

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest) {
        rpcClient.sendRequest(rpcRequest);
        RpcInvokeContext.removeUncompletedFuture(rpcRequest.getRequestId());//因为rpcClient.sendRequest会将Future加入RpcInvokeContext.uncompletedFutures
        return null;
    }
}
