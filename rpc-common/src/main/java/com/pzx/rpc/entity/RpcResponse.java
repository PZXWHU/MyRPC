package com.pzx.rpc.entity;

import com.pzx.rpc.enumeration.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc服务端返回给rpc客户端的响应对象
 * 包含三个元素：
 * 1、响应状态码
 * 2、响应状态信息
 * 3、响应数据
 */
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(ResponseCode.SUCCESS.getCode());
        rpcResponse.setMessage(ResponseCode.SUCCESS.getMessage());
        rpcResponse.setData(data);
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(code.getCode());
        rpcResponse.setMessage(code.getMessage());
        return rpcResponse;
    }


}
