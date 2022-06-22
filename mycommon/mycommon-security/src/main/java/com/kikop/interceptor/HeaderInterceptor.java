package com.kikop.interceptor;

import com.kikop.constants.SecurityConstants;
import com.kikop.context.SecurityContextHolder;
import com.kikop.dto.po.LoginUser;
import com.kikop.security.SecurityAuthUtils;
import com.kikop.security.SecurityUtils;
import com.kikop.utils.ServletUtils;
import com.kikop.utils.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义请求头拦截器
 * 将Header数据封装到线程变量中方便获取
 * 注意：
 * 此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author ruoyi
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_TOKEN));

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = SecurityAuthUtils.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                // token过期时间续命
                SecurityAuthUtils.verifyLoginUserExpire(loginUser);
                // loginUser写入hashMap
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
