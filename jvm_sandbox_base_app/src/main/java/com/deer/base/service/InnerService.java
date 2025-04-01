package com.deer.base.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class InnerService {
    public static void doCall(String a,String b) {
        System.out.println("in app: "+InnerService.class.getClassLoader());
        Thread currentThread = Thread.currentThread();
        System.out.println("app is running in thread: " + currentThread.getName() + " (ID: " + currentThread.getId() + ")");

        Random random = new Random();
        int i = random.nextInt(200);
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public int add(int a ,int b){
        return a+b;
    }
}
