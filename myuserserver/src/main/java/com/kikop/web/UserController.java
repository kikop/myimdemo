package com.kikop.web;

import com.alibaba.fastjson.JSONObject;
import com.kikop.dto.po.LoginUser;
import com.kikop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author kikop
 * @version 1.0
 * @project myuserserver
 * @file UserController
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "uc")
public class UserController {


    @Autowired
    public UserService userService;

    public UserController() {
    }

    /**
     * 用户登录
     *
     * @param reqParam
     * @return
     */
    // http://localhost:7082/myuserserver/userserver/login
    // @Cacheable(cacheNames = "userList", value = {"userInfoByuserId"}, key = "#reqParam.userId")
    @RequestMapping(value = "userLogin", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject userLogin(@RequestBody JSONObject reqParam) {
        JSONObject result = new JSONObject();
        String userId = reqParam.getString("userId");
        String userName = reqParam.getString("userName");
        LoginUser loginUser = userService.getUser(userId, userName);
        result.put("loginUser", loginUser);
        result.put("success", true);
        return result;
    }


    /**
     * 用户更新
     *
     * @param reqParam
     * @return
     */
    // 当需要在不影响方法执行的情况下更新缓存时，可以使用 @CachePut，也就是说，被 @CachePut 注解的缓存方法总是会执行
    // 表示对key=kikop的数据进行更新
    // @CachePut(cacheNames = "userList", key = "#reqParam.userId", value = {"userInfoByuserId"})
    // @CachePut
    @RequestMapping(value = "updateUserInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject updateUserInfo(@RequestBody JSONObject reqParam) {
        return null;
    }

}
