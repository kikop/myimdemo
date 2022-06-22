package com.kikop.codec;

import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.Header;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcEncoder
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */

    /**
     *
     * @param ctx
     * @param msg 输入参数
     * @param out 输出参数
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) throws Exception {

        log.info("=============begin RpcEncoder============");

        Header header = msg.getHeader();
        out.writeShort(header.getMagic()); //写入魔数
        out.writeByte(header.getSerialType()); //写入序列化类型
        out.writeByte(header.getReqType());//写入请求类型
        out.writeLong(header.getRequestId()); //写入请求id
        ISerializer serializer = SerializerManager.getSerializer(header.getSerialType());

        byte[] data = serializer.serialize(msg.getContent()); //序列化
        header.setLength(data.length); // 编码,Msg发出去之前,填上最终的内容长度

        out.writeInt(data.length); // 写入消息长度
        out.writeBytes(data);

        log.info("=============end RpcEncoder============");
    }
}