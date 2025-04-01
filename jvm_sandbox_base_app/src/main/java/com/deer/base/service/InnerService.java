package com.deer.base.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class InnerService {
    public static void doCall(String a,String b) {
        Random random = new Random();
        int i = random.nextInt(2000);
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
