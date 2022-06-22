package com.kikop.serial;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-core
 * @file RpcServerInitializer
 * @desc 序列化为String(可阅读), Byte
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public interface ISerializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);

    byte getType();

    <T> String convertToJson(T obj);

    <T> T convertToPojo(String strJson, Class<T> clazz);
}
