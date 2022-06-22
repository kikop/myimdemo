package com.kikop.config;

import com.kikop.servicediscovery.IServiceDiscovery;
import com.kikop.servicediscovery.impl.ServiceDiscoveryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkChatServerServiceDiscoveryConfig {

    @Value("${mygw.zk.addr}")
    private String zkConnectString;


    @Value("${mygw.zk.sessiontimeout}")
    private int sessionTimeOut;


    @Value("${myserver.zk.rootpath}")
    private String rootpath;

    @Bean
    public IServiceDiscovery iServiceDiscovery() {
        IServiceDiscovery iServiceDiscovery = new ServiceDiscoveryImpl(zkConnectString, rootpath, sessionTimeOut);
        return iServiceDiscovery;
    }
}
