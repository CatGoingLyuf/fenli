package com.lyuf.service.serviceimpl;

import com.lyuf.dao.UserMapper;
import com.lyuf.pojo.User;
import com.lyuf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @Author lyuf
 * @Date 2021/7/23 15:38
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User findByNameAndPwd(String name, String password) {
        return userMapper.findByNameAndPwd(name,password);
    }

    @Override
    public List<User> find(){
        return userMapper.find();
    }

    @Override
    public void delete(Integer id) {
        userMapper.delete(id);
    }

    @Override
    public void add(User user) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //md5 加密
        //得到一个信息摘要器
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        //获取Base64编码算法工具
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //进行编码加密
        String encodePassword = base64Encoder.encode(md5.digest(user.getPassword().getBytes("UTF-8")));

        user.setPassword(encodePassword);
        userMapper.add(user);
    }
}
