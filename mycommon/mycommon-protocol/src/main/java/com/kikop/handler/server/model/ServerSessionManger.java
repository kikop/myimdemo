package com.kikop.handler.server.model;

import com.kikop.handler.server.session.IServerSession;
import com.kikop.handler.server.session.impl.LocalSessionImpl;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file ServerSessionManger
 * @desc 服务端的session管理器
 * @date 2022/3/30
 * @time 8:00
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Data
@Repository("serverSessionManger")
public class ServerSessionManger {

    private IChatServerSessionCallback iChatServerSessionCallback;

    private static ServerSessionManger singleInstance = null;


    /*本地用户集合*/
    // key:userId
    // value:UserSessions-->里面有个 map,key:sessionId,value:MyChatServerNode(可能冗余)
    private ConcurrentHashMap<String, UserSessions> localUserMap = new ConcurrentHashMap();

    /*本地会话集合*/
    // key:sessionId
    // value:LocalSessionImpl
    private ConcurrentHashMap<String, LocalSessionImpl> LocalSessionMap = new ConcurrentHashMap();


    /*远程会话集合,todo nb comment by kikop*/
    // key:sessionId
    // value:RemoteSessionImpl
//    private ConcurrentHashMap<String, RemoteSessionImpl> remoteSessionMap = new ConcurrentHashMap();


    public IChatServerSessionCallback getiChatServerSessionCallback() {
        return iChatServerSessionCallback;
    }

    public void setiChatServerSessionCallback(IChatServerSessionCallback iChatServerSessionCallback) {
        this.iChatServerSessionCallback = iChatServerSessionCallback;
    }


    public static ServerSessionManger inst() {
        return singleInstance;
    }

    public static void setSingleInstance(ServerSessionManger singleInstance) {
        ServerSessionManger.singleInstance = singleInstance;
    }

    /**
     * chatclient登录成功之后， 增加 session对象
     */
    public void addLocalSessionImpl(LocalSessionImpl session) {

        // 1. 写入本地会话:LocalSessionMap
        String sessionId = session.getSessionId();
        LocalSessionMap.put(sessionId, session);

        String userid = session.getUser().getUserid();

        if (null != iChatServerSessionCallback) { // 业务端 mychatserver处理
            // 2.增加zk用户数
            // 3.当前服务端的负载
            iChatServerSessionCallback.addLocalSessionImpl(userid, sessionId);
        }
        /**
         * 通知其他节点 todo
         */
        // notifyOtherImNode(session, Notification.SESSION_ON);

    }

    /**
     * 删除本地会话session
     */
    public void removeLocalSessionImpl(String sessionId) {
        if (!LocalSessionMap.containsKey(sessionId)) {
            return;
        }
        // 1.删除本地会话
        LocalSessionImpl session = LocalSessionMap.get(sessionId);
        String userid = session.getUser().getUserid();
        LocalSessionMap.remove(sessionId);

        // 2.删除本地当前用户列表中的指定会话 add by kikop 20220622
        UserSessions userSessions = localUserMap.get(userid);
        if (null != userSessions) {
            userSessions.removeSession(sessionId);
        }
        // 3.业务端 mychatserver处理
        if (null != iChatServerSessionCallback) {
            // 减少zk用户数、减少 当前zk服务节点负载、更新Redis当前用户的会话列表
            iChatServerSessionCallback.removeLocalSessionImpl(userid, sessionId);
        }
        /**
         * 通知其他节点
         */
//        notifyOtherImNode(session, Notification.SESSION_OFF);

    }

//    /**
//     * 通知其他节点
//     *
//     * @param session session
//     * @param type    类型
//     */
//    private void notifyOtherImNode(LocalSessionImpl session, int type) {
//        UserDTO user = session.getUser();
//        RemoteSession remoteSession = RemoteSession.builder()
//                .sessionId(session.getSessionId())
//                .imNode(WorkerRouter.getInst().getLocalNode())
//                .userId(user.getUserId())
//                .valid(true)
//                .build();
//        Notification<RemoteSession> notification = new Notification<RemoteSession>(remoteSession);
//        notification.setType(type);
//        WorkerRouter.getInst().sendNotification(JsonUtil.pojoToJson(notification));
//    }


    /**
     * 根据用户id，获取 所有的 session对象(local、remote)
     *
     * @param userId
     * @return
     */
    public List<IServerSession> getSessionsBy(String userId) {

        List<IServerSession> sessions = new LinkedList<>();
        UserSessions userSessions = loadFromLocalSessionCache(userId);
        if (null == userSessions) {
            return null;
        }
        Map<String, MyChatServerNode> allSession = userSessions.getCurrentSession2ServerNodeMmap();

        allSession.keySet().stream().forEach(sessionId -> {

            // 查找路径1 comment by kikop
            // 首先取得本地的session
            IServerSession session = LocalSessionMap.get(sessionId);

            // 查找路径2  comment by kikop  too
            // 没有命中，取得远程的 session,
//            if (session == null) {
//                session = remoteSessionMap.get(key);
//            }
            if (session == null) {
                log.error("系统异常,本地服务中不存在这样,sessionId:{}", sessionId);

                allSession.keySet().stream().forEach(allsessionId -> {
                    log.error("userSessions.sessionId:{}", sessionId);
                });

                Enumeration<String> keys = LocalSessionMap.keys();
                while (keys.hasMoreElements()) {
                    String s = keys.nextElement();
                    log.error("LocalSessionMap.sessionId:{}-{}", s, LocalSessionMap.get(s).getSessionId());
                }
            } else {
                sessions.add(session);
            }
        });

        return sessions;

    }

    /**
     * 从二级缓存 Redis 加载 未使用
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSessions loadFromRedis(String userId) {
        UserSessions allUserSessionInRedis = iChatServerSessionCallback.getAllSession(userId);
        if (null == allUserSessionInRedis) {
            return null;
        }
        Map<String, MyChatServerNode> map = allUserSessionInRedis.getCurrentSession2ServerNodeMmap();
        map.keySet().stream().forEach(key -> {
            MyChatServerNode node = map.get(key);
            // 当前节点直接忽略 todo
//            if (!node.equals(MyChatServerManager.getInst().getMyChatServerNode())) {
//                remoteSessionMap.put(key, new RemoteSession(key, userId, node));
//            }
        });


        return allUserSessionInRedis;
    }


    /**
     * 本地用户数据从会话中来
     * 加载本地会话中的数据
     * 优先本地用户
     * 从本地用户(登录时还没有数据呢why)中取,没有的化,从本地会话(登录即有值),并拷贝数据到本地用户中
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSessions loadFromLocalSessionCache(String userId) {

        // 1.优先本地用户集合
        UserSessions userSessions = localUserMap.get(userId);
        if (null != userSessions // 问题点,本地用户的数据什么时候删除,好像没有删除操作啊 comment by kikop 20220622
                && null != userSessions.getCurrentSession2ServerNodeMmap()
                && userSessions.getCurrentSession2ServerNodeMmap().keySet().size() > 0) {
            return userSessions;
        }

        // 2.构建本地会话， LocalSessionMap --> localUserMap
        UserSessions newUserSessions = new UserSessions(userId);
        LocalSessionMap.values().stream().forEach(session -> {
            if (userId.equals(session.getUser().getUserid())) {
                newUserSessions.addLocalSession(session);
            }
        });

        // todo
//        remoteSessionMap.values().stream().forEach(session -> {
//            if (userId.equals(session.getUserId())) {
//                newUserSessions.addSession(session.getSessionId(), session.getImNode());
//            }
//        });

        // 3.覆盖本地用户集合
        localUserMap.put(userId, newUserSessions);
        return newUserSessions;
    }


    /**
     * 增加 远程的 session todo
     */
//    public void addRemoteSession(RemoteSession remoteSession) {
//        String sessionId = remoteSession.getSessionId();
//        if (LocalSessionMap.containsKey(sessionId)) {
//            log.error("通知有误，通知到了会话所在的节点");
//            return;
//        }
//
//        remoteSessionMap.put(sessionId, remoteSession);
//        // 删除本地保存的 远程session
//        String uid = remoteSession.getUserId();
//        UserSessions sessions = localUserMap.get(uid);
//        if (null == sessions) {
//            sessions = new UserSessions(uid);
//            localUserMap.put(uid, sessions);
//        }
//        sessions.addSession(sessionId, remoteSession.getImNode());
//    }

    /**
     * 删除 远程的 session
     */
//    public void removeRemoteSession(String sessionId) {
//        if (LocalSessionMap.containsKey(sessionId)) {
//            log.error("通知有误，通知到了会话所在的节点");
//            return;
//        }
//
//        RemoteSession s = remoteSessionMap.get(sessionId);
//        remoteSessionMap.remove(sessionId);
//
//        //删除本地保存的 远程session
//        String uid = s.getUserId();
//        UserSessions sessions = localUserMap.get(uid);
//        sessions.removeSession(sessionId);
//
//    }


    // 关闭连接
    public void closeSession(ChannelHandlerContext ctx) {

        LocalSessionImpl session =
                ctx.channel().attr(LocalSessionImpl.SESSION_KEY).get();

        if (null != session && session.isValid()) {
            session.close();
            // 删除本地会话中的数据
            this.removeLocalSessionImpl(session.getSessionId());
        }
    }


}
