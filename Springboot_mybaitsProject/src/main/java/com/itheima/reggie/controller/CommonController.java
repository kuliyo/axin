package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String reggiePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件名后缀
        String substring = originalFilename.substring(originalFilename.indexOf("."));

        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + substring;

        //创建文件目录对象
        File dir = new File(reggiePath);
        //判断是否存在目录
        if (!dir.exists()){
            //目录不存在，创建目录
            dir.mkdir();
        }

        try {
            //将临时文件转存指定位置
            file.transferTo(new File(reggiePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(reggiePath + name));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream servletOutputStream = response.getOutputStream();

            //设置响应回去的文件类型
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                servletOutputStream.write(bytes,0,len);
                servletOutputStream.flush();
            }

            //关闭资源
            fileInputStream.close();
            servletOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
