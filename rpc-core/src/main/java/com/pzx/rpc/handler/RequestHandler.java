package com.pzx.rpc.handler;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RpcResponse<Object> handle(RpcRequest rpcRequest, Object service) {
        return invokeTargetMethod(rpcRequest, service);
    }

    private RpcResponse<Object> invokeTargetMethod(RpcRequest rpcRequest, Object service){

        if (service == null)
            return RpcResponse.fail(ResponseCode.CLASS_NOT_FOUND);

        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return RpcResponse.success(result);
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }catch (IllegalAccessException | InvocationTargetException e){
            return RpcResponse.fail(ResponseCode.METHOD_INVOKER_FAIL);
        }

    }

}
