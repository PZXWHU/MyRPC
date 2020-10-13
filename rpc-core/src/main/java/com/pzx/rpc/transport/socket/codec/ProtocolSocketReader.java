package com.pzx.rpc.transport.socket.codec;

import com.pzx.rpc.protocol.ProtocolCoDec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProtocolSocketReader {

    public static Object read(InputStream inputStream) throws IOException {

        byte[] len = new byte[4];
        inputStream.read(len);
        int packageLength = ((len[0] << 24) + (len[1] << 16) + (len[2] << 8) + (len[3] << 0));//使用
        byte protocolVersion = (byte)inputStream.read();

        byte[] protocolBytes = new byte[packageLength];
        for(int i = 0; i < len.length; i++){
            protocolBytes[i] = len[i];
        }
        protocolBytes[4] = protocolVersion;
        inputStream.read(protocolBytes, 5, packageLength - 5);

        ProtocolCoDec protocolCoDec = ProtocolCoDec.getByVersion(protocolVersion);
        Object msg = protocolCoDec.decode(protocolBytes);
        return msg;
    }

}
