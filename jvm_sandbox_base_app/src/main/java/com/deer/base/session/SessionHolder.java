package com.deer.base.session;

import com.deer.base.domain.User;

public class SessionHolder {
    private static  ThreadLocal<User>threadLocal = new ThreadLocal<>();

    public static  User getCurrentUser(){
        return threadLocal.get();
    }

    public static void addUser(User user){
        threadLocal.set(user);
    }
    public static void removeUser(){
        threadLocal.remove();
    }
}
