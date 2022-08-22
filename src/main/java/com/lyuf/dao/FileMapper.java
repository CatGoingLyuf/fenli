package com.lyuf.dao;

import com.lyuf.pojo.File;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author lyuf
 * @Date 2021/7/26 15:28
 * @Version 1.0
 */
@Repository
@Mapper
public interface FileMapper {
    void addFile(File file);

    List<File> find (Integer id, String filename, Date time,String user);

    File findFileById(Integer id);

    int deleteFileById(Integer id);
}
