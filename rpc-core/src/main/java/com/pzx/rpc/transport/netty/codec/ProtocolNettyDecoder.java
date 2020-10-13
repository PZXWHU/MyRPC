package com.pzx.rpc.transport.netty.codec;

import com.pzx.rpc.protocol.ProtocolCoDec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    1 bytes    |     1 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
public class ProtocolNettyDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolNettyDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*
        int magic = in.readInt();
        if(magic != ProtocolConstants.MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        byte packageCode = in.readByte();
        Class<?> packageClass;
        if(packageCode == ProtocolConstants.REQUEST_TYPE) {
            packageClass = RpcRequest.class;
        } else if(packageCode == ProtocolConstants.RESPONSE_TYPE) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = in.readByte();
        RpcSerDe deserializer = RpcSerDe.getByCode(serializerCode);

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = deserializer.deserialize(bytes, packageClass);
        list.add(obj);

         */

        //获得数据包长度以及协议版本
        byteBuf.markReaderIndex();
        int packageLength = byteBuf.readInt();
        byte protocolVersion = byteBuf.readByte();
        byteBuf.resetReaderIndex();

        byte[] protocolBytes = new byte[packageLength];
        byteBuf.readBytes(protocolBytes);

        ProtocolCoDec protocolCoDec = ProtocolCoDec.getByVersion(protocolVersion);
        Object msg = protocolCoDec.decode(protocolBytes);
        list.add(msg);

    }
}
