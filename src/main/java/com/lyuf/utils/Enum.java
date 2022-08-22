package com.lyuf.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lyuf
 * @Date 2021/8/10 10:08
 * @Version 1.0
 */
public enum Enum {

    KEY1("1","描述1"),
    KEY2("2","描述2"),
    KEY3("3","描述3");

    Enum(String code,String text){
        this.code = code;
        this.text = text;
    }

    private String code;
    private String text;
    //用来存放enum集合
    private static Map<String,Enum> enmuMap = new HashMap<String,Enum>();

    //get方法
    public String getCode(){
        return code;
    }

    public String getText(){
        return text;
    }

    //初始化map
    static{
        for(Enum item : values()){
            enmuMap.put(item.getCode(),item);
        }
    }

    /**
     * 从初始化的map集合中获取
     */
    public static String getTextByCode(String code){
        return enmuMap.get(code).getText();
    }
}
