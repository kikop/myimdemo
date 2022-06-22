package com.kikop;

import com.kikop.utils.ZkRegisterUserServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @author kikop
 * @version 1.0
 * @project myuserserver
 * @file MyUserServerApplication
 * @desc
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
// 注解扫描多个包下示例,内嵌包中有@Component注解,需开启如下内容
//@ComponentScan({"com.kikop"})
public class MyUserServerApplication implements CommandLineRunner {

    @Autowired
    public ZkRegisterUserServerUtil zkRegisterUserServerUtil;

    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyUserServerApplication.class, args);

        log.info("{} 服务启动成功!", MyUserServerApplication.class.getSimpleName());
    }

    @Override
    public void run(String... args) throws Exception {
        // 注册当前服务到zk
        zkRegisterUserServerUtil.registerUserServerInfoToZk();
    }
}
