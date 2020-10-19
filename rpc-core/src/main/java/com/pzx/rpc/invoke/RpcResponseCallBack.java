package com.pzx.rpc.invoke;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;

public interface RpcResponseCallBack {


    void onResponse(RpcResponse rpcResponse);

    void onServerException(RpcResponse rpcResponse);

    void onClientException(Throwable throwable);


}
