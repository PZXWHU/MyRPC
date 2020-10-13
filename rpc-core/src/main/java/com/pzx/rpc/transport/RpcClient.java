package com.pzx.rpc.transport;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.SerDeCode;

public interface RpcClient {

    int DEFAULT_SERDE_CODE = SerDeCode.valueOf("KRYO").getCode();

    RpcResponse sendRequest(RpcRequest rpcRequest);
}
