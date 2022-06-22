package com.kikop.handler.server.model;


import lombok.extern.slf4j.Slf4j;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file MyChatServerManager
 * @desc 原 imwork
 * 每个 MyChatServer 对应一个 MyChatServerNode
 * 单例 MyChatServerManager、单列MyChatServerRoute
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
public class MyChatServerManager {

    private MyChatServerNode myChatServerNode = null;

    private static MyChatServerManager singleInstance = null;

    // 取得单例
    public static MyChatServerManager getInst() {

        if (null == singleInstance) {
            // 构建一个 MyChatServerManager,同时设置该节点的的归属:MyChatServerNode comment by kikop
            singleInstance = new MyChatServerManager();
            singleInstance.myChatServerNode = new MyChatServerNode();
            // 构建 IRegisterCenter
        }
        return singleInstance;
    }

    private MyChatServerManager() {

    }

    public void setMyChatServerNode(String ip, int port, int httpPort) {
        myChatServerNode.setHost(ip);
        myChatServerNode.setPort(port);
        myChatServerNode.setHttpport(httpPort);
    }

    /**
     * 返回本地的节点信息
     *
     * @return 本地的节点信息
     */
    public MyChatServerNode getMyChatServerNode() {
        return myChatServerNode;
    }

}