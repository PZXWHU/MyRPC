package com.pzx.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 */
public class SingletonFactory {

    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {}

    public static <T> T getInstance(Class<T> clazz) {
        Object object;
        if ((object = objectMap.get(clazz)) == null){
            synchronized (clazz) {
                if((object = objectMap.get(clazz)) == null) {
                    try {
                        object = clazz.newInstance();
                        objectMap.put(clazz, object);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }

        return clazz.cast(object);//这里没有双重校验锁的重排序问题，因为clazz.cast(object)一定是在object = clazz.newInstance()完成之后再执行。JMM happen-before保证
    }


}
