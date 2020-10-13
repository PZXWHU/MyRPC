package com.pzx.rpc.service.handler;

import com.pzx.rpc.entity.RpcRequest;
import com.pzx.rpc.entity.RpcResponse;
import com.pzx.rpc.enumeration.ResponseCode;
import com.pzx.rpc.service.register.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 利用RpcRequest对应的Service实例，调用相应函数获得结果，并构造RpcResponse
 */
public class ServiceRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRequestHandler.class);

    public RpcResponse<Object> handle(RpcRequest rpcRequest, ServiceRegistry serviceRegistry) {
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        return invokeTargetMethod(rpcRequest, service);
    }

    private RpcResponse invokeTargetMethod(RpcRequest rpcRequest, Object service){
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
