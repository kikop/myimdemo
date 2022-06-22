package com.kikop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kikop.myrestful.service.IRemoveUserService;
import com.kikop.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file LoginServiceImpl
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Component
public class LoginServiceImpl implements ILoginService {


//    private RestTemplate userRestTemplate = null;

    // 远程用户服务
    @Autowired
    public IRemoveUserService iRemoveUserService;


    @PostConstruct
    public void init() {
        // 1.构建 userRestTemplate
        // userRestTemplate = new RestTemplate();
    }


    @Override
    public JSONObject userLogin(JSONObject reqParam) {
        return iRemoveUserService.userLogin(reqParam);
    }

}
