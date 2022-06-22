package com.kikop.service;

import com.alibaba.fastjson.JSONObject;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file ChartClientService
 * @desc
 * @date 2022/3/13
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Component
public class ChartClientService {

    @Value("${mycc.userid}")
    private long userId;

    @Value("${mycc.gateway.url}")
    private String gatewayUrl;

    private RestTemplate gatewayRestTemplate;

    // 用户认证的 token
    @Value("${mycc.accesstoken}")
    private String accessToken = "";

    @PostConstruct
    public void init() {
        gatewayRestTemplate = new RestTemplate();
    }

    public JSONObject userLogin() {
        JSONObject reqParam = new JSONObject();
        reqParam.put("userId", userId);
        if (StringUtils.isNotEmpty(accessToken)) {
            reqParam.put("accessToken", accessToken);
        }
        JSONObject result = gatewayRestTemplate.postForObject(gatewayUrl + "gatewayc/userLogin", reqParam, JSONObject.class);

        return result;
    }

    /**
     * 发送请求给网关
     * 1.Web调用
     * 2.socket反射调用
     *
     * @param requestRpcProtocol
     */
    public JSONObject sendRequest2Gateway(RpcProtocol requestRpcProtocol) {
        JSONObject result = gatewayRestTemplate.postForObject(gatewayUrl + "gatewayc/recvRequestFromClient", requestRpcProtocol,
                JSONObject.class);
        return result;
    }


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
