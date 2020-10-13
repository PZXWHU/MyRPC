package com.pzx.rpc.service.register;

/**
 * 服务注册接口
 */
public interface ServiceRegistry {

    <T> void registerService(T service);

    Object getService(String serviceName);

}
