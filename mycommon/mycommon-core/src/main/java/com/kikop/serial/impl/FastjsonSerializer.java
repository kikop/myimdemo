package com.kikop.serial.impl;

import com.alibaba.fastjson.JSON;
import com.kikop.constants.DataSerialType;
import com.kikop.serial.ISerializer;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file FastjsonSerializer
 * @desc
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class FastjsonSerializer implements ISerializer {

    /**
     * serialize
     * T-->JSON-->toJSONString-->bytes
     *
     * @param obj
     * @param <T>
     * @return
     */
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONString(obj).getBytes();
    }

    /**
     * deserialize
     * bytes-->jsonString-->parseObject-->T
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data), clazz);
    }

    @Override
    public byte getType() {
        return DataSerialType.JSON_SERIAL.code();
    }

    /**
     *
     * @param obj
     * @param <T> 需要转换的泛型类型
     * @return
     */
    @Override
    public <T> String convertToJson(T obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> T convertToPojo(String strJson,Class<T> clazz) {
        return JSON.parseObject(strJson, clazz);
    }
}
