package com.cky.controller;

import com.cky.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @ClassName CommonController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/26 9:40
 * @Version 1.0
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String BasePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();//原始文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID来重构文件名
        String fileName= UUID.randomUUID().toString()+suffix;
        //目录不存在
        File dir=new File(BasePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            file.transferTo(new File(BasePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse){
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(BasePath+name));
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
