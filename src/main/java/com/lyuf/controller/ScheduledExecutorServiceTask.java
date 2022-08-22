package com.lyuf.controller;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author lyuf
 * @Date 2021/8/16 10:37
 * @Version 1.0
 */
public class ScheduledExecutorServiceTask {

    //延时时间
    private static final long DELAY = 1000;
    //间隔时间
    private static final long PERIOD = 5000;

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable is run : " + LocalDateTime.now());
            }
        };
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    }
}

