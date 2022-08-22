package com.lyuf.service;

import com.lyuf.pojo.File;
import com.lyuf.pojo.User;

import java.util.Date;
import java.util.List;

/**
 * @Author lyuf
 * @Date 2021/7/26 15:27
 * @Version 1.0
 */
public interface FileService {
    void addFile(File file);

    List<File> find (Integer id, String filename, Date time,String user);

    File findFileById(Integer id);
    int deleteFileById(Integer id);
}
