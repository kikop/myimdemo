package com.kikop.service;

import com.kikop.OnlineCounterUtil;
import com.kikop.handler.server.model.IChatServerSessionCallback;
import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.handler.server.model.UserSessions;
import com.kikop.utils.ZkRegisterChatServerUtil2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserver
 * @file ServerSessionHookImpl
 * @desc 服务端的session的构子管理器
 * @date 2022/6/17
 * @time 8:00
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ChatServerSessionCallback implements IChatServerSessionCallback {

    // 服务端会话UserSessions
    // 缓存Redis的接口
    @Autowired
    private ChatServerSessionDao chatServerSessionDao;

    @Autowired
    public OnlineCounterUtil onlineCounterUtil;

    @Autowired
    public ZkRegisterChatServerUtil2 zkRegisterChatServerUtil2;

    @Override
    public void addLocalSessionImpl(String userId, String sessionId) {

        // 1.增加zk用户数
        onlineCounterUtil.increment();

        // 2.增加 mychatservernode 负载
        MyChatServerNode myChatServerNode = MyChatServerManager.getInst().getMyChatServerNode();
        zkRegisterChatServerUtil2.incBalance(myChatServerNode.getPathRegister(), myChatServerNode);

        // 3.增加 redis 用户的session 信息到缓存
        // redis缓存在线用户列表
        chatServerSessionDao.addCacheUserSession(userId, sessionId);

        log.info("本地session增加：在线用户总数:{} ", onlineCounterUtil.getCurValue());
    }

    /**
     * 减少zk用户数、减少 当前zk服务节点负载、更新Redis当前用户的会话列表
     *
     * @param userId
     * @param sessionId
     */
    @Override
    public void removeLocalSessionImpl(String userId, String sessionId) {

        // 1.减少用户数
        onlineCounterUtil.decrement();


        // 2.减少 mychatservernode负载
        MyChatServerNode myChatServerNode = MyChatServerManager.getInst().getMyChatServerNode();
        zkRegisterChatServerUtil2.decrBalance(myChatServerNode.getPathRegister(), myChatServerNode);

        // 3.更新Redis当前用户的会话列表
        chatServerSessionDao.removeUserSession(userId, sessionId);

        log.info("本地session减少：在线用户总数:{} ", onlineCounterUtil.getCurValue());
    }

    /**
     * 获取 Redis中的所有用户 session
     *
     * @param userId
     * @return
     */
    @Override
    public UserSessions getAllSession(String userId) {
        UserSessions allUserSessions = chatServerSessionDao.getAllSession(userId);
        return allUserSessions;
    }


}
