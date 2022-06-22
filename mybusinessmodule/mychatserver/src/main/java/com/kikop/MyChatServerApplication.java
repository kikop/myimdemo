package com.kikop;

import com.kikop.handler.server.model.ServerSessionManger;
import com.kikop.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserer
 * @file MyChatServerApplication
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
// 注解扫描多个包下示例,内嵌包中有@Component注解,需开启如下内容
// @ComponentScan({"com.kikopxxx", "com.kikop"})
public class MyChatServerApplication implements CommandLineRunner {

    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyChatServerApplication.class, args);
        log.info("{} 服务启动成功!", MyChatServerApplication.class.getSimpleName());
    }

    @Override
    public void run(String... args) throws Exception {

        ServerSessionManger serverSessionMangerSingleTon = SpringUtils.getBean(ServerSessionManger.class);
        ServerSessionManger.setSingleInstance(serverSessionMangerSingleTon);
    }
}