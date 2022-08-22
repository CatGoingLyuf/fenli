package com.lyuf.service;

import com.lyuf.pojo.User;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @Author lyuf
 * @Date 2021/7/23 15:39
 * @Version 1.0
 */
public interface UserService {

    User findByNameAndPwd(String name, String password);

    List<User> find ();

    void delete (Integer id);

    void add (User user) throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
