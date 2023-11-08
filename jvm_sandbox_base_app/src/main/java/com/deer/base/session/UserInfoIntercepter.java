package com.deer.base.session;

import com.deer.base.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

public class UserInfoIntercepter implements HandlerInterceptor {
private static AtomicInteger id = new AtomicInteger(1000);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //这里通常从request中取一些和前端定义的关键字从而约定session之间的信息
        //方便起见，我们这里进行数据mock
        User user= new User();
        id.incrementAndGet();
        user.setUserId(id.longValue());
        user.setNickName("user_"+id);
        SessionHolder.addUser(user);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionHolder.removeUser();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
