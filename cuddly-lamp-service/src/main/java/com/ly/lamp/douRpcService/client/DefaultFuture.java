package com.ly.lamp.douRpcService.client;

import com.ly.lamp.douRpcService.protocol.RpcResponse;

public class DefaultFuture {

    private RpcResponse response;

    private volatile boolean successFlag = false;

    private final Object object = new Object();

    public RpcResponse getResponse(int timeout){

        synchronized (object){
            while (!successFlag){
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    public void setResponse(RpcResponse response){

        if(!successFlag){
            return;
        }

        synchronized (object){
            this.response = response;
            this.successFlag = true;
            object.notify();
        }
    }

}
