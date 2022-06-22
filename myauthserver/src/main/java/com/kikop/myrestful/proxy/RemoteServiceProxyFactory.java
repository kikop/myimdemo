package com.kikop.myrestful.proxy;

import com.kikop.myrestful.loadbalance.CustomLBClientFactory;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.ribbon.RibbonClient;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file RemoteServiceProxyFactory
 * @desc 远程服务代理工厂
 * @date 2021/3/7
 * @time 10:30
 * @by IDE: IntelliJ IDEA
 */
public class RemoteServiceProxyFactory {

    private static final String PROXY_PROTOCOL = "http://";

    /**
     * 获取远程代理服务
     *
     * @param remoteSericeClazz IRemoveUserService.class
     * @param hostName          http://myuserserver
     * @param strListOfServers  127.0.0.1:8085,127.0.0.1:8086
     * @param <T>
     * @return
     */
    public static <T> T getProxyService(Class<T> remoteSericeClazz, String hostName, String strListOfServers) {

        //  增加协议头
        if (hostName.indexOf(PROXY_PROTOCOL) == -1) {
            hostName = String.format("%s%s", PROXY_PROTOCOL, hostName);
        }
        // 1.ConfigurationManager的引用太复杂
        // 换做代码实现 DefaultClientConfigImpl
        // ConfigurationManager.loadPropertiesFromResources("myuserserver/myuserserver.properties");

        // 2.设置web请求客户端
        RibbonClient ribbonClient = RibbonClient.builder()
                .lbClientFactory(new CustomLBClientFactory(strListOfServers)).build();

        // 3.创建代理服务
        T removeServiceProxy = Feign.builder()
                .client(ribbonClient)    // 基于 Ribbon客户端做负载均衡(配置文件)
                // Feign 默认支持 JSON 格式的编码器和解码器
                .encoder(new JacksonEncoder())  // 设置 Feign.RequestTemplate body 编码
                .decoder(new JacksonDecoder())  // 设置 Feign.RequestTemplate body 解码
                .target(remoteSericeClazz, hostName); // myuserserver 代表实际的服务地址(ip:port)会被拼接并动态代理

        // -->http://myuserserver/mybusinessdemo/myfeign/getRibbonValue
        return removeServiceProxy;
    }
}
