package com.kikop.handler.server.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-zk
 * @file MyChatServerManager
 * @desc 原 imnode
 * 每个MyChatServe对应一个 MyChatServerNode和
 * 单列MyChatServerManager、单列MyChatServerRoute
 * @date 2020/8/29
 * @time 20:23
 * @by IDE: IntelliJ IDEA
 */
// 必须加注解,否则反序列化异常
@Data
public class MyChatServerNode implements Comparable<MyChatServerNode>, Serializable {

    private static final long serialVersionUID = 1L;

    // worker 的 Id
    // zookeeper负责生成
    private long id;

    // Netty 服务 的连接数
    private Integer balance = 0;

    // Netty 服务 IP
    private String host;

    // Netty 服务 端口
    private Integer port;

    // web服务端口
    private Integer httpport;

    // 注册到zk中的节点路径
    // Eg:"/mychatserver/seq0000000039
    private String pathRegister;

    public MyChatServerNode() {
    }

    public MyChatServerNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "MyChatServerNode{" +
                "id=" + id +
                ", balance=" + balance +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyChatServerNode that = (MyChatServerNode) o;
        return
                Objects.equals(host, that.host) &&
                        Objects.equals(pathRegister, that.pathRegister) &&  // 增加服务节点的匹配条件
                        Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }


    public void incrementBalance() {
        balance++;
    }

    public void decrementBalance() {
        balance--;
    }

    /**
     * mychatserver根据负载升序排列
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(MyChatServerNode o) {
        Integer balance = this.balance;
        Integer other = o.balance;
        if (balance > other) {
            return 1;
        } else if (balance < other) {
            return -1;
        }
        return 0;
    }


}
