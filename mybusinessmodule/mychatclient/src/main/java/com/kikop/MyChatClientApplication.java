package com.kikop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file MyNettyClientApplication
 * @desc
 * @date 2022/3/13
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class MyChatClientApplication implements CommandLineRunner {

    // http://localhost:6001/mychatclient/chatclientc/sendRequest2Gateway
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyChatClientApplication.class, args);
        log.info("{} 服务启动成功!", MyChatClientApplication.class.getSimpleName());
    }

    /**
     * Spring容器初始化好以后,执行的动作
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        // 1.获取本节的IP地址
//        String hostAddress = IpUtil.getIp();
//        System.out.println(hostAddress);

    }


}