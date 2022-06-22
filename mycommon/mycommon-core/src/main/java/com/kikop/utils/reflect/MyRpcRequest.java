package com.kikop.utils.reflect;


import lombok.Data;

/**
 * 只有内部类可以为static
 * https://blog.csdn.net/quanaianzj/article/details/82348982
 */
@Data
public class MyRpcRequest {
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
