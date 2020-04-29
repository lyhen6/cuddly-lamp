package com.ly.lamp.douRpcService.client;

import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T create(Class<T> clz){
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class<?>[]{clz}, new RpcInvoker<T>(clz));
    }
}
