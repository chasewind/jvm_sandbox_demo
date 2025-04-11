package com.deer.base.service.impl;

import com.deer.base.domain.User;
import com.deer.base.service.BaseService;
import com.deer.base.service.HelloService;
import com.deer.base.service.InnerService;
import com.deer.base.session.SessionHolder;
import com.deer.base.thread.RunnableWrapper;
import com.deer.base.thread.ThreadPoolExecutorWrapper;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class HelloServiceImpl implements HelloService {
    private AtomicLong seq = new AtomicLong(1L);
    public static InheritableThreadLocal<Integer>threadLocal = new InheritableThreadLocal<>();
    private ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);
    private static ExecutorService executorWrapper = new ThreadPoolExecutorWrapper(1, 1,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
    @Override
    public String sayHello(String userName) {
        threadLocal.set(seq.intValue());
        doOtherBiz();
        User user = SessionHolder.getCurrentUser();
        return "hello ," + userName + ",seq = " + seq.incrementAndGet()+","+user.getNickName();
    }

    @Override
    public void sayA() {
        BaseService.record("jack");
        BaseService.record("json");
    }

    private void doOtherBiz() {
        System.out.println("before :outer thread "+threadLocal.get());
//        new Thread(()->{
//            System.out.println("after : inner thread "+threadLocal.get());
//        }).start();
//        //
//        THREAD_POOL.execute(()->{
//            System.out.println("after in thread pool :  "+Thread.currentThread().getName()+" "+threadLocal.get());
//        });
//
//        THREAD_POOL.submit(new RunnableWrapper(()->{
//            System.out.println("after in RunnableWrapper :  "+Thread.currentThread().getName()+" "+threadLocal.get());
//        }));
//        executorWrapper.submit(()->{
//            System.out.println("after in ThreadPoolExecutorWrapper :  "+Thread.currentThread().getName()+" "+threadLocal.get());
//        });
    }
}
