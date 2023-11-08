package com.deer.agent.sandbox.trace;


public class MethodRecorder {
    public static MethodResult before(Object[] request, String className, String methodName, String methodDesc) {
        MethodResult methodResult = new MethodResult();
        methodResult.state = 1;
        methodResult.result = null;
        methodResult.response = null;
        return methodResult;
    }

    public static MethodResult after(Object response, Object[] request, String className, String methodName, String methodDesc) {
        MethodResult methodResult = new MethodResult();
        methodResult.state = 1;
        methodResult.result = response;
        methodResult.response = response;
        return methodResult;
    }

    public static MethodResult exception(Throwable throwable, String className, String methodName, String methodDesc) {
        MethodResult methodResult = new MethodResult();
        methodResult.state = 1;
        methodResult.result = null;
        methodResult.response = null;
        return methodResult;
    }

}
