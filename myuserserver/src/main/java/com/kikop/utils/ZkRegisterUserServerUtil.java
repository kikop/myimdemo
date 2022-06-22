package com.kikop.utils;

import com.kikop.registercenter.IRegisterCenter;
import com.kikop.utils.ip.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author kikop
 * @version 1.0
 * @project myuserserver
 * @file RegisterUserService
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ZkRegisterUserServerUtil {


    @Value("${server.port}")
    private int httpport;


    @Autowired
    private IRegisterCenter iRegisterCenter;

    /**
     * 注册当前服务到zk
     */
    public void registerUserServerInfoToZk() {

        // 永久结点: /myimdemo/myuserserver
        // 临时结点: /myimdemo/myuserserver/127.0.0.1:7082
        // [zk: localhost:2181(CONNECTED) 65] ls /myimdemo/myuserserver/127.0.0.1:7082
        // [127.0.0.1:7082]


        // 127.0.0.1
        String hostAddress = IpUtil.getIp();
        // 127.0.0.1:8080
        String serviceValue = String.format("%s:%d", hostAddress, httpport);
        iRegisterCenter.registerSimpleEphmeralNode(serviceValue);
    }
}
