package com.deer.base.service;

public class BaseService {
    public static void record(String value){
     //   System.out.println("one record:"+value);
        InnerService.doCall("hello","world");
    }


    public static void first() {
        System.out.println("测试结果：first");
        second();
    }

    public static void second() {
        System.out.println("测试结果：second");
        third();
    }

    public static void third() {
        System.out.println("测试结果：third");
    }

}
