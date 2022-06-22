package com.kikop.handler.client;

import com.kikop.codec.RpcDecoder;
import com.kikop.codec.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcClientHandler
 * @desc 入栈, 接收结果响应 Response
 * @date 2022/3/13
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("begin initChannel");
        ch.pipeline()

                // 增加 LengthFieldBasedFrameDecoder 变长解码器,解决粘包问题
                // 长度偏移位置:12
                // 长度为:4
                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                        12, 4, 0, 0))
                .addLast(new LoggingHandler())
                .addLast(new RpcEncoder())
                .addLast(new RpcDecoder())
                .addLast(new RpcClientHandler());
    }
}
