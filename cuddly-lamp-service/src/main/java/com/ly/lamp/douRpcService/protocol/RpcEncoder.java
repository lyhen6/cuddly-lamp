package com.ly.lamp.douRpcService.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clz;

    private Serialization serialization;

    public RpcEncoder(Class<?> clz, Serialization serialization){
        this.clz = clz;
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(clz != null){
           byte[] bytes = serialization.serialize(o);
           byteBuf.writeByte(bytes.length);
           byteBuf.writeBytes(bytes);
        }
    }
}
