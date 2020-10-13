package com.pzx.rpc.transport.socket.codec;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.protocol.ProtocolCoDec;
import com.pzx.rpc.protocol.ProtocolConstants;
import com.pzx.rpc.serde.RpcSerDe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProtocolSocketWriter {

    public static void write(Object object, OutputStream outputStream, RpcSerDe rpcSerDe) throws IOException {
        byte protocolVersion = ProtocolConstants.NEWEST_VERSION;
        ProtocolCoDec protocolCoDec = ProtocolCoDec.getByVersion(protocolVersion);
        outputStream.write(protocolCoDec.encode(object, rpcSerDe));
    }

}
