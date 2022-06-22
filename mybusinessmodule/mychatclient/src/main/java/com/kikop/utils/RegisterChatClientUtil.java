package com.kikop.utils;

import com.kikop.constants.DataSerialType;
import com.kikop.constants.ReqType;
import com.kikop.constants.RpcConstant;
import com.kikop.core.RpcAsyncProcess.RequestHolder;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.core.datasturct.innerds.UserRegister;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * @author kikop
 * @version 1.0
 * @project mychatclient
 * @file ChartClientService
 * @desc
 * @date 2022/3/13
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Component
public class RegisterChatClientUtil {

    @Value("${mycc.userid}")
    private String userId;

    @Value("${mycc.platform}")
    private String platform;

    @Value("${server.port}")
    private String httpport;


    @Value("${mycc.gateway.url}")
    private String gatewayUrl;

    private RestTemplate gatewayRestTemplate;


    @PostConstruct
    public void init() {
        gatewayRestTemplate = new RestTemplate();
    }


    /**
     * 注册客户端信息到 NettyServer
     *
     * @param channelFuture
     */
    public void registerClientInfoToNettyServer(ChannelFuture channelFuture) {

        if (channelFuture.isSuccess()) {
            // 1.构建消息体
            RpcProtocol requestRpcProtocol = new RpcProtocol<>();
            // 1.1.构建 协议体:header
            long requestId = RequestHolder.REQUEST_ID.incrementAndGet();
            Header header = new Header(RpcConstant.MAGIC,
                    DataSerialType.JSON_SERIAL.code(),
                    ReqType.REGISTER.code(),
                    requestId,0);
            requestRpcProtocol.setHeader(header); // 注意,此时 header的长度length:0,netty编码时才知道,但目前无法得知
            // 1.2.构建 协议体:RpcRequest 16+16*7+3=16+115=131 Bytes
            UserRegister userRegister = new UserRegister();
            userRegister.setUserId(userId);
            userRegister.setPlatform(platform);
            requestRpcProtocol.setContent(userRegister);
            // 2.发送
            channelFuture.channel().writeAndFlush(requestRpcProtocol);
        } else {
            System.out.println("注册服务端异常");
        }
    }

}