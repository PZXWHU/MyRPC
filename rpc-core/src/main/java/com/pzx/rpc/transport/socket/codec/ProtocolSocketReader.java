package com.pzx.rpc.transport.socket.codec;

import com.pzx.rpc.protocol.ProtocolCoDec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProtocolSocketReader {

    public static Object read(InputStream inputStream) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int packageLength = dataInputStream.readInt();
        byte protocolVersion = dataInputStream.readByte();

        byte[] protocolBytes = new byte[packageLength];
        byte[] packageLengthBytes = intToBytes(packageLength);
        for(int i = 0; i < packageLengthBytes.length; i++){
            protocolBytes[i] = packageLengthBytes[i];
        }
        protocolBytes[4] = protocolVersion;
        inputStream.read(protocolBytes, 5, packageLength - 5);

        ProtocolCoDec protocolCoDec = ProtocolCoDec.getByVersion(protocolVersion);
        Object msg = protocolCoDec.decode(protocolBytes);

        return msg;
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

}
