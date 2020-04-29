package com.ly.lamp.douRpcService.serivce.netty.handler;

import com.ly.lamp.douRpcService.protocol.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerInitialize extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0 ,4));
        channelPipeline.addLast(new RpcEncoder(RpcResponse.class, new JsonSerialization()));
        channelPipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonSerialization()));
        channelPipeline.addLast(serverHandler);

    }
}
