package com.kikop.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 配置注册
 *
 * @author ruoyi
 **/
@Configuration
public class FeignAutoConfiguration {

    /**
     * 传递用户信息请求头，防止丢失
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
