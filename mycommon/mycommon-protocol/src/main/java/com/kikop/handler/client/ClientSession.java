package com.kikop.handler.client;

import com.kikop.core.datasturct.innerds.UserRegisterResp;
import com.kikop.dto.vo.LoginUserVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientSession {

    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /**
     * 用户实现客户端会话管理的核心
     */
    private Channel channel;

    /**
     * 保存登录后的服务端
     * sessionid
     * 可能的情况:
     * 相同的userId对应不同的 sessionId
     * pc user01-->sessionid1
     * android user01-->sessionid2
     */
    private String sessionId;

    private LoginUserVo loginUserVo;

    private boolean isConnected = false; // 通道连接标志
    private boolean isLogin = false; // 用户登录标志


    public LoginUserVo getLoginUserVo() {
        return loginUserVo;
    }

    public void setLoginUserVo(LoginUserVo loginUserVo) {
        this.loginUserVo = loginUserVo;
    }


    //绑定通道
    public ClientSession(Channel channel) {
        this.channel = channel;
        // socket sessionId,一开始为null
        this.sessionId = String.valueOf(-1);
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }


    //登录成功之后,设置sessionId
    public static void loginSuccess(
            ChannelHandlerContext ctx, UserRegisterResp userRegisterResp) {
        Channel channel = ctx.channel();
        ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        session.setSessionId(userRegisterResp.getSessionId());
        session.setLogin(true);
        log.info("loginSuccess登录成功");
    }

    // 获取channel
    public static ClientSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        return session;
    }

    public String getRemoteAddress() {
        return channel.remoteAddress().toString();
    }

    // 写protobuf 数据帧
    public ChannelFuture witeAndFlush(Object pkg) {
        ChannelFuture f = channel.writeAndFlush(pkg);
        return f;
    }

    public void writeAndClose(Object pkg) {
        ChannelFuture future = channel.writeAndFlush(pkg);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    // 关闭通道
    public void close() {
        boolean isConnected = false;

        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.error("连接顺利断开");
                }
            }
        });
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
