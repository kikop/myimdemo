//package com.kikop.handler.server;
//
//import com.kikop.dto.po.LoginUser;
//import com.kikop.handler.server.model.ServerSession;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * @author kikop
// * @version 1.0
// * @project mycommon-protocol
// * @file OnLineServerSessions
// * @desc 在线用户会话列表
// * @date 2021/12/27
// * @time 9:30
// * @by IDE IntelliJ IDEA
// */
//@Slf4j
//public class OnLineServerSessions {
//
//
//    private static OnLineServerSessions onLineServerSessions = new OnLineServerSessions();
//
//    public static OnLineServerSessions getInstance() {
//        return onLineServerSessions;
//    }
//
//    // 在线的客户单连接上下文
//    // 考虑多个终端的情况,多个相同的UserId对应不同的channel
//    // key:
//    // 1.channel.id().asLongText(),局限性,适用于只有一台 Netty服务器
//    // 2.userID
//    // 3.sessionId(Good)
//    // value:
//    // 1.ChannelHandlerContext
//    // 2.serverSession(Good)
//    public ConcurrentHashMap<String, ServerSession> map = new ConcurrentHashMap<String, ServerSession>();
//
//
//    /**
//     * 增加 session对象
//     */
//    public void addSession(
//            String sessionId, ServerSession s) {
//        map.put(sessionId, s);
//        log.info("用户登录:id= " + s.getLoginUser().getUserid()
//                + "   在线总数: " + map.size());
//
//    }
//
//    /**
//     * 获取session对象
//     */
//    public ServerSession getSession(String sessionId) {
//        if (map.containsKey(sessionId)) {
//            return map.get(sessionId);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 根据用户id，获取session对象
//     */
//    public List<ServerSession> getSessionsBy(String userId) {
//
//        List<ServerSession> list = map.values()
//                .stream()
//                .filter(s -> s.getLoginUser().getUserid().equals(userId))
//                .collect(Collectors.toList());
//        return list;
//    }
//
//    /**
//     * 删除session
//     */
//    public void removeSession(String sessionId) {
//        if (!map.containsKey(sessionId)) {
//            return;
//        }
//        ServerSession s = map.get(sessionId);
//        map.remove(sessionId);
//        log.info("用户下线:id= " + s.getLoginUser().getUserid()
//                + "   在线总数: " + map.size());
//    }
//
//
//    public boolean hasLogin(LoginUser loginUser) {
//        Iterator<Map.Entry<String, ServerSession>> it =
//                map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, ServerSession> next = it.next();
//            LoginUser loginUserCache = next.getValue().getLoginUser();
//            if (loginUserCache.getUserid().equals(loginUser.getUserid())
//                    && loginUserCache.getPlatform().equals(loginUser.getPlatform())) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//}