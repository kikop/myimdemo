//package com.kikop.handler.server.model;
//
//import com.kikop.dto.po.LoginUser;
//import com.kikop.handler.server.OnLineServerSessions;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.util.AttributeKey;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 实现服务器Socket Session会话
// * 作废
// */
//@Data
//@Slf4j
//public class ServerSession {
//
//
//    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("user_id");
//
//    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");
//
//    /**
//     * session中存储的session 变量属性值
//     */
//    private Map<String, Object> concurrentHashMap = new ConcurrentHashMap<String, Object>();
//
//
//    /**
//     * 用户实现服务端会话管理的核心
//     */
//
//    // 通道
//    private Channel channel;
//
//    // 用户
//    private LoginUser loginUser;
//
//    // session唯一标示
//    private final String sessionId;
//
//    // 登录状态
//    private boolean isLogin = false;
//
//
//    public ServerSession(Channel channel) {
//        this.channel = channel;
//        this.sessionId = buildNewSessionId();
//    }
//
//    // 反向导航
//    // ctx-->IServerSession
//    public static ServerSession getSession(ChannelHandlerContext ctx) {
//        Channel channel = ctx.channel();
//        return channel.attr(ServerSession.SESSION_KEY).get();
//    }
//
//    // 关闭连接
//    public static void closeSession(ChannelHandlerContext ctx) {
//        ServerSession session =
//                ctx.channel().attr(ServerSession.SESSION_KEY).get();
//
//        if (null != session && session.isValid()) {
//            session.close();
//            OnLineServerSessions.getInstance().removeSession(session.getSessionId());
//        }
//    }
//
//    // 和 channel 通道实现双向绑定
//    public ServerSession bind() {
//        log.info(" IServerSession 绑定会话成功 " + channel.remoteAddress());
//        channel.attr(ServerSession.SESSION_KEY).set(this);
//        OnLineServerSessions.getInstance().addSession(getSessionId(), this);
//        isLogin = true;
//        return this;
//    }
//
//    public ServerSession unbind() {
//        isLogin = false;
//        OnLineServerSessions.getInstance().removeSession(getSessionId());
//        this.close();
//        return this;
//    }
//
//    public String getSessionId() {
//        return sessionId;
//    }
//
//    /**
//     * buildNewSessionId
//     *
//     * @return
//     */
//    private static String buildNewSessionId() {
//        String uuid = UUID.randomUUID().toString();
//        return uuid.replaceAll("-", "");
//    }
//
//    public void set(String key, Object value) {
//        concurrentHashMap.put(key, value);
//    }
//
//
//    public <T> T get(String key) {
//        return (T) concurrentHashMap.get(key);
//    }
//
//
//    public boolean isValid() {
//        return getLoginUser() != null ? true : false;
//    }
//
//    // 写 Protobuf数据帧
//    public synchronized void writeAndFlush(Object pkg) {
//        channel.writeAndFlush(pkg);
//    }
//
//    // 关闭连接
//    public synchronized void close() {
//        ChannelFuture future = channel.close();
//        future.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (!future.isSuccess()) {
//                    log.error("CHANNEL_CLOSED error ");
//                }
//            }
//        });
//    }
//
//
//    public LoginUser getLoginUser() {
//        return loginUser;
//    }
//
//    public void setLoginUser(LoginUser loginUser) {
//        this.loginUser = loginUser;
//        loginUser.setSessionId(sessionId);
//    }
//
//
//}
