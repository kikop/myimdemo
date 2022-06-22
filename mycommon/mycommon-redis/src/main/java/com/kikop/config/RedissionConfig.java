package com.kikop.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-redis
 * @file RedissionConfig
 * @desc 单机Redis分布式锁启动类(配置类) todo
 * @date 2021/3/30
 * @time 8:00
 * @by IDE IntelliJ IDEA
 * @reference https://www.jianshu.com/p/59ffff18e1ff
 */
@Configuration
public class RedissionConfig {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * 单机模式
     * 构建Bean RedissonClient
     *
     * @return
     */
    @Bean
    public RedissonClient singleRedissonClient() {
        Config config = new Config();

//        config.useSingleServer().setAddress("redis://ip:port")
//                .setPassword("password")
//                .setDatabase(0);

        String url = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();

        config.useSingleServer().setAddress(String.format("redis://%s:%s",
                redisProperties.getHost(), redisProperties.getPort()))
                .setDatabase(0);

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    /**
     * 哨兵模式
     * 构建 RedissonClient
     *
     * @return
     */
//    @Bean
    public RedissonClient sentinelRedissonClient() {
        Config config = new Config();
        config.useSentinelServers().addSentinelAddress("redis://ip1:port1",
                "redis://ip2:port2",
                "redis://ip3:port3")
                .setMasterName("mymaster")
                .setPassword("password")
                .setDatabase(0);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    /**
     * Cluster集群模式
     * 构建 RedissonClient
     *
     * @return
     */
//    @Bean
    public RedissonClient clusterRedissonClient() {
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .setPassword(redisProperties.getPassword())
                .setScanInterval(5000);

        // 注册集群各个节点
        for (String node : redisProperties.getCluster().getNodes()) {
            clusterServersConfig.addNodeAddress("redis://".concat(node));
        }
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
