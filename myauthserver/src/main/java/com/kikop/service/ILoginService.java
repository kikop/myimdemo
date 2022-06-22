package com.kikop.service;


import com.alibaba.fastjson.JSONObject;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file ILoginService
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
public interface ILoginService {

    /**
     * 登录网关服务 by Feign
     *
     * @param reqParam
     * @return
     */
    JSONObject userLogin(JSONObject reqParam);
}
