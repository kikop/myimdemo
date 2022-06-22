package com.kikop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication

@RestController
// 注解扫描多个包下示例,内嵌包中有@Component注解,需开启如下内容
//@ComponentScan({"com.kikopxxx", "com.kikop"})
public class MyOfflineServerApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyOfflineServerApplication.class, args);

        //    Object myDog = configurableApplicationContext.getBean("myDog");

        System.out.println("------");

    }

}
