package com.kikop.client;

import com.kikop.handler.client.RpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
public class MyNettyClient {

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private GenericFutureListener<ChannelFuture> bindFutureListener = null;

    // 连接标志
    private boolean connectFlag = false;

    private String inetHost;
    private int inetPort;

    /**
     * 关闭监听器
     * closeChannelFutureListener
     */
    private ChannelFutureListener closeFutureListener = (ChannelFuture closeFuture) -> {
        Channel channel = closeFuture.channel();
        System.out.println("连接通道关闭!");
    };


    private GenericFutureListener<ChannelFuture> connectedFutureListener = null;
    /**
     * 连接监听器
     * connectChannelFutureListener
     */
    private ChannelFutureListener defaultConnectedFutureListener = (ChannelFuture connectFuture) -> {
        if (connectFuture.isSuccess()) { // 连接成功
            log.info("连接服务端成功:{}->{}!", connectFuture.channel().localAddress(), connectFuture.channel().remoteAddress());
            ChannelFuture channelFuture = connectFuture.channel().closeFuture().addListener(closeFutureListener);
        } else { // 连接失败
            log.info("连接服务端失败,5秒后重试...");
            // 线程已经开启,所有程序不会退出
            connectFuture.channel().eventLoop().schedule(() -> {
                doConnect();
            }, 3, TimeUnit.SECONDS);
        }
    };


    public MyNettyClient(String serverIp, int socketPort) {

        this.inetHost = serverIp;
        this.inetPort = socketPort;

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(inetHost, inetPort)
                .handler(new RpcClientInitializer()); // 配置 handler处理器
    }


    /**
     * 连接netty服务器
     * 发起异步连接请求
     */
    public void doConnect() {
        try {

            // 1.发起异步连接请求
            ChannelFuture connectFuture = bootstrap.connect();
            if (connectedFutureListener != null) {
                connectFuture.addListener(connectedFutureListener);
            } else {
                connectFuture.addListener(defaultConnectedFutureListener);
            }
//            connectFuture.sync(); // 等待连接成功

            // 2.等待直到连接关闭
//            ChannelFuture closeFuture = connectFuture.channel().closeFuture();
//            closeFuture.sync();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //  eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 服务关闭
     */
    public void stop() {
        if (null != eventLoopGroup) {
            Future<?> future = eventLoopGroup.shutdownGracefully();
        }
    }

    public void setConnectedFutureListener(GenericFutureListener<ChannelFuture> connectedFutureListener) {
        this.connectedFutureListener = connectedFutureListener;
    }

    public static void main(String[] args) throws InterruptedException {

        MyNettyClient myNettyClient = new MyNettyClient("192.168.174.110", 9092);
        myNettyClient.doConnect();

//        TimeUnit.SECONDS.sleep(8);
//        myNettyClient.close();
    }


}