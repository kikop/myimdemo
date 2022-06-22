package com.kikop.handler.server.session;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file IServerSession
 * @desc
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
public interface IServerSession {

    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();
}

