package com.deer.agent;

public class SpringControllerInterceptor extends PreciseInterceptor{
    @Override
    public String getDescription() {
        return "拦截spring 的Controller，RestController，请尽可能的使用项目对应的包名，避免对spring相关包的依赖";
    }

}
