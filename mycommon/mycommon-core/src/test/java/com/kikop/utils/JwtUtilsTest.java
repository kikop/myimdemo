package com.kikop.utils;

import com.kikop.constants.SecurityConstants;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Jwt工具类
 * token:过期时间设置
 * token 的生成、校验放在认证中心
 * jwtToken-->claims-->keys
 *
 * @author ruoyi
 */
public class JwtUtilsTest {


    @Test
    public void testJwtUtils() {

        // Jwt自定义存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        String fastUUID = IdUtils.fastUUID();

        // f6a7b7ea-bc12-45f1-b52d-9df7e0e4748e
        claimsMap.put(SecurityConstants.USER_TOKEN, fastUUID); // user_token
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, "33"); // user_id
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, "kikop"); // user_name

        // eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzMiLCJ1c2VyX2tleSI6ImU0ODJmMDM4LTBhNmUtNGExNS1iMmFiLTc4OGE5MjljNTgwOCIsInVzZXJuYW1lIjoia2lrb3AifQ.eSPjXL0iMeZIVPnxAdFR3Vha93iuGf9MfCg3wjnqkJB94jBaQS6knfx5snwFZipvLNjcs0TGLZLh_KdxmLhawA
        String jwtToken = JwtUtils.createToken(claimsMap);
        System.out.println("jwtToken:" + jwtToken);

        String userToken = JwtUtils.getUserToken(jwtToken);
        System.out.println("userToken:" + userToken);

        String userId = JwtUtils.getUserId(jwtToken);
        System.out.println("userId:" + userId);

        String userName = JwtUtils.getUserName(jwtToken);
        System.out.println("userName:" + userName);

    }

}
