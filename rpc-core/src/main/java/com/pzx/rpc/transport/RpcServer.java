package com.pzx.rpc.transport;

import com.pzx.rpc.enumeration.SerDeCode;

public interface RpcServer {

    int DEFAULT_SERDE_CODE = SerDeCode.valueOf("KRYO").getCode();

    void start(int port);
}
