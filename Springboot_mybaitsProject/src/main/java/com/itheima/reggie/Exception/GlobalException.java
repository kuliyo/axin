package com.itheima.reggie.Exception;


import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//拦截RestController,Controller注解
@ResponseBody
@Slf4j
public class GlobalException {
    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//处理SQL异常
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException ex){

        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }


    @ExceptionHandler(CustomEXception.class)
    public R<String> ExceptionHandler(CustomEXception ex){

        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
