package com.deer.base.service;

public class BaseService {
    public static void record(String value){
     //   System.out.println("one record:"+value);
        InnerService.doCall("hello","world");
    }
}
