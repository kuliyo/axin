package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 移动端用户登录
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){

        //获取手机号
        String phone = user.getPhone();

        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}",code);

            //调用短信服务API发送验证码
            //SMSUtils.sendMessage("","","");

            //将生成的验证码保存到session
            session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }
        return R.success("手机验证码发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session获得保存的验证码
        Object attribute = session.getAttribute(phone);
        //进行验证码对比(页面提交的验证码和session中保存的验证码进行对比)
        if (attribute != null && attribute.equals(code)){

            //如果对比成功，登录成功
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);

            //判断当前手机号对应的用户是否为新用户
            User one = userService.getOne(lqw);
            if (one == null){
                //如果为新用户就自动完成注册
                User user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
                User save = userService.getOne(lqw);
                session.setAttribute("user",save.getId());
            }else {
                session.setAttribute("user",one.getId());
            }
            return R.success(one);
        }
        return R.error("短信发送失败");
    }
}
