package com.pzx.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  RpcError {

    UNKNOWN_SERDE("不识别的的序列化器/反序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包"),
    UNKNOWN_PROTOCOL("不识别的通信协议"),
    SERIALIZE_FAIL("序列化失败"),
    DESERIALIZE_FAIL("反序列化失败"),
    SERVICE_NOT_FOUND("未找到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口");

    private final String message;
}
