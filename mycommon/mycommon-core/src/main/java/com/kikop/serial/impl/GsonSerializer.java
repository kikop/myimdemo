package com.kikop.serial.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kikop.constants.DataSerialType;
import com.kikop.serial.ISerializer;

import java.io.UnsupportedEncodingException;

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
public class GsonSerializer implements ISerializer {

    // 谷歌 GsonBuilder 构造器
    static GsonBuilder gsonBuilder = new GsonBuilder();

    private static final Gson gson;

    static {
        //不需要html escape
        gsonBuilder.disableHtmlEscaping();
        gson = gsonBuilder.create();
    }

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
        String json = gson.toJson(obj);
        try {
            return json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
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

        return gson.fromJson(new String(data), clazz);
    }

    @Override
    public byte getType() {
        return DataSerialType.GSON_SERIAL.code();
    }

    /**
     * @param obj
     * @param <T> 需要转换的泛型类型
     * @return
     */
    @Override
    public <T> String convertToJson(T obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T convertToPojo(String strJson, Class<T> clazz) {
        return gson.fromJson(strJson, clazz);
    }
}
