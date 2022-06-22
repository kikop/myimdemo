package com.kikop.service;

import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.handler.server.GatewayMsgProcess2Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserer
 * @file ChatServerService
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ChatServerService {

    /**
     * 单聊
     *
     * @param requestRpcProtocol
     */
    public void singleSend(RpcProtocol requestRpcProtocol) {
        GatewayMsgProcess2Client.doSingleSending(requestRpcProtocol);
    }

    /**
     * 单聊响应
     *
     * @param requestRpcProtocol
     */
    public void singleSendResp(RpcProtocol requestRpcProtocol) {
        GatewayMsgProcess2Client.doSingleSendingResp(requestRpcProtocol);
    }
}
