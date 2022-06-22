package com.kikop.constants;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcConstant
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class RpcConstant {


    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */

    // header部分的总字节数
    public final static int HEAD_TOTAL_LEN = 16;

    public final static int HEAD_SESSIONID_LEN = 32;
    // 魔数
    // 可快速的预校验
    public final static short MAGIC = 0xca;
}
