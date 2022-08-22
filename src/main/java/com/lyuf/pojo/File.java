package com.lyuf.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author lyuf
 * @Date 2021/7/26 15:18
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private int id;
    private String filename;
    private String filepath;
    private String filetype;
    private String filesize;
    private String time;
    private String ip;
    private String user;

}
