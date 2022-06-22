package com.kikop.config;

import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.registercenter.IRegisterCenter;
import com.kikop.registercenter.impl.ZkRegisterCenterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
@Configuration
public class ZkChatServerRegisterCnterConfig {


    @Value("${myserver.zk.addr}")
    private String zkConnectString;

    @Value("${myserver.zk.sessiontimeout}")
    private int sessionTimeOut;


    @Value("${myserver.zk.rootpath}")
    private String rootpath;


    @Bean
    public IRegisterCenter iRegisterCenter() {

        IRegisterCenter iRegisterCenter = new ZkRegisterCenterImpl(zkConnectString, rootpath, sessionTimeOut);
        return iRegisterCenter;
    }


}
