package com.deer.agent.sandbox;

import org.apache.commons.lang3.StringUtils;

public class Sender {
    public static Ret send(Object response, String methodId, Object[] request, String className, String methodName, String methodDesc) {
        if (StringUtils.equals(methodId, "1002--1")) {
            if (null == DataCache.get(methodId)) {
                return null;
            }else{
                return DataCache.get(methodId);
            }
        }
        Ret ret = new Ret();
        ContextData contextData = new ContextData();
        contextData.setMethodId(methodId);
        contextData.setResponse(response);
        contextData.setRequestParam(request);
        contextData.setClassName(className);
        contextData.setMethodName(methodName);
        contextData.setMethodDesc(methodDesc);
        ret.status=true;
        ret.result=contextData.getResponse();
        ret.response=response;
        ret.contextData=contextData;
        DataCache.put(methodId, ret);
        return ret;

    }
}
