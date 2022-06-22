//package com.kikop.utils;
//
//import com.kikop.registercenter.IRegisterCenter;
//import com.kikop.registercenter.impl.ZkRegisterCenterImpl;
//import com.kikop.utils.ip.IpUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author kikop
// * @version 1.0
// * @project myuserserver
// * @file RegisterUserService
// * @desc
// * @date 2021/12/27
// * @time 9:30
// * @by IDE IntelliJ IDEA
// */
//@Slf4j
//@Component
//public class ZkRegisterChatServerUtil {
//
//
//    @Value("${server.port}")
//    private int httpport;
//
//    @Value("${myserver.socketport}")
//    private String socketport;
//
//    @Value("${myserver.zk.addr}")
//    private String zkConnectString;
//
//    @Value("${myserver.zk.sessiontimeout}")
//    private int sessionTimeOut;
//
//
//    @Value("${myserver.zk.rootpath}")
//    private String rootpath;
//
//    private IRegisterCenter iRegisterCenter;
//
//    @PostConstruct
//    public void init() {
//        iRegisterCenter = new ZkRegisterCenterImpl(zkConnectString, rootpath, sessionTimeOut);
//    }
//
//    // 永久结点: /myimdemo/mychatserver
//    // 临时结点: /myimdemo/mychatserver/127.0.0.1:8086:9092
//    // [zk: localhost:2181(CONNECTED) 65] ls /mychatserver/127.0.0.1:8086:9092
//    // [127.0.0.1:8086:9092]
//
//    /**
//     * 注册当前服务到zk
//     */
//    public void registerChatServerInfoToZk() {
//        // 127.0.0.1
//        String hostAddress = IpUtil.getIp();
//        // 127.0.0.1:8080:9000
//        String zkpathValue = String.format("%s:%d:%s", hostAddress, httpport, socketport);
//        iRegisterCenter.registerSimpleEphmeralNode(zkpathValue);
//    }
//}
