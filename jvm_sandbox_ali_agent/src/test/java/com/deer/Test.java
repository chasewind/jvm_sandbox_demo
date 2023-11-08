package com.deer;

import com.alibaba.jvm.sandbox.spy.Spy;

public class Test {
    public int add(int a ,int b){
        return a+b;
    }
    public static int calculate(){
        int a=1;
        int b=2;
        int c=(a+b)*10;
        return c;
    }

    public void test() {
        try {
            System.out.println("Before Sleep");
            Thread.sleep(1000);
            System.out.println("After Sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public   int calculateNumber(){
        int a=1;
        int b=2;
        int c=(a+b)*10;
        return c;
    }

}
