package com.kikop.servicediscovery;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file IServiceDiscovery
 * @desc
 * @date 2020/8/29
 * @time 20:09
 * @by IDE: IntelliJ IDEA
 */
public interface IServiceDiscovery {

    /**
     * 获取一个可用的服务
     *
     * @return
     */
    List<String> getSimpleEphmeralNodeAvaliableServiceList();

    <T> List<T> getAvaliableServiceNodeList(String childChatServiceNamePathPrefix, Class<T> clazz);
}
