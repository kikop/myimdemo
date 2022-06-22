package com.kikop.config;

import com.kikop.routestategy.ILoadBalance;
import com.kikop.routestategy.impl.LoopLoadBalance;
import com.kikop.routestategy.impl.PayLoadBalance;
import com.kikop.routestategy.impl.RandomLoadBalance;
import com.kikop.servicediscovery.IServiceDiscovery;
import com.kikop.servicediscovery.impl.ServiceDiscoveryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatServerLoadBanalceConfig {

    @Value("${mygw.loadbalance}")
    private String loadbalance;

    @Bean
    public ILoadBalance iLoadBalance() {
        ILoadBalance iLoadBalance = new PayLoadBalance();
        if (LoopLoadBalance.class.getName().equalsIgnoreCase(loadbalance)) {
            iLoadBalance = new LoopLoadBalance();
        } else if (RandomLoadBalance.class.getName().equalsIgnoreCase(loadbalance)) {
            iLoadBalance = new RandomLoadBalance();
        }
        return iLoadBalance;
    }
}
