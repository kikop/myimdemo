package com.kikop.core.RpcAsyncProcess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RequestHolder
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class RequestHolder {

    /**
     * 全局请求消息ID
     * 局限:同一个JVM
     */
    public static final AtomicLong REQUEST_ID=new AtomicLong();

    /**
     * 请求缓存
     * key:long
     * value:RpcFuture
     */
    public static final Map<Long,RpcFuture> REQUEST_MAP=new ConcurrentHashMap<>();
}
