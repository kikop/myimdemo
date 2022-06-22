package com.kikop.serial;

import com.kikop.serial.impl.FastjsonSerializer;
import com.kikop.serial.impl.GsonSerializer;
import com.kikop.serial.impl.JdkSerializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file SerializerManager
 * @desc 序列化管理器
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class SerializerManager {

    // 序列化列表
    private final static ConcurrentHashMap<Byte, ISerializer> serializers = new ConcurrentHashMap<Byte, ISerializer>();

    static {
        ISerializer jsonSerializer = new FastjsonSerializer(); // fastjson
        ISerializer javaSerializer = new JdkSerializer(); // JDK
        ISerializer gsonSerializer=new GsonSerializer(); // gson
        serializers.put(jsonSerializer.getType(), jsonSerializer);
        serializers.put(javaSerializer.getType(), javaSerializer);
        serializers.put(gsonSerializer.getType(),gsonSerializer);
    }

    public static ISerializer getSerializer(byte key) {

        ISerializer serializer = serializers.get(key);
        if (serializer == null) { // 没找到默认JDK序列化
            return new JdkSerializer();
        }
        return serializer;
    }
}
