//package com.kikop.core.datasturct.innerds;
//
//import lombok.Data;
//
//import java.io.Serializable;
//
///**
// * @author kikop
// * @version 1.0
// * @project mycommon-protocol
// * @file RpcResponse
// * @desc
// * @date 2021/12/27
// * @time 9:30
// * @by IDE IntelliJ IDEA
// */
//@Data
//public class RpcResponse implements Serializable {
//
//    private String msg;      // 标志,成功:success;失败:fail
//
//    // 服务端集群环境,reqType不足以表达,需再次引入一个子类型,用于客户端响应处理,参考:ChatType
//    private byte chatType;
//    private String fromUserId;
//    private String toUserId;
//    private Object data;
//}
