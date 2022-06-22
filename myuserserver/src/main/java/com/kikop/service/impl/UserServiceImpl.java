package com.kikop.service.impl;

import com.kikop.dto.po.LoginUser;
import com.kikop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
public class UserServiceImpl implements UserService {

    @Override
    public LoginUser getUser(String userId, String userName) {
//        if (!checkUserExist(userId)) {
//            return null;
//        }
        String strUserId = String.valueOf(userId);
        LoginUser loginUser = new LoginUser();
        // set userinfo
        loginUser.setUserid(userId);
        loginUser.setUsername(String.format("%sTest", strUserId));
        return loginUser;
    }

    private boolean checkUserExist(long userId) {
        boolean isExist = false;
        String strUserId = String.valueOf(userId);
        if ("1000".equalsIgnoreCase(strUserId)
                || "1001".equalsIgnoreCase(strUserId)) {
            isExist = true;
        }
        return isExist;
    }
}
