package com.deer.special;

/**
 * copy from sky-walking
 */
public class RunnableWrapper implements Runnable {
    final Runnable runnable;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    public static RunnableWrapper of(Runnable r) {
        return new RunnableWrapper(r);
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}
