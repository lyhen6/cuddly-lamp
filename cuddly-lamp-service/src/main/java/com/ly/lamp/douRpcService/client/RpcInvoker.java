package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RpcInvoker<T> implements InvocationHandler {

    private Class<T> clz;

    public RpcInvoker(Class<T> clz){
        this.clz = clz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();

        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        return Transporters.send(request).getResult();
    }
}
