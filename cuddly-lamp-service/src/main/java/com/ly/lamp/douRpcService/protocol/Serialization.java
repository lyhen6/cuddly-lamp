package com.ly.lamp.douRpcService.protocol;


/**
 *  确定序列化协议
 */
public interface Serialization {

    /**
     *  实现序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 发序列化
     * @param data
     * @param clz
     * @param <T>
     * @return
     */
    <T> T deSerialize(byte[] data, Class<T> clz);
}
