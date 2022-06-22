package com.kikop.serial.impl;


import com.kikop.constants.DataSerialType;
import com.kikop.serial.ISerializer;

import java.io.*;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file JdkSerializer
 * @desc
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class JdkSerializer implements ISerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream =
                    new ObjectOutputStream(byteArrayOutputStream);

            outputStream.writeObject(obj);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(byteArrayInputStream);

            return (T) objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte getType() {
        return DataSerialType.JAVA_SERIAL.code();
    }

    /**
     * convertPojoToJson
     *
     * @param obj
     * @param <T> 需要转换的泛型类型
     * @return
     */
    @Override
    public <T> String convertToJson(T obj) {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream =
                    new ObjectOutputStream(byteArrayOutputStream);

            outputStream.writeObject(obj);

            return byteArrayOutputStream.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String();
    }

    @Override
    public <T> T convertToPojo(String strJson, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(strJson.getBytes());
        try {
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(byteArrayInputStream);

            return (T) objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
