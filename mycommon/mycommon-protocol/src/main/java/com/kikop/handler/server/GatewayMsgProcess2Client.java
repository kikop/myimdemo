package com.kikop.handler.server;

import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.handler.server.model.ServerSessionManger;
import com.kikop.handler.server.session.IServerSession;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file GatewayMsgProcess2Client
 * @desc 服务端单聊、群聊消息的发送
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class GatewayMsgProcess2Client {

    /**
     * 单聊
     * 通过 netty发出去
     *
     * @param requestRpcProtocol
     */
    public static void doSingleSending(RpcProtocol requestRpcProtocol) {
        doSingleSendingXXX(requestRpcProtocol);
    }

    /**
     * 单聊响应
     * 通过 netty发出去
     *
     * @param responseRpcProtocol
     */
    public static void doSingleSendingResp(RpcProtocol responseRpcProtocol) {
        doSingleSendingXXX(responseRpcProtocol);
    }

    private static void doSingleSendingXXX(RpcProtocol requestRpcProtocol) {

        // 1.查找 toUserID对应的 channelHandlerContext

//        UserChart userChart = (UserChart) requestRpcProtocol.getContent();
//        String toUserId = userChart.getToUserId();

        LinkedHashMap content = (LinkedHashMap) requestRpcProtocol.getContent();
        String toUserId = (String) content.get("toUserId");

        List<IServerSession> serverSessionList = ServerSessionManger.inst().getSessionsBy(toUserId);
        if (null == serverSessionList || serverSessionList.size() == 0) {
            log.warn("发往 {} 的消息已经转离线存储", toUserId);
            return;
        }
        // 2.异步发送
        for (int i = 0; i < serverSessionList.size(); i++) {
            IServerSession iServerSession = serverSessionList.get(i);
            if (null == iServerSession) {
                log.error("iServerSession is null,serverSessionList:{}", serverSessionList);
                continue;
            }
            iServerSession.writeAndFlush(requestRpcProtocol);
        }
    }

}
