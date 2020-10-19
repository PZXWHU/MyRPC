package com.pzx.rpc.test;

import com.pzx.rpc.entity.RpcResponse;

import com.pzx.rpc.enumeration.RpcError;
import com.pzx.rpc.exception.RpcException;
import com.pzx.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class TestSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(TestSocketClient.class);

    public static void main(String[] args) throws Exception {

        new Thread(()->f("hh")).start();
        new Thread(()->f("hh")).start();
    }

    public static void f(String s) {
        synchronized (s){
            try {
                System.out.println(s);
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
