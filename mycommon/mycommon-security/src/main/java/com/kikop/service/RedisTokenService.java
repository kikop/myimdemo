package com.kikop.service;

import com.kikop.constants.CacheConstants;
import com.kikop.constants.SecurityConstants;
import com.kikop.dto.po.LoginUser;
import com.kikop.security.SecurityUtils;
import com.kikop.utils.IdUtils;
import com.kikop.utils.JwtUtils;
import com.kikop.utils.ServletUtils;
import com.kikop.utils.StringUtils;
import com.kikop.utils.ip.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-redis
 * @file TokenService
 * @desc 基于Redis的 token管理服务
 * @date 2021/3/30
 * @time 8:00
 * @by IDE IntelliJ IDEA
 */
@Component
public class RedisTokenService {

    // Redis服务类
    @Autowired
    public RedisService redisService;

    /**
     * @param loginUser access_token、expires_in
     * @return
     */
    public Map<String, Object> createToken(LoginUser loginUser) {

        // 1.createToken

        // IdUtils 生成token(作废)
        String token = IdUtils.fastUUID();

        // 2.构造loginUser
        String userId = loginUser.getUserid();
        String userName = loginUser.getUsername();

//        Long userId = loginUser.getSysUser().getUserId();
//        String userName = loginUser.getSysUser().getUserName();


//        loginUser.setUserid(userId);
//        loginUser.setUsername(userName);

        // 请求用户的 IP
        loginUser.setIpaddr(IpUtils.getIpAddr(ServletUtils.getRequest()));


        // 4.jwtToken业务数据定义
        // user_token、user_id、username
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(SecurityConstants.USER_TOKEN, token); // usertoken:token
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, userId);
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, userName);

        String accessToken = JwtUtils.createToken(claimsMap);
        loginUser.setAccessToken(accessToken);

        // 5.create JwtToken
        Map<String, Object> rspMap = new HashMap<String, Object>();
        rspMap.put("access_token", accessToken);
        rspMap.put("expires_in", CacheConstants.EXPIRATION_TIME);

        // 6.存入 Redis
        refreshToken(loginUser);
        return rspMap;
    }

    /**
     * 刷新令牌有效期
     * 根据前缀 + uuid将 loginUser缓存
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {

        // 1.设置过期时间
        loginUser.setLoginTime(System.currentTimeMillis()); // 1646546467180
        loginUser.setExpireTime(loginUser.getLoginTime() + CacheConstants.EXPIRATION_TIME * CacheConstants.MILLIS_MINUTE);

        // 2.token入 redis缓存
        // 数据结构:String
        // key:myimdemo_auth:jwttoken: + jwtToken,Eg:myimdemo_auth:jwttoken::53b44661-93b6-4dc7-b197-c4524820b1f7
        // value:loginUser 进行Json序列化对象
        String userKey = getTokenKey(loginUser.getAccessToken());
        redisService.setCacheObject(userKey, loginUser, CacheConstants.EXPIRATION_TIME, TimeUnit.MINUTES);
    }


    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser() {
        return getLoginUser(ServletUtils.getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = SecurityUtils.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String jwtToken) {
        LoginUser loginUser = null;
        try {
            if (StringUtils.isNotEmpty(jwtToken)) {
//                String userToken = JwtUtils.getUserToken(jwtToken);
                loginUser = redisService.getCacheObject(getTokenKey(jwtToken));
                return loginUser;
            }
        } catch (Exception e) {
        }
        return loginUser;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getAccessToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userToken = JwtUtils.getUserToken(token);
            redisService.deleteObject(getTokenKey(userToken));
        }
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param loginUser
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= CacheConstants.MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 根据 前缀和uuid生成userKey
     *
     * @param token
     * @return
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }
}
