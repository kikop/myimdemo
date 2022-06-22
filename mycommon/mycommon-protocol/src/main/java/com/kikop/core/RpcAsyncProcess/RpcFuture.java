package com.kikop.core.RpcAsyncProcess;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-protocol
 * @file RpcFuture
 * @desc
 * @date 2021/12/27
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Data
public class RpcFuture<T> {

    /**
     * 用于设置 IO操作的结果
     * Netty通过 Promise对 Future进行扩展,
     * JDK Future自身并没有写操作相关的接口
     */
    private Promise<T> promise;

    public RpcFuture(Promise<T> promise) {
        this.promise = promise;
    }
}
