package com.kikop.handler.server;

import com.kikop.constants.ReqType;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.core.datasturct.innerds.UserRegister;
import com.kikop.core.datasturct.innerds.UserRegisterResp;
import com.kikop.dto.po.LoginUser;
import com.kikop.handler.server.model.ServerSessionManger;
import com.kikop.handler.server.session.impl.LocalSessionImpl;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcServerHandler
 * @desc 入栈，接收请求进行处理 Request
 * SimpleChannelInboundHandler:自动释放ByteBuf
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
@Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    /**
     * 上线
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。
     * 也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // super.channelActive(ctx);

        // 通知客户端链接建立成功
        // String str = "与服务端链接建立成功" + " " + channel.localAddress().getHostString() + "\r\n";
        // ctx.writeAndFlush(str);

        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info(String.format("有新的客户端连接,信息：channelId：%s,ip:%s,port:%d"
                , channel.id().asShortText()
                , channel.localAddress().getHostString(),
                channel.localAddress().getPort()));

    }


    /**
     * 掉线
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。
     * 也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // super.channelInactive(ctx);
        System.out.println("断开链接" + ctx.channel().localAddress().toString());
        ServerSessionManger.inst().closeSession(ctx);
    }


    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        ServerSessionManger.inst().closeSession(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol rpcRequestRpcProtocol) throws Exception {
        log.error("process channelRead0 开始...");
        if (isValidUser(rpcRequestRpcProtocol)) {
            processClientMessage(rpcRequestRpcProtocol, ctx);
        }
        log.error("process channelRead0 结束");

    }

    /**
     * JWT token校验
     * 调用认证服务即可 todo
     *
     * @param rpcRequestRpcProtocol
     * @return
     */
    private boolean isValidUser(RpcProtocol rpcRequestRpcProtocol) {
        return true;
    }

    /**
     * 解析客户端的请求
     *
     * @param requestRpcProtocol
     * @return
     */
    private void processClientMessage(RpcProtocol requestRpcProtocol, ChannelHandlerContext ctx) {

        // 1.协议解析
        ReqType reqType = ReqType.findByCode(requestRpcProtocol.getHeader().getReqType());
        switch (reqType) {
            case REGISTER:
                doUserRegister(requestRpcProtocol, ctx);
                break;
            default:
                log.error("位置的协议类型:{}", reqType.code());
                break;
        }
    }

    /**
     * 加入缓存
     *
     * @param requestRpcProtocol
     * @param ctx
     */
    public void doUserRegister(RpcProtocol requestRpcProtocol, ChannelHandlerContext ctx) {

        // 1.模拟zk 超时 Client session timed out, have not heard from server in 17718ms
        UserRegister userRegister = (UserRegister) requestRpcProtocol.getContent();

        // 1.构造LocalSessionImpl
        LocalSessionImpl localSession = new LocalSessionImpl(ctx.channel());
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(userRegister.getUserId());
        loginUser.setPlatform(userRegister.getPlatform());
        localSession.setLoginUser(loginUser);

        // 2.设置当前 channel的用户属性-->sessionId
        localSession.bind();

        // 3.记录相关本地会话 LocalSessionMap
        // 内存、缓存Redis等
        ServerSessionManger.inst().addLocalSessionImpl(localSession);

        // 3.通知客户端：登录成功
        RpcProtocol responseProtocol = new RpcProtocol();
        // 构造 header
        Header header = requestRpcProtocol.getHeader();
        header.setReqType(ReqType.REGISTER_RESP.code());
        responseProtocol.setHeader(header);

        UserRegisterResp userRegisterResp = assembleRegisterResponse(userRegister);
        String sessionId = localSession.getSessionId();
        userRegisterResp.setSessionId(sessionId);
        responseProtocol.setContent(userRegisterResp);

        // 返回响应结果
        localSession.writeAndFlush(responseProtocol);
    }


    /**
     * 客户端信息注册响应
     *
     * @param userRegister
     * @return
     */
    private UserRegisterResp assembleRegisterResponse(UserRegister userRegister) {

        // 1.构造rpcResponse
        UserRegisterResp userRegisterResp = new UserRegisterResp();

        // 2.业务处理
        String userId = userRegister.getUserId();

        // 3.消息组装
        // 3.1.content,服务端调用,获得内容体
        userRegisterResp.setResult("success");

        // 数据交互回传流程:
        // C1开始(请求ID C2 开始)--gatewayByC2-->Rest转发给 Server2-->长连接发给 C2
        // C2(响应D C1 开始)-->gatewayByC1-->Rest转发给 Server1-->长连接发给C1结束

        String resultData = String.format("%s 注册成功", userId);
        userRegisterResp.setMsg(resultData);

        // 4.返回
        return userRegisterResp;
    }

}
