package com.kikop.utils;

import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.registercenter.IRegisterCenter;
import com.kikop.registercenter.impl.ZkRegisterCenterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author kikop
 * @version 1.0
 * @project myuserserver
 * @file RegisterUserService
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ZkRegisterChatServerUtil2 {


    @Value("${server.port}")
    private int httpport;

    @Value("${myserver.socketport}")
    private String socketport;


    @Value("${myserver.zk.childChatServiceNamePathPrefix}")
    private String childChatServiceNamePathPrefix;


    @Autowired
    private IRegisterCenter iRegisterCenter;


    /**
     * 在 zookeeper中创建临时顺序节点
     *
     * @return
     */
    public String registerChatServerInfoToZk() {

        // 1.构建 MyChatServerManager单例
        MyChatServerNode myChatServerNode = MyChatServerManager.getInst().getMyChatServerNode();


        // 创建一个 ZNode 节点
        // 节点的 payload 为当前 myChatServerNode 实例
        // String pathRegistered = iRegisterCenter.registerEphmeralSeqNode(rootpath + "/" + childChatServiceNamePathPrefix, myChatServerNode);
        String pathRegistered = iRegisterCenter.registerEphmeralSeqNode(childChatServiceNamePathPrefix, myChatServerNode);
        if (null == pathRegistered) {
            log.error("init {} error", childChatServiceNamePathPrefix);
        }
        myChatServerNode.setPathRegister(pathRegistered);

        // 设置 myChatServerNode id
        long idByPath = getIdByPath(pathRegistered);
        myChatServerNode.setId(idByPath);
        return pathRegistered;

    }

    /**
     * 增加负载，表示有用户登录成功
     *
     * @return 成功状态
     */
    public boolean incBalance(String pathRegistered, MyChatServerNode myChatServerNode) {
        if (null == myChatServerNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        // 增加负载：并写回zookeeper
        while (true) {
            try {
                myChatServerNode.incrementBalance();
                iRegisterCenter.updateServerNodePayLoad(pathRegistered, myChatServerNode);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 减少负载，表示有用户下线，写回zookeeper
     *
     * @return 成功状态
     */
    public boolean decrBalance(String pathRegistered, MyChatServerNode myChatServerNode) {
        if (null == myChatServerNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        while (true) {
            try {
                myChatServerNode.decrementBalance();
                iRegisterCenter.updateServerNodePayLoad(pathRegistered, myChatServerNode);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 取得IM 节点编号
     *
     * @param pathRegistered 路径
     * @return 编号
     */
    public long getIdByPath(String pathRegistered) {
        String sid = null;
        if (null == pathRegistered) {
            throw new RuntimeException("节点路径有误");
        }
        int index = pathRegistered.lastIndexOf(childChatServiceNamePathPrefix);
        if (index >= 0) {
            index += childChatServiceNamePathPrefix.length();
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("节点ID获取失败");
        }

        return Long.parseLong(sid);
    }


}
