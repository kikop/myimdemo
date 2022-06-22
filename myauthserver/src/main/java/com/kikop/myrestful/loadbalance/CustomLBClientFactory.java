package com.kikop.myrestful.loadbalance;


import com.netflix.client.ClientFactory;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file CustomLBClientFactory
 * @desc 自定义客户端负载均衡策略(feign - ribbon.jar)
 * @date 2021/3/7
 * @time 10:30
 * @by IDE: IntelliJ IDEA
 */
public class CustomLBClientFactory implements LBClientFactory {

    private String strListOfServers = null;

    /**
     * 127.0.0.1:8085,127.0.0.1:8086
     *
     * @param strListOfServers
     */
    public CustomLBClientFactory(String strListOfServers) {
        this.strListOfServers = strListOfServers;
    }

    /**
     * 构建 LBClient
     *
     * @param clientName Eg:http://myuserserver/getUserInfo;http://127.0.0.1:8185-> myuserserver and 127.0.0.1
     * @return
     */
    @Override
    public LBClient create(String clientName) {

        // 设置负载均衡属性
        // IClientConfig默认的实现类:DefaultClientConfigImpl
        IClientConfig config = ClientFactory.getNamedConfig(clientName);

        // 1.重试机制(对第一次请求的服务的重试次数)
        IClientConfigKey<Integer> maxAutoRetries = CommonClientConfigKey.MaxAutoRetries;
        config.set(maxAutoRetries, 1);

        // 2.要重试的下一个服务的最大数量（不包括第一个服务）
        IClientConfigKey<Integer> maxAutoRetriesNextServer = CommonClientConfigKey.MaxAutoRetriesNextServer;
        config.set(maxAutoRetriesNextServer, 2);

        // 3.对所有请求都重试,get可以
        // post需事先幂等,否则危险,所以设置:false
        IClientConfigKey<Boolean> okToRetryOnAllOperations = CommonClientConfigKey.OkToRetryOnAllOperations;
        config.set(okToRetryOnAllOperations, false);

        // 4.服务列表刷新机制
        // 定时 ping任务,从 server刷新服务列表
        IClientConfigKey<Integer> serverListRefreshInterval = CommonClientConfigKey.ServerListRefreshInterval;
        config.set(serverListRefreshInterval, 2000);

        // 5.请求链接超时
        // 这里配置的 ConnectTimeout和 ReadTimeout是当HTTP客户端使用的是 HttpClient才生效
        IClientConfigKey<Integer> connectTimeout = CommonClientConfigKey.ConnectTimeout;
        config.set(connectTimeout, 3000);

        // 6.请求处理超时时间(这里配置的ConnectTimeout和 ReadTimeout是当HTTP客户端使用的是HttpClient才生效)
        IClientConfigKey<Integer> readTimeout = CommonClientConfigKey.ReadTimeout;
        config.set(readTimeout, 5000);

        // 7.服务列表
        // 具体的 IP:PORT 将替换 clientName
        IClientConfigKey<String> listOfServers = CommonClientConfigKey.ListOfServers;
        config.set(listOfServers, strListOfServers);

        // 8.enablePrimeConnections
        IClientConfigKey<Boolean> enablePrimeConnections = CommonClientConfigKey.EnablePrimeConnections;
        config.set(enablePrimeConnections, false);

        // 9.负载对象
        // DynamicServerListLoadBalancer，采用的是线性轮询的方式来选择调用服务实例，
        // 该算法实现简单并没有区域Zone的概念，所以它会把所有实例视为一个Zone下的节点来看待，
        // 这样就会周期性地产生跨区域访问的情况
        // 由此产生:ZoneAwareLoadBalancer
        ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);


        // 10.选取服务策略
        ZoneAwareLoadBalancer zoneAwareLoadBalancer = (ZoneAwareLoadBalancer) lb;
        // IRule 默认轮询策略
        // com.netflix.loadbalancer.RoundRobinRule@18ece7f4
        //  ((AvailabilityFilteringRule) zoneAwareLoadBalancer.rule)

        // zoneAwareLoadBalancer.setRule(new RandomRule());     // 设置策略类型:随机
        zoneAwareLoadBalancer.setRule(new RoundRobinRule()); // 默认设置策略类型:简单轮询(Kafka默认)

        return LBClient.create(lb, config);
    }
}
