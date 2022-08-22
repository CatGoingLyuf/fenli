package com.lyuf.controller;

import com.lyuf.pojo.User;
import com.lyuf.service.serviceimpl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * @Author lyuf
 * @Date 2021/7/23 15:47
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/user")
//跨域 @CrossOrigin
@CrossOrigin
public class UserController {

    @Autowired
    UserServiceImpl userService;


    @RequestMapping("/login")
    public String login(String name, String password, HttpSession session) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (name != null && password != null) {

            //得到一个信息摘要器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //获取Base64编码算法工具
            BASE64Encoder base64Encoder = new BASE64Encoder();
            //进行编码加密
            String encodePassword = base64Encoder.encode(md5.digest(password.getBytes("UTF-8")));
            System.err.println(encodePassword);

            if (userService.findByNameAndPwd(name, encodePassword) != null) {
                User userLogin = userService.findByNameAndPwd(name, encodePassword);
                session.setAttribute("user", userLogin);
                return "redirect:/files/fileAll";
            } else {
                return "/error" ;
            }
        } else {
            return "/error";
        }
    }

    @RequestMapping("/find")
    public String find(@RequestParam(required = false) Integer id, @RequestParam("name") String name, @RequestParam("password") String password, Model model) {
        System.out.println(id + name + password);
        Objects.requireNonNull(name, password);
        if (name.equals("admin") && password.equals("lyuf1997")) {

            return "redirect:/user/userAll";
        }
        return "/adminlogin";
    }

    @RequestMapping("/userAll")
    public String userAll(Model model) {
        try {
            List<User> users = userService.find();
            model.addAttribute("usersList", users);
        } catch (Exception e) {
            log.error("/find");
        }
        return "/admin";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "/login";
    }

    @RequestMapping("/delete")
    public String delete(Integer id){
        userService.delete(id);
        return "redirect:/user/userAll";
    }

    @PostMapping("/add")
    public String add(User user) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        userService.add(user);
        return "redirect:/user/userAll";
    }

//    @RequestMapping("index")
//    public String getindex(Model model){
//        User user = new User(0, "lyuf", "adfad");
//
//        List<String> userList = new ArrayList<>();
//        userList.add("zhang san 66");
//        userList.add("li si 66");
//        userList.add("wang wu 66");
//
//        HashMap<String , String> map = new HashMap<>();
//        map.put("place","杭州");
//        map.put("feeling","very well");
//
//        model.addAttribute("name","lyuf");//普通字符串
//        model.addAttribute("user",user);//储存javabean
//        model.addAttribute("userList",userList);//储存List
//        model.addAttribute("map",map);//储存Map
//
//        return "index";
//    }

}
