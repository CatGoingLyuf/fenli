package com.lyuf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author lyuf
 * @Date 2021/8/11 15:40
 * @Version 1.0
 */
@RestController
public class RedisController {

    @Autowired
    RedisTemplate redisTemplate;


//String类型相关操作
    public void test() {
        //通过redisTemplate设置值
        redisTemplate.boundValueOps("StringK").set("StringV");
        redisTemplate.boundValueOps("StringK").set("StringV",1, TimeUnit.MINUTES);
        //通过ValueOperations设置值
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("StringK2","StringV2");
        valueOperations.set("StringK2","StringV2",1,TimeUnit.MINUTES);

        //设置过期时间（单独设置）
//        redisTemplate.boundValueOps("StringK").expire(1,TimeUnit.MINUTES);


//        String stringK = (String) redisTemplate.boundValueOps("StringK").get();
    }

    public static void main(String[] args) {
        new test();
    }
}
