package com.kikop.registercenter.impl;

import com.kikop.constants.DataSerialType;
import com.kikop.registercenter.IRegisterCenter;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file ZkRegisterCenterImpl
 * @desc 基于zk的注册中心(by curator - framework)
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
public class ZkRegisterCenterImpl implements IRegisterCenter {

    private String connectString = "";

    public String getZkParentNode() {
        return zkParentNode;
    }

    public void setZkParentNode(String zkParentNode) {
        this.zkParentNode = zkParentNode;
    }

    private String zkParentNode;

    // 默认 fastJson序列化
    private byte serialType = DataSerialType.JSON_SERIAL.code();


    /**
     * zk客户端调用API接口
     */
    private CuratorFramework curatorFramework = null;


    /**
     * 构造函数
     *
     * @param connectString
     * @param zkParentNode   根节点,一般服务名作为次参数
     * @param sessionTimeOut 4000
     */
    public ZkRegisterCenterImpl(String connectString, String zkParentNode, int sessionTimeOut) {

        this.connectString = connectString;
        this.zkParentNode = zkParentNode;

        // 内部启动两个线程
        // 一个用于监听
        // 一个用于后台操作轮询
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeOut)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }


    /**
     * 创建父节点
     *
     * @param persistServiceNodePath 父节点路径  必须 begin with:/A/B
     */
    private void createParentIfNeeded(String persistServiceNodePath) {

        try {
            Stat stat = curatorFramework.checkExists().forPath(persistServiceNodePath);
            if (null == stat) {
                String pathRegisterResult = curatorFramework
                        .create()
                        .creatingParentsIfNeeded()
//                        .withProtection() // 添加保护 todo
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(persistServiceNodePath);
                System.out.println("pathRegisterResult:" + pathRegisterResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" createParentIfNeeded error {} ", e.getMessage());
        }

    }

    /**
     * 服务注册(非顺序、未序列化、无负载)
     *
     * @param servieAddress 服务地址 192.168.174.110:7082
     */
    @Override
    public void registerSimpleEphmeralNode(String servieAddress) {

        try {
            // 永久节点: /myimdemo/mychatserver
            String persistServiceNodePath = getZkParentNode();
            // 临时节点: /myuserserver/192.168.174.110:7082
            String strReqTmpNode = persistServiceNodePath + "/" + servieAddress; // 手动加上了父节点

//            // 永久节点: /myimdemo/mychatserver
//            String persistServiceNodePath = "/myimdemo6/myuserserver";
//            // 临时节点: /myuserserver/192.168.174.110:7082
//            String strReqTmpNode = persistServiceNodePath + "/" + servieAddress;
//            // 1.创建父节点
//            createParentIfNeeded(persistServiceNodePath);

            // 2.创建临时节点
            // 2.1.判断临时节点是否已经存在
            Stat stat = curatorFramework.checkExists().forPath(strReqTmpNode);
            if (null != stat) {
                Void aVoid = curatorFramework.delete().forPath(strReqTmpNode);
                log.info("节点删除成功:{}", strReqTmpNode);
            }

            // 2.2.创建临时节点
            String payLoad = "kikop"; // 临时节点的值
            String strResultNode = curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(strReqTmpNode, payLoad.getBytes());
            // --> ls /myuserserver
            // --> ls /myuserserver/192.168.174.110:7082
            // --> get /myuserserver/192.168.174.110:7082

            // 2.3.测试读取payLoad
            byte[] bytes = curatorFramework.getData().forPath(strReqTmpNode);
            String checkPayLoad = new String(bytes, "UTF-8");
            log.info("payLoad is:{}", checkPayLoad);

            log.info("zk 服务注册成功:{}", strResultNode);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(".register 运行异常{}", ex);
        }
    }

    @Override
    public String registerEphmeralSeqNode(String childPathPrefix, Object payLoadObj) {
        try {
            // 1.创建父节点
            // 永久节点: /im/nodes
            createParentIfNeeded(getZkParentNode());

            // 2.创建顺序临时节点
            // 临时顺序节点: /im/nodes/seq-
            ISerializer serializer = SerializerManager.getSerializer(serialType);
            byte[] serializePayload = serializer.serialize(payLoadObj);

            String strReqTmpNode = getZkParentNode() + "/" + childPathPrefix; // 手动加上了父节点

            String pathRegistered = curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(strReqTmpNode, serializePayload); // todo

            // 2.3.测试读取payLoad
            byte[] checkPayLoadBytes = curatorFramework.getData().forPath(pathRegistered);
            log.info("zk 服务注册成功:{},payLoad:{}", pathRegistered, new String(checkPayLoadBytes, "UTF-8"));
            return pathRegistered;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(".register 运行异常{}", ex);
        }
        return null;
    }


    @Override
    public void updateServerNodePayLoad(String pathRegistered, Object payLoadObj) {
        try {
            ISerializer serializer = SerializerManager.getSerializer(serialType);
            byte[] serializePayload = serializer.serialize(payLoadObj);
            curatorFramework.setData().forPath(pathRegistered, serializePayload);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateServerNodePayLoad error!");
        }
    }
}