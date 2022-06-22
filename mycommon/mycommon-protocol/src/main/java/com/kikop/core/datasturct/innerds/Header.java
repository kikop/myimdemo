package com.kikop.core.datasturct.innerds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file Header
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Header implements Serializable {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */
    private short magic;     // 魔数-用来验证报文的身份（2个字节）
    private byte serialType; // 序列化类型（1个字节）
    private byte reqType;    // 操作类型（1个字节）,参考枚举 ReqType
    private long requestId;  // 消息源ID（8个字节）,基于 AtomicLong.incrementAndGet
//    private String sessionId; // 客户端的 sessionId(32byte)
    private int length;      // 数据长度（4个字节）

}