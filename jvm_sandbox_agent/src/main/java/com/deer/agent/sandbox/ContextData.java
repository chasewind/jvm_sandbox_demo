package com.deer.agent.sandbox;

import lombok.Data;

@Data
public class ContextData {

    /**
     * 返回结果
     */
    private Object response;
    /**
     * 请求参数
     */
    private Object[] requestParam;
    /**
     * 方法ID
     */
    private String methodId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法描述
     */
    private String methodDesc;

}
