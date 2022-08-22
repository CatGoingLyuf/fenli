package com.lyuf.dao;

import com.lyuf.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @Author lyuf
 * @Date 2021/7/23 15:10
 * @Version 1.0
 */
@Repository
@Mapper
public interface UserMapper {

    User findByNameAndPwd(String name, String password);

    List<User> find();

    void delete (Integer id);

    void add (User user);
}
