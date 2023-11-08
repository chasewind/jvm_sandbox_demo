package com.deer.base.thread;

import java.util.concurrent.*;

public class ThreadPoolExecutorWrapper extends ThreadPoolExecutor
{
    public ThreadPoolExecutorWrapper(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableFuture<Void> newTask = newTaskFor(new RunnableWrapper(task), null);
        execute(newTask);
        return newTask;
    }
}
