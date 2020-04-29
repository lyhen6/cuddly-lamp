package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.protocol.RpcRequest;
import com.ly.lamp.douRpcService.protocol.RpcResponse;

public class Transporters {

    public static RpcResponse send(RpcRequest request){
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8801);

        nettyClient.connect(nettyClient.getInetSocketAddress());

        RpcResponse send = nettyClient.send(request);

        return send;
    }
}
