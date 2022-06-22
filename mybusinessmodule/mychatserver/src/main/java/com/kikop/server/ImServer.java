package com.kikop.server;

import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.ServerSessionManger;
import com.kikop.service.ChatServerSessionCallback;
import com.kikop.utils.ZkRegisterChatServerUtil2;
import com.kikop.utils.ip.IpUtils;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserer
 * @file ImServer
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ImServer {

    private String chatServerIp = "";

    @Value("${server.port}")
    private int httpport;

    @Value("${myserver.socketport}")
    private int socketport;

    @Value("${myserver.zk.switch}")
    private boolean zkswitch;

    @Value("${myserver.redis.switch}")
    private boolean redisswitch;

    @Autowired
    public ZkRegisterChatServerUtil2 zkRegisterChatServerUtil2;

    // 客户端连接服务端后,需要缓存到Redis中
    // 1.在线用户、
    // 2.用户userId映射的Session服务列表
    @Autowired
    private ServerSessionManger serverSessionManger;

    @Autowired
    private ChatServerSessionCallback chatServerSessionCallback;

    private MyNettyServer myNettyServer;

    private GenericFutureListener<ChannelFuture> closeFutureListener = (ChannelFuture f) -> {
        log.info("服务端即将关闭...");
    };

    private GenericFutureListener<ChannelFuture> bindFutureListener = (ChannelFuture f) -> {
        if (f.isSuccess()) {

            log.info("startNettyServer success on hostAddress:{}", f.channel().localAddress());
            f.channel().closeFuture().addListener(closeFutureListener);

            // 1.构建 MyChatServerNode
            MyChatServerManager.getInst().setMyChatServerNode(chatServerIp, socketport, httpport);
            if (zkswitch) {
                registerChatServerInfoToZk();
                registerChatServerRoute();
            }
        } else {
            log.info("startNettyServer fail on hostAddress:{}", f.channel().localAddress());
        }
    };



    /**
     * 初始化
     */
    @PostConstruct
    public void start() {

        try {
            // 1.前置初始化
            // 2.前置条件设置
            // 2.1.ip
            chatServerIp = IpUtils.getHostIp();
            // 2.2.回调函数
            serverSessionManger.setiChatServerSessionCallback(chatServerSessionCallback);

            // 3.启动 netty服务端,启动连接成功后,向zk服务端注册
            myNettyServer = new MyNettyServer(chatServerIp, socketport);
            myNettyServer.setBindFutureListener(bindFutureListener);
            myNettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ImServer.start 运行异常", e);
        }
    }

    /**
     * 资源释放
     */
    @PreDestroy
    public void close() {
        myNettyServer.stop();
    }

    /**
     * 注册 mychatserver 服务端信息到ZK
     */
    private void registerChatServerInfoToZk() {
        zkRegisterChatServerUtil2.registerChatServerInfoToZk();
    }

    /**
     * 构建各个 mychatserver之间的 网状通信数据结构 todo
     */
    private void registerChatServerRoute() {
        // 注册节点间的监听
    }

}
