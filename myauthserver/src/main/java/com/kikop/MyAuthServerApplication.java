package com.kikop;

import com.alibaba.fastjson.JSONObject;
import com.kikop.servicediscovery.IServiceDiscovery;
import com.kikop.servicediscovery.impl.ServiceDiscoveryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file MyAuthServerApplication
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class MyAuthServerApplication implements CommandLineRunner {


    @Value("${server.port}")
    private Integer serverPort;


    @Value("${myauth.zk.switch}")
    private Boolean zkSwitch;

    @Value("${myauth.userserver.url}")
    private String userServiceUrl;


    @Value("${myauth.zk.addr}")
    private String zkConnectString;


    @Value("${myauth.zk.sessiontimeout}")
    private int sessionTimeOut;


    @Value("${myuser.zk.rootpath}")
    private String rootpath;



    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) {

        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyAuthServerApplication.class, args);

        log.info("{} 服务启动成功!", MyAuthServerApplication.class.getSimpleName());

    }

    @Override
    public void run(String... args) throws Exception {
//        testUserLogin();
//        getAvaliableUserService();
    }

    private void testUserLogin() {
        JSONObject reqParam = new JSONObject();
        reqParam.put("userId", 1000);
        reqParam.put("userName", "kikop1000");
        String reqUrl = String.format("http://localhost:%d%s/authc/userLogin", serverPort, serverContextPath);
        JSONObject jsonObjectResponseEntity = restTemplate.postForObject(reqUrl, reqParam
                , JSONObject.class);
        /*
        {"access_jwttoken":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxMDAwLCJ1c2VyX2tleSI6IjUzYjQ0NjYxLTkzYjYtNGRjNy1iMTk3LWM0NTI0ODIwYjFmNyIsInVzZXJuYW1lIjoiMTAwMFRlc3QifQ.wyvJ3sG-h_wk6y8xfmb0lYhdHLueEiy5BrrgLqnCUWC4viY67WXADTjViaxKZ-9921dGVSvYVkTXUACbDfuuKw"
                ,"loginUser":{"userid":1000,"username":"1000Test"}
                ,"expires_in":720
        }
        */
        System.out.println(jsonObjectResponseEntity);
    }

    /**
     * 获取一个可用的用户服务列表
     *
     * @return 127.0.0.1:7082,127.0.0.1:7083
     */
    private String getAvaliableUserService() {

        if (true == zkSwitch) {
            String strListOfServers = "";
            IServiceDiscovery iServiceDiscovery = new ServiceDiscoveryImpl(zkConnectString, rootpath, sessionTimeOut);
            List<String> myuserserverList = iServiceDiscovery.getSimpleEphmeralNodeAvaliableServiceList();

            for (String server : myuserserverList) {
                strListOfServers += server + ",";
            }
            if (!"".equalsIgnoreCase(strListOfServers)) {
                strListOfServers = strListOfServers.substring(0, strListOfServers.length() - 1);
            }
            System.out.println("strListOfServers:" + strListOfServers);
            return strListOfServers;
        }
        return userServiceUrl;
    }
}
