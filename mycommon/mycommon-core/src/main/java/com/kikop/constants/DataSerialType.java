package com.kikop.constants;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-core
 * @file DataSerialType
 * @desc 序列化类型
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public enum DataSerialType {

    JSON_SERIAL((byte) 0),
    JAVA_SERIAL((byte) 1),
    GSON_SERIAL((byte) 2);

    private byte code;

    DataSerialType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }
}
