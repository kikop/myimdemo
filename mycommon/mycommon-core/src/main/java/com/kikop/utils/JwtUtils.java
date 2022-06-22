package com.kikop.utils;

import com.kikop.constants.SecurityConstants;
import com.kikop.text.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * Jwt工具类
 * token:过期时间设置
 * token 的生成、校验放在认证中心
 * jwtToken-->claims-->keys
 *
 * @author ruoyi
 */
public class JwtUtils {

    // begin_Claims:数据声明

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";


    // end_Claims:数据声明

    /**
     * 根据算法、令牌秘钥、业务数据声明中生成令牌
     *
     * @param customClaims 业务数据声明,user_token、user_id、username
     * @return 令牌
     */
    public static String createToken(Map<String, Object> customClaims) {

        String token = Jwts.builder()
                .setClaims(customClaims)
                .signWith(SignatureAlgorithm.HS512, JwtUtils.SECRET)
                .compact();
        return token;
    }


    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(JwtUtils.SECRET).parseClaimsJws(token).getBody();
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserToken(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.USER_TOKEN);
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserToken(Claims claims) {
        return getValue(claims, SecurityConstants.USER_TOKEN);
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_USER_ID);
    }

    /**
     * 根据身份信息获取用户ID
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserId(Claims claims) {
        return getValue(claims, SecurityConstants.DETAILS_USER_ID);
    }

    /**
     * 根据令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserName(String token) {

        // 获取 claims
        Claims claims = parseToken(token);
        // 获取 claims中的keys
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取用户名
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static String getUserName(Claims claims) {
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param claims 身份信息
     * @param key    键
     * @return 值
     */
    public static String getValue(Claims claims, String key) {
        return Convert.toStr(claims.get(key), "");
    }

}
