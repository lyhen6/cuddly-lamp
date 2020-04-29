package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.annotation.RpcInterface;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class RpcConfig implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Reflections reflections = new Reflections("com.ly.lamp");
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        for(Class<?> clz : reflections.getTypesAnnotatedWith(RpcInterface.class)){
            defaultListableBeanFactory.registerSingleton(clz.getSimpleName(), ProxyFactory.create(clz));
        }
        log.info(" afterPropertiesSet is {} ", reflections.getTypesAnnotatedWith(RpcInterface.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
