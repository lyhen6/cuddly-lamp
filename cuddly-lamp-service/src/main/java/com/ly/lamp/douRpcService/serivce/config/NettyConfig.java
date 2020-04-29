package com.ly.lamp.douRpcService.serivce.config;

import com.ly.lamp.douRpcService.serivce.netty.ChannelRepository;
import com.ly.lamp.douRpcService.serivce.netty.handler.ServerInitialize;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(NettyProperties.class)
public class NettyConfig {


    @Autowired
    private NettyProperties nettyProperties;

    @Autowired
    private ServerInitialize serverInitialize;


    @Bean
    public ServerBootstrap serverBootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverInitialize);

        Map<ChannelOption<?>, Object> channelOptionObjectMap = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = channelOptionObjectMap.keySet();

        for(ChannelOption channelOption : keySet){
            serverBootstrap.option(channelOption, tcpChannelOptions().get(channelOption));
        }

        return serverBootstrap;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }


    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }

    @Bean
    public Map<ChannelOption<?>, Object> tcpChannelOptions(){
        Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
        optionObjectMap.put(ChannelOption.SO_BACKLOG, nettyProperties.getBackLog());
        return optionObjectMap;
    }

    @Bean
    public InetSocketAddress tcpSocketAddress(){
        return new InetSocketAddress(nettyProperties.getTcpPort());
    }

    @Bean
    public ChannelRepository channelRepository(){
        return new ChannelRepository();
    }

}


