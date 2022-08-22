package com.lyuf.controller;

import com.lyuf.pojo.User;
import com.lyuf.service.serviceimpl.FileServiceImpl;
import com.lyuf.utils.Constants;
import com.lyuf.utils.Enum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author lyuf
 * @Date 2021/7/26 15:26
 * @Version 1.0
 */
//@Slf4j

@RequestMapping("/files")
@Controller
@CrossOrigin
@Slf4j
public class FileController {

    @Autowired
    FileServiceImpl fileService;


    @GetMapping("/fileAll")
    public String fileAll(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        try {
            List<com.lyuf.pojo.File> fileUploadsList = fileService.find(null, null, null, user.getName());
            fileUploadsList.forEach(file -> {
                int i = file.getFilename().indexOf("-");
                String substring = file.getFilename().substring(i + 1);
                file.setFilename(substring);
            });
            model.addAttribute("fileUploadsList", fileUploadsList);
        } catch (Exception e) {
            log.error("findAll");
        }
        return "/fileAll";
    }

    @GetMapping("/fileByName")
    public String fileByName(HttpSession session, Model model, String name) {
        User user = (User) session.getAttribute("user");
        try {
            List<com.lyuf.pojo.File> fileUploadsList = fileService.find(null, name, null, user.getName());
            fileUploadsList.forEach(file -> {
                int i = file.getFilename().indexOf("-");
                String substring = file.getFilename().substring(i + 1);
                file.setFilename(substring);
            });
            model.addAttribute("fileUploadsList", fileUploadsList);
        } catch (Exception e) {
            log.error("findAll");
        }
        return "/fileAll";
    }


    @RequestMapping("/find")
    public List<com.lyuf.pojo.File> fileFind(HttpSession session, Integer id, String filaname, Date time) {
        User user = (User) session.getAttribute("user");
        if (fileService.find(id, filaname, time, user.getName()) != null) {
            return fileService.find(id, filaname, time, user.getName());
        } else {
            return null;
        }
    }


    @RequestMapping("/upload")
    @Transactional
    public String upload(MultipartFile file, HttpSession session, RedirectAttributes attributes) throws IOException {
        User user = (User) session.getAttribute("user");
        if (file != null) {
            String path = Constants.BASE_FILE_PATH;
            File file1 = new File(path);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            String filename = file.getOriginalFilename();
//            String replace = UUID.randomUUID().toString().replace("-", "");
            filename = user.getName() + "-" + filename;
            file.transferTo(new File(path, filename));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd '--' HH:mm:ss a");
            Date date = new Date(System.currentTimeMillis());

            com.lyuf.pojo.File file2 = new com.lyuf.pojo.File();
            file2.setFilename(filename);
            file2.setFilesize(fileSize(file));
            file2.setFilepath(Constants.BASE_FILE_PATH + filename);
            file2.setFiletype(file.getContentType());
            file2.setTime(formatter.format(date));
            String localHost = String.valueOf(InetAddress.getLocalHost());
            file2.setIp(localHost);
            file2.setUser(user.getName());
            System.out.println(file2);
            fileService.addFile(file2);
            List<com.lyuf.pojo.File> b = fileService.find(null, filename, null, user.getName());
            if (b != null) {
                attributes.addFlashAttribute("msg", "保存成功！");
            } else {
                attributes.addFlashAttribute("msg", "保存失败！");
            }
            System.out.println("上传结束");
            return "redirect:/files/fileAll";
        } else {
            return "请选择文件";
        }
    }

    @RequestMapping("/download")
    public String download(@RequestParam("filename") String filename, HttpServletResponse response) {

        //文件名称通过参数传递过来
        File file = new File(Constants.BASE_FILE_PATH + filename);
        if (file.exists()) {
            response.setContentType("application/force-download;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "fileName =" + filename);
            //每次读取字节数
            byte[] buffer = new byte[1024];

            try (//字节流输出
                 OutputStream outStream = response.getOutputStream();
                 //BufferedInputStream是一个带有缓冲区的输入流，将file读取到缓冲区
                 BufferedInputStream buffStream = new BufferedInputStream(new FileInputStream(file))) {
                int i = 0;
                //buffStream
                //每次调用read方法的时候，它首先尝试从buffStream里读取数据
                //返回读入buffer的总字节数，或者如果由于已到达流的末尾而没有更多数据返回-1
                while ((i = buffStream.read(buffer)) != -1) {
                    //将buffer中读入的数据写入到输出流中
//                    outStream.write(buffer);?
                    outStream.write(buffer, 0, i);
                }
                return "download success";
            } catch (Exception e) {
//                log.error("download error {}", e.getMessage());
            }
        }
        return null;
    }

    @GetMapping("/delete")
    public String delete(Integer id, RedirectAttributes attributes) throws FileNotFoundException {

        //先删除文件在删数据库中的信息
        com.lyuf.pojo.File fileUpload = fileService.findFileById(id);
        //根据数据库的信息拼接文件的全路径  资源路径+static+数据库已存入的文件路径+"/"+新文件名
        // 如：D:/IDEAworkspace/3SpringBoot_Workspace/functionDemo/FileUploadDemo01/target/classes/static/files/2020-11-03/
//        String globalPath = ResourceUtils.getURL("classpath:").getPath() + "static" + fileUpload.getPath() + "/";
        String globalPath = fileUpload.getFilepath();
        System.out.println(globalPath);
        File file = new File(globalPath, fileUpload.getFilepath());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        int b = fileService.deleteFileById(id);
        if (b == 1) {
            attributes.addFlashAttribute("msg", "删除成功！");
        } else {
            attributes.addFlashAttribute("msg", "删除失败！");
        }
        return "redirect:/files/fileAll";
    }

    //文件大小
    public String fileSize(MultipartFile file) {
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量

        DecimalFormat decimalFormat = new DecimalFormat();

        String msg;
        if (file.getSize() / MB >= 1) {
            msg = (decimalFormat.format(file.getSize() / MB)) + " MB";
        } else if (file.getSize() / GB >= 1) {
            msg = (decimalFormat.format(file.getSize() / GB)) + " GB";
        } else if (file.getSize() / KB >= 1) {
            msg = (decimalFormat.format(file.getSize() / KB)) + " KB";
        } else {
            msg = decimalFormat.format(file.getSize()) + " B";
        }
        return msg;
    }

}
