package com.lyuf.service.serviceimpl;

import com.lyuf.dao.FileMapper;
import com.lyuf.pojo.File;
import com.lyuf.pojo.User;
import com.lyuf.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author lyuf
 * @Date 2021/7/26 15:27
 * @Version 1.0
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileMapper fileMapper;

    @Override
    public void addFile(File file) {
        fileMapper.addFile(file);
    }

    @Override
    public List<File> find(Integer id, String filename, Date time, String user) {
        return fileMapper.find(id,filename,time,user);
    }

    @Override
    public File findFileById(Integer id) {
        return fileMapper.findFileById(id);
    }

    @Override
    public int deleteFileById(Integer id) {
        return fileMapper.deleteFileById(id);
    }
}
