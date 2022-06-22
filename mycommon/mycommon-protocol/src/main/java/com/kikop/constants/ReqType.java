package com.kikop.constants;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file ReqType
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public enum ReqType {

    REGISTER((byte) 0),            // 向服务端注册,实现下线用户的缓存
    REGISTER_RESP((byte) 1),       // 注册确认
    LOGOUT((byte) 2),
    LOGOUT_RESP((byte) 3),
    HEARTBEAT((byte) 4),
    HEARTBEAT_RESP((byte) 5),       // 注册确认
    CHAT((byte) 6),      // 单聊(C1-->C2)
    CHAT_RESP((byte) 7),       // 组聊
    MESSAGENOTIFICATION((byte) 8);  // 组聊确认(C2-->C1)

    private byte code;

    ReqType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public static ReqType findByCode(int code) {

        for (ReqType msgType : ReqType.values()) {
            if (msgType.code() == code) {
                return msgType;
            }
        }
        return null;
    }
}
