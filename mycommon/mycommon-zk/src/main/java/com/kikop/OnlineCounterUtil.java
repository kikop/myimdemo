package com.kikop;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;


/**
 * 分布式计数器
 * create by 尼恩 @ 疯狂创客圈
 **/
public class OnlineCounterUtil {

    // Zk客户端
    private CuratorFramework curatorFramework = null;

    // 分布式计数器，失败时重试10，每次间隔30毫秒
    DistributedAtomicLong distributedAtomicLong = null;

    private Long curValue;


    public OnlineCounterUtil(String zkConnectString, String onlinecounterpath, int sessionTimeOut) {

        // 1.start
        // 内部启动两个线程
        // 一个用于监听
        // 一个用于后台操作轮询
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .sessionTimeoutMs(sessionTimeOut)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();

        // 2.DistributedAtomicLong构造
        distributedAtomicLong = new DistributedAtomicLong(curatorFramework, onlinecounterpath, new RetryNTimes(10, 30));
    }


    /**
     * 增加计数
     */
    public boolean increment() {
        boolean result = false;
        AtomicValue<Long> val = null;
        try {
            val = distributedAtomicLong.increment();
            result = val.succeeded();
            System.out.println("old cnt: " + val.preValue()
                    + "   new cnt : " + val.postValue()
                    + "  result:" + val.succeeded());
            curValue = val.postValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 减少计数
     */
    public boolean decrement() {
        boolean result = false;
        AtomicValue<Long> val = null;
        try {
            val = distributedAtomicLong.decrement();
            result = val.succeeded();
            System.out.println("old cnt: " + val.preValue()
                    + "   new cnt : " + val.postValue()
                    + "  result:" + val.succeeded());
            curValue = val.postValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


    public Long getCurValue() {
        return curValue;
    }

    public void setCurValue(Long curValue) {
        this.curValue = curValue;
    }
}
