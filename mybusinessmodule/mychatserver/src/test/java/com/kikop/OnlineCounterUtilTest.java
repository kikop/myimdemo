package com.kikop;

// junit5

import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.utils.ZkRegisterChatServerUtil2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserver
 * @file OnlineCounterUtilTest
 * @desc
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
// RunWith 适用于 only for junit4
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = OnlineCounterUtilTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest
public class OnlineCounterUtilTest {


    @Autowired
    private OnlineCounterUtil onlineCounterUtil;

    @Test
    public void contextLoads() {
        System.out.println("contextLoads");
    }

    /**
     * testOnlineCounterUtil
     *
     * @throws IOException
     */
    @Test
    public void testOnlineCounterUtil() throws IOException {
        boolean increment = onlineCounterUtil.increment();
        System.out.println("getCurValue:" + onlineCounterUtil.getCurValue());
    }


}
