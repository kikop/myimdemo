package com.kikop.handler.client;

import com.kikop.constants.ReqType;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.UserChart;
import com.kikop.core.datasturct.innerds.UserChartResp;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file ClientMsgProcess2Gateway
 * @desc 将来自服务端的响应类消息转换为网关的请求类消息
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class ClientMsgProcess2Gateway {

    /**
     * 响应消息
     * 通过 Rest发出去
     * 将来自服务端的响应类消息转换为网关的请求类消息-->toGateway-->toServer
     *
     * @param rpcResponseRpcProtocol
     * @return
     */
    public static RpcProtocol convertServerResponseMsg2Request(RpcProtocol rpcResponseRpcProtocol) {

        RpcProtocol convert2Request = new RpcProtocol();
        convert2Request.setHeader(rpcResponseRpcProtocol.getHeader());

        byte reqType = rpcResponseRpcProtocol.getHeader().getReqType();

        if (reqType == ReqType.CHAT.code()) {
            convert2Request.getHeader().setReqType(ReqType.CHAT_RESP.code()); // 重点
            UserChart userChart = (UserChart) rpcResponseRpcProtocol.getContent();
            UserChartResp userChartResp = new UserChartResp();

            userChartResp.setToUserId(userChart.getFromUserId());
            userChartResp.setFromUserId(userChart.getToUserId());
            String resultData = String.format("%s,你的单聊消息,%s 确认收到。", userChart.getFromUserId(),
                    userChart.getToUserId());
            userChartResp.setMsg(resultData);

            convert2Request.setContent(userChartResp);
            return convert2Request;
        }
        return null;

    }
}