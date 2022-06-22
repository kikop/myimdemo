//package com.kikop.utils;
//
//import com.kikop.constants.SecurityConstants;
//import com.kikop.constants.TokenConstants;
//import com.kikop.context.SecurityContextHolder;
//import com.kikop.dto.po.LoginUser;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author kikop
// * @version 1.0
// * @project myauthserver
// * @file 用户认证及权限获取工具类
// * @desc
// * @date 2022/3/11
// * @time 10:50
// * @by IDE IntelliJ IDEA
// */
//public class SecurityUtils {
//    /**
//     * 获取用户ID
//     */
//    public static Long getUserId() {
//        return SecurityContextHolder.getUserId();
//    }
//
//    /**
//     * 获取用户名称
//     */
//    public static String getUsername() {
//        return SecurityContextHolder.getUserName();
//    }
//
//    /**
//     * 获取用户key
//     */
//    public static String getUserKey() {
//        return SecurityContextHolder.getUserKey();
//    }
//
//    /**
//     * 获取登录用户信息
//     */
//    public static LoginUser getLoginUser() {
//        return SecurityContextHolder.get(SecurityConstants.LOGIN_USER, LoginUser.class);
//    }
//
//    /**
//     * 获取请求token
//     */
//    public static String getToken() {
//        return getToken(ServletUtils.getRequest());
//    }
//
//    /**
//     * 根据request获取请求token
//     */
//    public static String getToken(HttpServletRequest request) {
//        // 从 request header获取 token标识
//        String token = request.getHeader(TokenConstants.AUTHENTICATION);
//        return replaceTokenPrefix(token);
//    }
//
//    /**
//     * 裁剪token前缀
//     */
//    public static String replaceTokenPrefix(String token) {
//        // 如果前端设置了令牌前缀，则裁剪掉前缀
//        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
//            token = token.replaceFirst(TokenConstants.PREFIX, "");
//        }
//        return token;
//    }
//
//    /**
//     * 是否为管理员
//     *
//     * @param userId 用户ID
//     * @return 结果
//     */
//    public static boolean isAdmin(Long userId) {
//        return userId != null && 1L == userId;
//    }
//
//    /**
//     * 生成BCryptPasswordEncoder密码
//     *
//     * @param password 密码
//     * @return 加密字符串
//     */
//    public static String encryptPassword(String password) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        return passwordEncoder.encode(password);
//    }
//
//    /**
//     * 判断密码是否相同
//     *
//     * @param rawPassword     真实密码
//     * @param encodedPassword 加密后字符
//     * @return 结果
//     */
//    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }
//}
