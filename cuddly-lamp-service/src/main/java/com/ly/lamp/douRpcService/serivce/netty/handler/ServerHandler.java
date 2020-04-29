package com.ly.lamp.douRpcService.serivce.netty.handler;

import com.ly.lamp.douRpcService.protocol.RpcRequest;
import com.ly.lamp.douRpcService.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            Object handler = handler(rpcRequest);
            response.setResult(handler);
        } catch (Throwable throwable) {
            response.setThrowable(throwable);
            throwable.printStackTrace();
        }
        channelHandlerContext.writeAndFlush(response);
    }

    private Object handler(RpcRequest request) throws Throwable{

        Class<?> clz = Class.forName(request.getClassName());

        Object serviceBean = applicationContext.getBean(clz);

        Class<?> serviceClass = serviceBean.getClass();

        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(request.getClassName(), request.getParameterTypes());
        return fastMethod.invoke(serviceBean, request.getParameters());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
