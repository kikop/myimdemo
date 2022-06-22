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
 * @project mygatewayserver
 * @file MyGatewayServerApplication
 * @desc 网关服务
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class MyGatewayServerApplication implements CommandLineRunner {

    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(MyGatewayServerApplication.class, args);
        log.info("{} 服务启动成功!", MyGatewayServerApplication.class.getSimpleName());
    }

    /**
     * Spring容器启动后,执行的额外初始化方法
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) {
    }
}
