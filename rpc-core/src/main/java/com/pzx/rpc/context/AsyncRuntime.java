package com.pzx.rpc.context;

import com.pzx.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;

/**
 * 异步执行运行时
 *

 */
public class AsyncRuntime {

    private final static Logger LOGGER = LoggerFactory.getLogger(AsyncRuntime.class);

    private static volatile ThreadPoolExecutor asyncThreadPool;

    /**
     * 得到callback用的线程池 默认开始创建
     *
     * @return callback用的线程池
     */
    public static ThreadPoolExecutor getAsyncThreadPool() {
        return getAsyncThreadPool(true);
    }

    /**
     * 得到callback用的线程池
     *
     * @param build 没有时是否构建
     * @return callback用的线程池
     */
    public static ThreadPoolExecutor getAsyncThreadPool(boolean build) {
        if (asyncThreadPool == null && build) {
            synchronized (AsyncRuntime.class) {
                if (asyncThreadPool == null && build) {
                    asyncThreadPool = ThreadPoolFactory.createDefaultPool();
                }
            }
        }
        return asyncThreadPool;
    }
}

