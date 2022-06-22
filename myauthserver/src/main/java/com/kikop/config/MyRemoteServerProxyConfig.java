package com.kikop.config;

import com.kikop.myrestful.proxy.RemoteServiceProxyFactory;
import com.kikop.myrestful.service.IRemoveUserService;
import com.kikop.servicediscovery.IServiceDiscovery;
import com.kikop.servicediscovery.impl.ServiceDiscoveryImpl;
import com.kikop.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file MyRemoteServerProxyConfig
 * @desc 远程服务代理配置
 * @date 2021/3/7
 * @time 10:30
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
@Configuration
public class MyRemoteServerProxyConfig {


    @Value("${myauth.zk.switch}")
    private Boolean zkSwitch;

    @Value("${myauth.userserver.url}")
    private String userServiceUrl;


    @Value("${myauth.zk.addr}")
    private String zkConnectString;


    @Value("${myauth.zk.sessiontimeout}")
    private int sessionTimeOut;


    @Value("${myuser.zk.rootpath}")
    private String rootpath;

    @Value("${myauth.feign.servicename}")
    private String myuserserver;

    @PostConstruct
    public void init() {
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IRemoveUserService iRemoveUserService() {

        // 1.根据服务名获取服务端列表
        String strListOfServers = getAvaliableUserService();

        // 2.创建代理
        if (StringUtils.isEmpty(strListOfServers)) {
            log.error("getAvaliableUserService for {} is null", myuserserver);
            return null;
        }
        // 代理中进行负载均衡
        return RemoteServiceProxyFactory.getProxyService(
                IRemoveUserService.class, // 远程代理的接口Feign定义
                myuserserver,             // myuserserver
                strListOfServers);        // 127.0.0.1:7082,127.0.0.1:7083
    }

    /**
     * 获取一个可用的用户服务列表
     *
     * @return 127.0.0.1:7082,127.0.0.1:7083
     */
    private String getAvaliableUserService() {

        if (true == zkSwitch) {
            String strListOfServers = "";
            IServiceDiscovery iServiceDiscovery = new ServiceDiscoveryImpl(zkConnectString, rootpath, sessionTimeOut);
            List<String> myuserserverList = iServiceDiscovery.getSimpleEphmeralNodeAvaliableServiceList();

            for (String server : myuserserverList) {
                strListOfServers += server + ",";
            }
            if (!"".equalsIgnoreCase(strListOfServers)) {
                strListOfServers = strListOfServers.substring(0, strListOfServers.length() - 1);
            }
            return strListOfServers;
        }
        return userServiceUrl;
    }


}
