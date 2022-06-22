package com.kikop;

// junit5

import com.kikop.constants.DataSerialType;
import com.kikop.handler.server.model.UserSessions;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserver
 * @file UserSessionsSerialTest
 * @desc
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
public class UserSessionsSerialTest {


    /**
     * testJsonSerial
     *
     * @throws IOException
     */
    @Test
    public void testJsonSerial() throws IOException {

        String strJsonExample = "{\"map\":{\"ffffe44baf8640d0a8093c464cf03618\":{\"balance\":1,\"host\":\"192.168.174.110\",\"httpport\":9095,\"id\":19,\"pathRegister\":\"/mychatserver/seq0000000019\",\"port\":9092}},\"userId\":\"1000\"}";

        // bug
        ISerializer serializer = SerializerManager.getSerializer(DataSerialType.JSON_SERIAL.code());
        UserSessions userSessions = serializer.deserialize(strJsonExample.getBytes(), UserSessions.class);
        System.out.println(userSessions.getCurrentSession2ServerNodeMmap().size());
    }


    /**
     * testJsonSerial
     *
     * @throws IOException
     */
    @Test
    public void testGsonSerial() throws IOException {

        String strJsonExample = "{\"map\":{\"ffffe44baf8640d0a8093c464cf03618\":{\"balance\":1,\"host\":\"192.168.174.110\",\"httpport\":9095,\"id\":19,\"pathRegister\":\"/mychatserver/seq0000000019\",\"port\":9092}},\"userId\":\"1000\"}";

        ISerializer serializer = SerializerManager.getSerializer(DataSerialType.GSON_SERIAL.code());
        UserSessions userSessions = serializer.deserialize(strJsonExample.getBytes(), UserSessions.class);
        System.out.println(userSessions.getCurrentSession2ServerNodeMmap().size());
    }

}
