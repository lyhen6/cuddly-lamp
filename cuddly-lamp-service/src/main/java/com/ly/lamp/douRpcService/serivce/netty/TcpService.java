package com.ly.lamp.douRpcService.serivce.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Data
@Component
public class TcpService {

    private final ServerBootstrap serverBootstrap;

    private final InetSocketAddress inetSocketAddress;

    public TcpService(ServerBootstrap serverBootstrap, InetSocketAddress inetSocketAddress){
        this.serverBootstrap = serverBootstrap;
        this.inetSocketAddress = inetSocketAddress;
    }

    private Channel serverChannel;

    public void start() throws InterruptedException {
        serverBootstrap.bind(inetSocketAddress).sync().channel().closeFuture().channel();
    }


    @PreDestroy
    public void stop(){
        if(serverChannel != null){
            serverChannel.close();
            serverChannel.parent().close();
        }
    }

}
