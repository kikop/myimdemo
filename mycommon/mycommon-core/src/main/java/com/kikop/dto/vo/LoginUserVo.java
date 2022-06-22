package com.kikop.dto.vo;


import java.io.Serializable;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-core
 * @file LoginUserVo
 * @desc 登录用户视图
 * @date 2022/3/11
 * @time 10:50
 * @by IDE IntelliJ IDEA
 */
public class LoginUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识 jwttoken
     * (uuid)
     */
    private String accessToken;

    /**
     * 用户名id
     * (底层来自数据库:db.sysUser.userId)
     */
    private String userid;

    /**
     * 用户名
     * (底层来自数据库:db.sysUser.userId)
     */
    private String username;

    /**
     * 登录时间
     * Long类型
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 长连接的chatServer
     */
    private String chatServer;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getChatServer() {
        return chatServer;
    }

    public void setChatServer(String chatServer) {
        this.chatServer = chatServer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
