package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.protocol.RpcRequest;
import com.ly.lamp.douRpcService.protocol.RpcResponse;

import java.net.InetSocketAddress;

public interface Client {

    RpcResponse send(RpcRequest request);

    void connect(InetSocketAddress socketAddress);

    InetSocketAddress getInetSocketAddress();

    void close();
}
