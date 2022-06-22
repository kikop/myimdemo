package com.kikop.client;

import com.alibaba.fastjson.JSONObject;
import com.kikop.dto.vo.LoginUserVo;
import com.kikop.handler.client.ClientSession;
import com.kikop.service.ChartClientService;
import com.kikop.utils.RegisterChatClientUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file imclient
 * @desc
 * @date 2020/1/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Component
public class ImClient {

    private MyNettyClient myNettyClient;


    @Autowired
    public RegisterChatClientUtil registerChatClientUtil;


    @Autowired
    public ChartClientService chartClientService;

    public ClientSession getClientSession() {
        return clientSession;
    }

    public void setClientSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    // 会话类
    private ClientSession clientSession;

    private Channel clientChannel;

    private LoginUserVo loginUserVo;

    GenericFutureListener<ChannelFuture> closeFutureListener = (ChannelFuture f) -> {
//        log.info(new Date() + ": 连接已经断开……");
        clientChannel = f.channel();
        // 获取通道总的会话
        ClientSession clientSession1 =
                clientChannel.attr(ClientSession.SESSION_KEY).get();
        clientSession1.close();

    };


    private GenericFutureListener<ChannelFuture> connectFutureListener = (ChannelFuture f) -> {
        if (f.isSuccess()) {
            log.info("startNettyServer success on hostAddress:{}", f.channel().localAddress());

            clientChannel = f.channel();
            clientSession = new ClientSession(clientChannel);
            clientSession.setLoginUserVo(loginUserVo);
            clientChannel.closeFuture().addListener(closeFutureListener);
            registerClientInfoToNettyServer(f); // sessionId is null
        } else {
            log.info("startNettyServer fail on hostAddress:{}", f.channel().localAddress());
        }
    };


    @PostConstruct
    public void start() {
        // 1.登录
        JSONObject loginResult = doUserLogin();
        // 2.校验及请求
        if (null != loginResult) {
            // 1.设置 token
            loginUserVo = loginResult.getObject("loginUserVo", LoginUserVo.class);
            chartClientService.setAccessToken(loginUserVo.getAccessToken());
            // 2.connectSocketServer
            connectSocketServer(loginResult);
        }

    }


    /**
     * 获取可使用的 netty sever
     *
     * @param loginResult
     * @return
     */
    private JSONObject getAvaliableNettyServer(JSONObject loginResult) {
        // 2.连接 netty服务端,连接成功后,向服务端注册
        String selectedServerInfo = loginResult.getString("mychatserver");
        if (null != selectedServerInfo) {
            JSONObject avaliableResult = new JSONObject();
            // 127.0.0.1:9091:8081
            String[] selectedServerInfoSplitArray = selectedServerInfo.split(":");
            String serverIp = selectedServerInfoSplitArray[0];
            String httpPort = selectedServerInfoSplitArray[1];
//            String serverUrl = String.format("http://%s:%s", serverIp, httpPort);

            String socketPort = selectedServerInfoSplitArray[2];


            avaliableResult.put("serverIp", serverIp);
            avaliableResult.put("socketPort", Integer.valueOf(socketPort));
            return avaliableResult;
        } else {
            log.error("无可用的netty server");
        }

        return null;

    }

    /**
     * 1.doUserLogin
     *
     * @return
     */
    private JSONObject doUserLogin() {
        JSONObject loginResult = chartClientService.userLogin();
        if (null == loginResult || !loginResult.getBoolean("success")) {
            log.error("doUserLogin is null");
            return null;
        }
        return loginResult;
    }


    /**
     * 2.connectSocketServer
     *
     * @param loginResult
     */
    private void connectSocketServer(JSONObject loginResult) {
        //2.获取可使用的 netty sever
        JSONObject avaliableNettyServer = getAvaliableNettyServer(loginResult);
        if (null != avaliableNettyServer) {
            myNettyClient = new MyNettyClient(avaliableNettyServer.getString("serverIp")
                    , avaliableNettyServer.getIntValue("socketPort"));
            myNettyClient.setConnectedFutureListener(connectFutureListener);
            myNettyClient.doConnect();
        }
    }

    /**
     * 3.注册客户端 ID userID信息到 NettyServer
     *
     * @param connectFuture
     */
    private void registerClientInfoToNettyServer(ChannelFuture connectFuture) {
        registerChatClientUtil.registerClientInfoToNettyServer(connectFuture);
    }
}