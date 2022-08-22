package com.lyuf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author lyuf
 * @Date 2021/8/16 10:22
 * @Version 1.0
 */
@Component
public class TimeTask {

    //延时时间
    private static final long DELAY = 1000;
    //间隔时间
    private static final long PERIOD = 5000;

    public static void main(String[] args) {
        // 定义要执行的任务
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("timeTask is run : " + LocalDateTime.now());
            }
        };
        Timer timer = new Timer();
        timer.schedule(timeTask,DELAY,PERIOD);
    }
}
