package com.kikop.handler.server.session.impl;

import com.kikop.constants.DataSerialType;
import com.kikop.constants.ServerConstants;
import com.kikop.dto.po.LoginUser;
import com.kikop.handler.server.model.ServerSessionManger;
import com.kikop.handler.server.session.IServerSession;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file LocalSessionImpl
 * @desc
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
@Data
@Slf4j
public class LocalSessionImpl implements IServerSession {

    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<LocalSessionImpl> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    public static ISerializer fastJsonSerial = SerializerManager.getSerializer(DataSerialType.JSON_SERIAL.code());

    /**
     * session中存储的session 变量属性值
     * key:sessionId
     * value:
     */
    private Map<String, Object> concurrentHashMap = new ConcurrentHashMap<String, Object>();

    // 通道
    private Channel channel;

    // 用户
    private LoginUser loginUser;

    // session唯一标示
    private final String sessionId;

    // 登录状态
    private boolean isLogin = false;


    public LocalSessionImpl(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    // 反向导航
    public static LocalSessionImpl getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(LocalSessionImpl.SESSION_KEY).get();
    }


    // session和 channel 通道实现双向绑定
    public LocalSessionImpl bind() {
        log.info(" LocalSessionImpl 绑定会话 " + channel.remoteAddress());
        channel.attr(LocalSessionImpl.SESSION_KEY).set(this);
        channel.attr(ServerConstants.CHANNEL_NAME).set(fastJsonSerial.convertToJson(loginUser));
        isLogin = true;
        return this;
    }

    public LocalSessionImpl unbind() {
        isLogin = false;
        ServerSessionManger.inst().removeLocalSessionImpl(getSessionId());
        this.close();
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    /**
     * buildNewSessionId
     *
     * @return
     */
    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void set(String key, Object value) {
        concurrentHashMap.put(key, value);
    }


    public <T> T get(String key) {
        return (T) concurrentHashMap.get(key);
    }


    public boolean isValid() {
        return getUser() != null ? true : false;
    }

    // 写Protobuf数据帧
    public synchronized void writeAndFlush(Object pkg) {
        if (null == channel) {
            log.error("channel is null");
            return;
        }
        channel.writeAndFlush(pkg);
    }

    // 写Protobuf数据帧
    public synchronized void writeAndClose(Object pkg) {
        channel.writeAndFlush(pkg);
        close();
    }

    // 关闭连接
    public synchronized void close() {
        // 用户下线 通知其他节点
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("CHANNEL_CLOSED error ");
                }
            }
        });
    }


    public LoginUser getUser() {
        return loginUser;
    }

    public void setUser(LoginUser loginUser) {
        this.loginUser = loginUser;
        loginUser.setSessionId(sessionId);
    }
}
