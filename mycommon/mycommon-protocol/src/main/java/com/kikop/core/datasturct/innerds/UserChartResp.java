package com.kikop.core.datasturct.innerds;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcRequest
 * @desc 请求结构定义
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Data
public class UserChartResp implements Serializable {

    private String result;      // 标志,true表示发送成功，false表示发送失败
    private int code;
    private String msg;

    private String fromUserId; // 来源端
    private String toUserId;   // 目的端

}
