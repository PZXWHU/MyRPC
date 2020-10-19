package com.pzx.rpc.invoke;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.transport.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class AbstractInvocationHandler implements InvocationHandler {


    protected final RpcClient rpcClient;
    protected final long timeout;

    public AbstractInvocationHandler(RpcClient rpcClient, long timeout){
        this.rpcClient = rpcClient;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(RpcRequest.nextRequestId())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcResponse rpcResponse = doInvoke(rpcRequest);
        return rpcResponse == null ? null : rpcResponse.getData();
    }

    abstract protected RpcResponse doInvoke(RpcRequest rpcRequest);
}
