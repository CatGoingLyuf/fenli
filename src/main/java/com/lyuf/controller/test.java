package com.lyuf.controller;

import com.lyuf.utils.Enum;

/**
 * @Author lyuf
 * @Date 2021/8/10 10:13
 * @Version 1.0
 */
public class test {

    public String en(String code) {
        System.out.println(Enum.getTextByCode(code));
        return Enum.getTextByCode(code);
    }

    public static void main(String[] args) {
        new test().en("1");
    }
}
