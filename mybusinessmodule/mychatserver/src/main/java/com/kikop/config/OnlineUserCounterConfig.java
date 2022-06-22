package com.kikop.config;

import com.kikop.OnlineCounterUtil;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 分布式计数器
 * create by 尼恩 @ 疯狂创客圈
 **/
@Configuration
public class OnlineUserCounterConfig {

    @Value("${myserver.zk.addr}")
    private String zkConnectString;


    @Value("${myserver.zk.sessiontimeout}")
    private int sessionTimeOut;

    @Value("${myserver.zk.onlinecounter}")
    private String onlinecounterpath;


    @Bean
    public OnlineCounterUtil onlineCounterUtil() {
        OnlineCounterUtil onlineCounterUtil = new OnlineCounterUtil(zkConnectString, onlinecounterpath, sessionTimeOut);
        return onlineCounterUtil;

    }

}
