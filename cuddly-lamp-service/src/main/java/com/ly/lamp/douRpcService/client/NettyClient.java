package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

public class NettyClient implements Client {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private ClientHandler clientHandler;

    private String host;

    private int port;

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse send(final RpcRequest request) {
        try {
            channel.writeAndFlush(request).wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @Override
    public void connect(final InetSocketAddress socketAddress) {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0 ,4));
                        channelPipeline.addLast(new RpcEncoder(RpcResponse.class, new JsonSerialization()));
                        channelPipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonSerialization()));
                        channelPipeline.addLast(clientHandler);
                    }
                });

        try {
            bootstrap.connect(socketAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}
