package com.kikop.config;

import com.kikop.registercenter.IRegisterCenter;
import com.kikop.registercenter.impl.ZkRegisterCenterImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkUserServerRegisterCenterConfig {

    @Value("${myuser.zk.addr}")
    private String zkConnectString;

    @Value("${myuser.zk.sessiontimeout}")
    private int sessionTimeOut;


    @Value("${myuser.zk.rootpath}")
    private String rootpath;

    @Bean
    public IRegisterCenter iRegisterCenter() {
        // ZkRegisterCenterImpl在其余的Jar中,一开始没有在容器中,动态将Java pojo注册到容器
        IRegisterCenter iRegisterCenter = new ZkRegisterCenterImpl(zkConnectString, rootpath, sessionTimeOut);
        return iRegisterCenter;
    }

}