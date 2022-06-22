package com.kikop.core.datasturct;

import com.kikop.core.datasturct.innerds.Header;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcProtocol
 * @desc 总的消息结构定义
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Data
public class RpcProtocol<T> implements Serializable {

    private Header header;

    // 泛型
    // 目前主要是:RpcRequest、RpcResponse、RpcContent
    private T content;
}