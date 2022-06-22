package com.kikop.web;

import com.alibaba.fastjson.JSONObject;
import com.kikop.client.ImClient;
import com.kikop.constants.DataSerialType;
import com.kikop.constants.ReqType;
import com.kikop.constants.RpcConstant;
import com.kikop.core.RpcAsyncProcess.RequestHolder;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.core.datasturct.innerds.UserChart;
import com.kikop.handler.client.ClientSession;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import com.kikop.service.ChartClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file ChatClientController
 * @desc
 * @date 2022/3/13
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "chatclientc")
public class ChatClientController {

    @Value("${mycc.userid}")
    private long userId;

    @Autowired
    public ChartClientService clientService;

    @Autowired
    public ImClient imClient;

    /**
     * 终端登录
     *
     * @return
     */
    @GetMapping(value = "userLogin")
    @ResponseBody
    public JSONObject userLogin() {
        JSONObject result = new JSONObject();
        result.put("success", false);
        try {
            result = clientService.userLogin();
            result.put("success", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 客户端发送请求给网关
     * http://localhost:6001/mychatclient/chatclientc/sendRequest2Gateway
     */
    @RequestMapping(value = "sendRequest2Gateway", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject sendRequest2Gateway() {

        JSONObject result = new JSONObject();
        result.put("success", false);
        try {

            // 1.构建消息体
            RpcProtocol requestRpcProtocol = new RpcProtocol<>();

            // 1.1.构建 协议体:header
            ClientSession clientSession = imClient.getClientSession();
            String sessionId = clientSession.getSessionId();
            long requestId = RequestHolder.REQUEST_ID.incrementAndGet();
            Header header = new Header(RpcConstant.MAGIC,
                    DataSerialType.JSON_SERIAL.code(),
                    ReqType.CHAT.code(),
                    requestId, 0);
            requestRpcProtocol.setHeader(header); // 注意,此时 header的长度 length:0,netty编码时才知道,但目前无法得知

            // 1.2.构建 协议体:RpcRequest 16+16*7+3=16+115=131 Bytes
            UserChart userChart = new UserChart();
            userChart.setSessionId(sessionId);
            userChart.setFromUserId(String.valueOf(userId));
            userChart.setToUserId("1000");
            userChart.setData("你好,我是机器人!");
            requestRpcProtocol.setContent(userChart); // 泛型 todo

            // 1.3.请求编码,由于是走Rest接口,需手动获取设置长度
            // 缺点:没有充分利用netty 的特性
            ISerializer serializer = SerializerManager.getSerializer(header.getSerialType());
            byte[] data = serializer.serialize(requestRpcProtocol.getContent()); //序列化
            header.setLength(data.length);
            result.put("requestId", header.getRequestId());
            result.put("success", true);
            clientService.sendRequest2Gateway(requestRpcProtocol);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}