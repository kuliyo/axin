package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class SpringbootMybaitsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybaitsProjectApplication.class, args);
//        输出日志
        log.info("项目启动成功....");    //Slf4j注解
    }
}
