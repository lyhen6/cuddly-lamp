package com.ly.lamp.douRpcService.protocol;

import lombok.Data;

@Data
public class RpcRequest {

    /**
     *  调用编号
     */
    private String requestId;

    /**
     *  调用的类名
     */
    private String className;

    /**
     *  调用的方法名
     */
    private String methodName;

    /**
     *  请求的参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     *  请求的参数
     */
    private Object[] parameters;

}
