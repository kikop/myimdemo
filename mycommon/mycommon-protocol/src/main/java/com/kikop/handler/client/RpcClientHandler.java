package com.kikop.handler.client;

import com.kikop.constants.ReqType;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.core.datasturct.innerds.UserChart;
import com.kikop.core.datasturct.innerds.UserChartResp;
import com.kikop.core.datasturct.innerds.UserRegisterResp;
import com.kikop.utils.spring.SpringAppContextHolder;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcClientHandler
 * @desc 入栈处理器
 * SimpleChannelInboundHandler内置 ByteBuf的释放
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    private static final String CHAT_CLIENT_SERVICE = "com.kikop.service.ChartClientService";
    private static final String METHOD_NAME_BYREST = "sendRequest2Gateway";

    private final Class<?>[] parameterTypes = {RpcProtocol.class};

    // 反射获取业务端的实例
    private static Class<?> chatClientServiceClazz=null;
    private static Object chatClientServiceInstance=null;

    /**
     * 收到服务端的数据
     *
     * @param ctx
     * @param rpcResponseRpcProtocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol rpcResponseRpcProtocol) throws Exception {
        Header header = rpcResponseRpcProtocol.getHeader();
        ReqType reqType = ReqType.findByCode(header.getReqType());
        if (reqType == ReqType.REGISTER_RESP) { // 修改客户端注册状态
            UserRegisterResp userRegisterResp = (UserRegisterResp) rpcResponseRpcProtocol.getContent();
            log.info(String.format("登录认证成功,给您的 socket sessionId:%s", userRegisterResp.getSessionId()));
            ClientSession.loginSuccess(ctx, userRegisterResp);
        } else if (reqType == ReqType.CHAT) {
            // 处理来自 netty服务端的消息
            // mychatclient-->gateway-->mychatserver-->mychatclient-->gateway
            UserChart userChart = (UserChart) rpcResponseRpcProtocol.getContent();
            log.info("收到来自 {} 的聊天内容:{}", userChart.getFromUserId(), userChart.getData());
            // 2.消息转换
            RpcProtocol rpcRequestRpcProtocol = ClientMsgProcess2Gateway.convertServerResponseMsg2Request(rpcResponseRpcProtocol);
            if (null == rpcRequestRpcProtocol) {
                log.error("convertServerResponseMsg2Request is null");
                return;
            }
            processChatResponseToGateway(rpcRequestRpcProtocol, ctx);
        } else if (reqType == ReqType.CHAT_RESP) {
            UserChartResp userChartResp = (UserChartResp) rpcResponseRpcProtocol.getContent();
            log.info(String.format("走过万水千山, %s-->%s 的消息终于发送完成!", userChartResp.getToUserId(), userChartResp.getFromUserId()));
        }

    }

    private void assembleChatClientServiceInstance() {
        try {
            if (null != chatClientServiceInstance) {
                return;
            }
            // 未显式引用 mychatclient.Jar包
            // 但已经在容器中,所以可以反向获取容器中的实例对象
            chatClientServiceClazz = Class.forName(CHAT_CLIENT_SERVICE);
            if (null == chatClientServiceClazz) {
                log.error("chatClientServiceClazz is null");
            } else {
                chatClientServiceInstance = SpringAppContextHolder.getBean(chatClientServiceClazz);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("RpcClientHandler.static 反射异常:{}", e.getException());
        }
    }

    /**
     * 客户端收到服务端的数据后,进行核心数据的确认,这里是否有必要处理 todo
     * 发响应数据给网关,由网关完成转发
     *
     * @param rpcRequestRpcProtocol
     * @param ctx
     */
    private void processChatResponseToGateway(RpcProtocol rpcRequestRpcProtocol, ChannelHandlerContext ctx) {
        try {
            // 1.反射,获取对象实例
            assembleChatClientServiceInstance();
            // 3.通过短连接发给网关
            log.info("通过短连接发送对端聊天内容收到的确认消息给给网关,由网关转发给对端...");
            Object[] args = {rpcRequestRpcProtocol};
            Method declaredMethod = chatClientServiceClazz.getDeclaredMethod(METHOD_NAME_BYREST, parameterTypes);
            declaredMethod.invoke(chatClientServiceInstance, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.error("RpcClientHandler.responseInvoke 异常,{}", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.error("RpcClientHandler.responseInvoke 异常,{}", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("RpcClientHandler.responseInvoke 异常,{}", e);
        }
    }


}
