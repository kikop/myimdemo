package com.kikop.handler.server;

import com.kikop.codec.RpcDecoder;
import com.kikop.codec.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcServerInitializer
 * @desc
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // 基于长度的变长解码器:LengthFieldBasedFrameDecoder,解决粘包问题
                // 长度偏移位置:12,
                // 长度为:4
                // 不跳过,最后的消息体全部暴露
                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                        12, 4, 0, 0))
                .addLast(new LoggingHandler(LogLevel.INFO))
                .addLast(new RpcDecoder())
                .addLast(new RpcEncoder())
                .addLast(new RpcServerHandler());
    }
}
