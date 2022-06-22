package com.kikop.web;

import com.alibaba.fastjson.JSONObject;
import com.kikop.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file AuthController
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "authc")
public class AuthController {

    @Autowired
    public ILoginService iLoginService;


    /**
     * 用户登录
     *
     * @param reqParam
     * @return
     */
    @RequestMapping(value = "userLogin", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject userLogin(@RequestBody JSONObject reqParam) {

        JSONObject result = new JSONObject();

        long userId = reqParam.getLong("userId"); // 1000
        String userName = reqParam.getString("userName"); // kikop1000

        // 1.用户数据库验证及用户权限获取
        // LoginUser里面包含:SysUser(数据来源 DB)
        result = iLoginService.userLogin(reqParam);

//        if (null != result) {
//            LoginUser loginUser = result.getObject("loginUser", LoginUser.class);
//            // 2.token管理
//            Map<String, Object> tokenMap = tokenService.createToken(loginUser);
//            result.put("token", loginUser.getToken());
//            result.putAll(tokenMap);
//        }
        return result;
    }




}


