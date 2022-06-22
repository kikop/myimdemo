package com.kikop.service;

import com.kikop.dto.po.LoginUser;

/**
 * @author kikop
 * @version 1.0
 * @project myuserserver
 * @file UserServiceImpl
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public interface UserService {

    LoginUser getUser(String userId, String userName);


}
