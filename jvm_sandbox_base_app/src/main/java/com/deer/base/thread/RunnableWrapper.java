package com.deer.base.thread;

import com.deer.base.service.impl.HelloServiceImpl;

public class RunnableWrapper implements Runnable {
    private Runnable task;
    private Integer passedValue;

    public RunnableWrapper(Runnable task) {
        this.task = task;
        this.passedValue = HelloServiceImpl.threadLocal.get();
    }

    @Override
    public void run() {
        HelloServiceImpl.threadLocal.set(passedValue);
        task.run();
    }
}
