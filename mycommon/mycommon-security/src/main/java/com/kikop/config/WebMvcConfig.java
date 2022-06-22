package com.kikop.config;

import com.kikop.interceptor.HeaderInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 * todo
 * EnableAutoConfiguration 里面触发 不用加 @Configuration
 *
 * @author ruoyi
 */
public class WebMvcConfig implements WebMvcConfigurer {


    // 难道boot项目都不带 context-path=/myauthserver
    // http://localhost:7081/login
    // http://localhost:7081/myauthserver/auth/login
    /**
     * 白名单
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/login", "/logout", "/refresh", "/myauthserver/security/login"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor();
    }
}
