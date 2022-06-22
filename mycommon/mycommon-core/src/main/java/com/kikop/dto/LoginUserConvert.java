package com.kikop.dto;


import com.kikop.dto.po.LoginUser;
import com.kikop.dto.vo.LoginUserVo;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-core
 * @file LoginUser
 * @desc 登录用户视图
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
public class LoginUserConvert {

    public static LoginUserVo convert2LoginUserVo(LoginUser loginUser) {

        LoginUserVo loginUserVo = new LoginUserVo();

        loginUserVo.setUserid(loginUser.getUserid());
        loginUserVo.setUsername(loginUser.getUsername());
        loginUserVo.setAccessToken(loginUser.getAccessToken());
        loginUserVo.setExpireTime(loginUser.getExpireTime());
        return loginUserVo;
    }

    public static LoginUser convert2LoginUser(LoginUserVo loginUserVo) {

        LoginUser loginUser = new LoginUser();

        return loginUser;
    }


}
