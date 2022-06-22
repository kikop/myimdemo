package com.kikop.codec;

import com.kikop.constants.ReqType;
import com.kikop.constants.RpcConstant;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.core.datasturct.innerds.*;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcDecoder
 * @desc 入站解码器, 继承 ByteToMessageDecoder的解码器, 注意:粘包问题处理需再增加解码器:LengthFieldBasedFrameDecoder
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */

    /**
     * @param ctx
     * @param in  入站参数类型
     * @param out 出站参数类型
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.info("==========begin RpcDecoder ==============");

        if (in.readableBytes() < RpcConstant.HEAD_TOTAL_LEN) {
            // 消息长度不够，继续等待字节流,不需要解析
            return;
        }

        in.markReaderIndex();// 标记一个读取数据的索引，方面后续复位 ReaderIndex。

        short magic = in.readShort(); // 1.标志位,读取magic
        if (magic != RpcConstant.MAGIC) {
            throw new IllegalArgumentException("Illegal request parameter 'magic'," + magic);
        }
        byte serialType = in.readByte();   // 2.读取序列化算法类型
        byte reqType = in.readByte();      // 3.请求类型
        long requestId = in.readLong();    // 4.请求消息id

//        byte[] sessionIdBytes = new byte[RpcConstant.HEAD_SESSIONID_LEN];
//        in.readBytes(sessionIdBytes); // 改变指针位置
//        String sessionId = new String(sessionIdBytes);


        int dataLength = in.readInt();     // 5.请求数据长度

        // 可读区域的字节数小于实际数据长度,修改读的索引标志位
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex(); // 复位 ReaderIndex
            return;
        }

        // 读取消息内容
        byte[] content = new byte[dataLength];
        in.readBytes(content);

        // 构建header头信息
        Header header = new Header(magic, serialType, reqType, requestId, dataLength);
        ISerializer serializer = SerializerManager.getSerializer(serialType);
        ReqType rt = ReqType.findByCode(header.getReqType());
        RpcProtocol reqProtocol = new RpcProtocol<>();
        reqProtocol.setHeader(header);
        switch (rt) {
            case REGISTER: // 服务端收到数据处理:入栈,收到客户端的请求
                UserRegister userRegister = serializer.deserialize(content, UserRegister.class);
                reqProtocol.setContent(userRegister);
                out.add(reqProtocol);
                break;
            case REGISTER_RESP: // 客户端收到数据处理:入栈,接收响应数据
                UserRegisterResp userRegisterResp = serializer.deserialize(content, UserRegisterResp.class);
                reqProtocol.setContent(userRegisterResp);
                out.add(reqProtocol);
                break;
            case CHAT: // 客户端收到数据处理:入栈,接收响应数据
                UserChart userChart = serializer.deserialize(content, UserChart.class);
                reqProtocol.setContent(userChart);
                out.add(reqProtocol);
                break;
            case CHAT_RESP: // 客户端收到数据处理:入栈,接收响应数据
                UserChartResp userChartResp = serializer.deserialize(content, UserChartResp.class);
                reqProtocol.setContent(userChartResp);
                out.add(reqProtocol);
                break;

            case HEARTBEAT:
                break;
            default:
                break;
        }
        log.info("==========end RpcDecoder ==============");
    }
}
