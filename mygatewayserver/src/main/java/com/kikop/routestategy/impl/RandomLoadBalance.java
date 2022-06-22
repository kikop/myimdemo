package com.kikop.routestategy.impl;


import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.routestategy.ILoadBalance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author kikop
 * @version 1.0
 * @project mygatewayserver
 * @file ILoadBalance
 * @desc 随机负载策略
 * 随机策略，从所有可用的 provider 中随机选择一个,参考 Ribbon:RandomRule
 * @date 2020/8/30
 * @time 10:16
 * @by IDE: IntelliJ IDEA
 */
//@Component
//@ConditionalOnProperty(value = "mygw.route.strategy", havingValue = "com.kikop.routestategy.impl.RandomLoadBalance", matchIfMissing = false)
public class RandomLoadBalance implements ILoadBalance {

    /**
     * 从本地缓存中拿到一个远程服务
     *
     * @param allServerList
     * @return
     */
    @Override
    public MyChatServerNode selectAavaliableServer(List<MyChatServerNode> allServerList) {

        MyChatServerNode bestServer = null;
        while (null == bestServer) {
            if (Thread.interrupted()) { // 判断当前线程释放被中断
                return null;
            }
            int serverCount = allServerList.size(); // 所有的远程服务列表
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }
            int index = chooseRandomInt(serverCount);
            bestServer = allServerList.get(index);
            if (null == bestServer) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                // 使当前线程由执行状态，变成为就绪状态，让出 cpu时间
                // 在下一个线程执行时候，此线程有可能被执行，也有可能没有被执行。
                Thread.yield();
                continue;
            }
            return bestServer;
        }
        return bestServer;
    }

    /**
     * 随机选择一个服务
     *
     * @param serverCount
     * @return
     */
    protected int chooseRandomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }

    /**
     * isActive
     * 判读服务示范活着
     *
     * @param currentServer
     * @return
     */
    private boolean isActive(String currentServer) {
        return true;
    }

}
