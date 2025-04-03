package com.deer.base.controller;

import com.deer.base.domain.User;
import com.deer.base.service.BaseService;
import com.deer.base.service.HelloService;
import com.deer.base.session.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/sayHi/{userName}")
    public String sayHello(@PathVariable(name ="userName") String userName) {
        BaseService.record(userName);
        User user = SessionHolder.getCurrentUser();
        System.out.println(Thread.currentThread().getId()+"--"+Thread.currentThread().getName()+"--current user: "+ user);
        //lambda 语法
        Consumer<String> consumer = (s) -> System.out.println("in lambda: "+s);
        consumer.accept(userName);
        return helloService.sayHello(userName);
    }

}
