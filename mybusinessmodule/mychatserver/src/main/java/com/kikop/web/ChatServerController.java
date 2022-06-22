package com.kikop.web;

import com.alibaba.fastjson.JSONObject;
import com.kikop.constants.ReqType;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.service.ChatServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file ChatServerController
 * @desc
 * @date 2020/1/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@RestController
@RequestMapping(value = "chatserverc")
public class ChatServerController {

    @Autowired
    public ChatServerService chatServerService;

    /**
     * 接收网关消息的转发请求
     *
     * @param requestRpcProtocol
     * @return 向网关返回调度结果
     */
    @RequestMapping(value = "recvGatewayDispatchRequest", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject recvGatewayDispatchRequest(@RequestBody RpcProtocol requestRpcProtocol) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        try {
            Header header = requestRpcProtocol.getHeader();
            result.put("requestId", header.getRequestId());

            ReqType reqType = ReqType.findByCode(requestRpcProtocol.getHeader().getReqType());
            switch (reqType) {
                case CHAT:
                    log.info("CHAT...");
                    chatServerService.singleSend(requestRpcProtocol);
                    break;
                case CHAT_RESP:
                    log.info("CHAT_RESP...");
                    chatServerService.singleSendResp(requestRpcProtocol);
                default:
                    break;
            }
            result.put("success", true); // 直接给同步相应,实际还可能在发送中(异步响应才是最美,暂无必要)
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return result;
    }
}
