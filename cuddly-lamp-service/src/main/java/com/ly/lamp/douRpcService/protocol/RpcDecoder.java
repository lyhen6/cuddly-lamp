package com.ly.lamp.douRpcService.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clz;

    private Serialization serialization;

    public RpcDecoder(Class<?> clz, Serialization serialization){
        this.clz = clz;
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 4){
            return;
        }

        byteBuf.markReaderIndex();

        int dataLength = byteBuf.readInt();

        if(byteBuf.readableBytes() < dataLength){

            byteBuf.markReaderIndex();

            return;
        }

        byte[] data = new byte[dataLength];

        byteBuf.readBytes(data);

        Object object = serialization.deSerialize(data, clz);

        list.add(object);
    }
}
