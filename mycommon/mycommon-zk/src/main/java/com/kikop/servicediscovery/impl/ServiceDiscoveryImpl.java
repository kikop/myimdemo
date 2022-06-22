package com.kikop.servicediscovery.impl;

import com.kikop.constants.DataSerialType;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import com.kikop.servicediscovery.IServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file ServiceDiscoveryImpl
 * @desc
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
public class ServiceDiscoveryImpl implements IServiceDiscovery {

    /**
     * 缓存所有用于负载均衡的服务列表
     */
    private List<String> avaliableServiceList = new ArrayList<>();

    // 避免重复监听
    private HashSet<String> watchedPaths = new HashSet<String>();

    private String connectString = "";

    // 服务名
    private String serviceName = "";

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }


    public CuratorFramework curatorFramework = null;

    /**
     * @param connectString
     * @param route
     * @param sessionTimeoutMs 4000 15000
     */
    public ServiceDiscoveryImpl(String connectString, String route, int sessionTimeoutMs) {

        this.connectString = connectString;
        this.serviceName = route;
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
    }


    /**
     * 获取一个可用的服务
     *
     * @return
     */
    @Override
    public List<String> getSimpleEphmeralNodeAvaliableServiceList() {
        try {
            // 获取servicename完成路径
            String serviceFullPath = this.serviceName;

            // 1.获取可用服务列表
            this.avaliableServiceList = curatorFramework.getChildren().forPath(serviceFullPath);

            if (watchedPaths.contains(serviceFullPath)) {
                // 2.通过zk节点的 watch机制实现服务端 servicename 监听
                registerWatch(serviceFullPath);
            }
            return this.avaliableServiceList;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取可用服务异常,{}", e);
        }
        return null;

    }

    /**
     * 获取所有可用的 mychatserver-->mychatservernode
     *
     * @param childChatServiceNamePathPrefix mychatserver-seq
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> getAvaliableServiceNodeList(String childChatServiceNamePathPrefix, Class<T> clazz) {
        try {
            List<T> mychatserverlist = new ArrayList<T>();

            // 1.获取可用服务列表
            List<String> serverList = curatorFramework.getChildren().forPath(this.serviceName);

            // 2.遍历符合条件的数据
            for (String currentServer : serverList) {
                if (currentServer.indexOf(childChatServiceNamePathPrefix) == -1) {
                    continue;
                }
                log.info("currentServer:{}", currentServer);
                byte[] payload = null;
                try {
                    payload = curatorFramework.getData().forPath(this.serviceName + "/" + currentServer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == payload) {
                    continue;
                }
                ISerializer fastJsonSerializer = SerializerManager.getSerializer(DataSerialType.JSON_SERIAL.code());
                T currentServerInstance = fastJsonSerializer.deserialize(payload, clazz);
                mychatserverlist.add(currentServerInstance);
            }
            return mychatserverlist;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取可用服务异常,{}", e);
        }
        return null;
    }

    /**
     * 通过zk节点的 watch机制实现服务端监听
     * synchronized确保线程安全
     *
     * @param serviceFullPath
     */
    private synchronized void registerWatch(String serviceFullPath) {
        try {
            watchedPaths.add(serviceFullPath);
            //  1.cacheData:是否缓存数据
            PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, serviceFullPath, true);

            // 2.创建监听
            PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    avaliableServiceList = curatorFramework.getChildren().forPath(serviceFullPath);
                    log.info("avaliableServiceList:{}", avaliableServiceList);
                }
            };
            // 3.注册监听
            // 追加时是否需要清除监听,watch机制只监听一次
            pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("注册 PathChildrenCacheListener 异常" + e);
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
