package com.deer.base.controller;

import com.deer.base.domain.User;
import com.deer.base.service.BaseService;
import com.deer.base.service.HelloService;
import com.deer.base.session.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/sayHi/{userName}")
    public String sayHello(@PathVariable(name ="userName") String userName) {
        BaseService.record(userName);
        User user = SessionHolder.getCurrentUser();
     //   System.out.println(Thread.currentThread().getId()+"--"+Thread.currentThread().getName()+"--current user: "+ user);
        return helloService.sayHello(userName);
    }

}
