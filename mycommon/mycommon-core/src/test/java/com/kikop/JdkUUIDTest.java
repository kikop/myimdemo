package com.kikop;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class JdkUUIDTest {

    @Test
    public void testUUID() {
        String uuid = java.util.UUID.randomUUID().toString();
        String s = uuid.replaceAll("-", "");
        System.out.println(s.getBytes().length);
    }
}
