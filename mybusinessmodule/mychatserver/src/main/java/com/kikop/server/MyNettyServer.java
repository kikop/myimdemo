package com.kikop.server;

import com.kikop.handler.server.RpcServerInitializer;
import com.kikop.utils.ip.IpUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserer
 * @file MyNettyServer
 * @desc
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class MyNettyServer {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private String hostIp = "";
    private int socketport = 9092;

    private GenericFutureListener<ChannelFuture> bindFutureListener = null;
    private GenericFutureListener<ChannelFuture> closeFutureListener = null;
    private ChannelFuture serverBindChannelFuture = null;

    GenericFutureListener<ChannelFuture> defaultBindFutureListener = (ChannelFuture f) -> {
        if (f.isSuccess()) {
            log.info("startNettyServer success on :{}->{}!", f.channel().localAddress(), f.channel().remoteAddress());
        } else {
            log.info("startNettyServer fail on :{}->{}!", f.channel().localAddress(), f.channel().remoteAddress());
        }
    };


    public MyNettyServer(String chatServerIp, int socketport) {
        this.hostIp = chatServerIp;
        this.socketport = socketport;
    }

    public void setBindFutureListener(GenericFutureListener<ChannelFuture> bindFutureListener) {
        this.bindFutureListener = bindFutureListener;
    }

    /**
     * 资源释放
     */
    public void stop() {
        try {
            log.info("mychatserver服务端即将关闭...");
            if (serverBindChannelFuture != null && serverBindChannelFuture.channel().isActive()) {
                serverBindChannelFuture.channel().close();
            }
            Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
            Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
            bossGroupFuture.await();
            workerGroupFuture.await();
            log.info("mychatserver服务端关闭完成.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("mynettyserver运行异常:{}", e);
        }
    }

    /**
     * 启动 Netty服务
     *
     * @throws Exception
     */
    public void start() {
        // 作为线程启动的好处
        try {

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(hostIp, socketport)
                    // 服务于子线程组
                    .childHandler(new RpcServerInitializer());
//            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            // 等待绑定结果
            serverBindChannelFuture = serverBootstrap.bind().sync();
            if (null != bindFutureListener) {
                serverBindChannelFuture.addListener(bindFutureListener);
            } else {
                serverBindChannelFuture.addListener(defaultBindFutureListener);
            }
        } catch (Exception e) {
            log.error("startNettyServer Exception", e);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String hostIp = IpUtils.getHostIp();
        MyNettyServer myNettyServer = new MyNettyServer(hostIp, 9092);
        myNettyServer.start();
        TimeUnit.SECONDS.sleep(5);
        myNettyServer.stop();
    }

    public GenericFutureListener<ChannelFuture> getCloseFutureListener() {
        return closeFutureListener;
    }

    public void setCloseFutureListener(GenericFutureListener<ChannelFuture> closeFutureListener) {
        this.closeFutureListener = closeFutureListener;
    }
}
