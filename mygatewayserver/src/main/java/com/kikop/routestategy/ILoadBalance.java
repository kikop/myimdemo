package com.kikop.routestategy;

import com.kikop.handler.server.model.MyChatServerNode;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project mygatewaryserver
 * @file ILoadBalance
 * @desc 负载均衡接口
 * @date 2020/8/30
 * @time 10:16
 * @by IDE: IntelliJ IDEA
 */
public interface ILoadBalance {

    /**
     * 获取一个可用的服务
     *
     * @param repos
     * @return
     */
    MyChatServerNode selectAavaliableServer(List<MyChatServerNode> repos);
}
