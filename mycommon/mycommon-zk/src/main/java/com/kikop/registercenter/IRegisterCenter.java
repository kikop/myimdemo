package com.kikop.registercenter;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file IRegisterCenter
 * @desc 基于zk的注册中心
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
public interface IRegisterCenter {

    /**
     * 普通服务注册临时节点
     *
     * @param servieAddress 127.0.0.1:8080:9090
     */
    void registerSimpleEphmeralNode(String servieAddress);

    /**
     * 服务注册临时顺序节点
     *
     * @param childChatServiceNamePathPrefix mychatserver-seq
     * @param payLoadObj                     127.0.0.1:8080:9090
     */
    String registerEphmeralSeqNode(String childChatServiceNamePathPrefix, Object payLoadObj);

    /**
     * 更新节点负载
     *
     * @param pathRegistered
     * @param payLoadObj
     */
    void updateServerNodePayLoad(String pathRegistered, Object payLoadObj);
}
