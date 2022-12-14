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
                attributes.addFlashAttribute("msg", "???????????????");
            } else {
                attributes.addFlashAttribute("msg", "???????????????");
            }
            System.out.println("????????????");
            return "redirect:/files/fileAll";
        } else {
            return "???????????????";
        }
    }

    @RequestMapping("/download")
    public String download(@RequestParam("filename") String filename, HttpServletResponse response) {

        //????????????????????????????????????
        File file = new File(Constants.BASE_FILE_PATH + filename);
        if (file.exists()) {
            response.setContentType("application/force-download;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "fileName =" + filename);
            //?????????????????????
            byte[] buffer = new byte[1024];

            try (//???????????????
                 OutputStream outStream = response.getOutputStream();
                 //BufferedInputStream??????????????????????????????????????????file??????????????????
                 BufferedInputStream buffStream = new BufferedInputStream(new FileInputStream(file))) {
                int i = 0;
                //buffStream
                //????????????read????????????????????????????????????buffStream???????????????
                //????????????buffer????????????????????????????????????????????????????????????????????????????????????-1
                while ((i = buffStream.read(buffer)) != -1) {
                    //???buffer???????????????????????????????????????
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

        //??????????????????????????????????????????
        com.lyuf.pojo.File fileUpload = fileService.findFileById(id);
        //????????????????????????????????????????????????  ????????????+static+?????????????????????????????????+"/"+????????????
        // ??????D:/IDEAworkspace/3SpringBoot_Workspace/functionDemo/FileUploadDemo01/target/classes/static/files/2020-11-03/
//        String globalPath = ResourceUtils.getURL("classpath:").getPath() + "static" + fileUpload.getPath() + "/";
        String globalPath = fileUpload.getFilepath();
        System.out.println(globalPath);
        File file = new File(globalPath, fileUpload.getFilepath());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        int b = fileService.deleteFileById(id);
        if (b == 1) {
            attributes.addFlashAttribute("msg", "???????????????");
        } else {
            attributes.addFlashAttribute("msg", "???????????????");
        }
        return "redirect:/files/fileAll";
    }

    //????????????
    public String fileSize(MultipartFile file) {
        int GB = 1024 * 1024 * 1024;//??????GB???????????????
        int MB = 1024 * 1024;//??????MB???????????????
        int KB = 1024;//??????KB???????????????

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
