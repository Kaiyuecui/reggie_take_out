package com.cky.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cky.common.R;
import com.cky.pojo.User;
import com.cky.service.UserService;
import com.cky.utils.SMSUtils;
import com.cky.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/29 9:42
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/sendMsg")
    public R<String> snedMsg(@RequestBody User user,HttpSession httpSession){
        log.info(user.toString());
        //获取手机号
        String phone = user.getPhone().toString();
        if (StringUtils.isNotEmpty(phone)) {
            //手机号不为空发送验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code.toString());
//            SMSUtils.sendMessage("","",phone,"");//发送手机验证码
            //httpSession.setAttribute(phone,code); //将生成的验证码保存到session中


            //将验证码保存到redis中 并设置时间为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("验证码发送成功");

        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获得手机号
        String phone = map.get("phone").toString();
        //获得验证码
        String code = map.get("code").toString();
       // Object sessionCode = session.getAttribute(phone);
        //从redis中获取验证码
        Object sessionCode = redisTemplate.opsForValue().get(phone);
        //比对验证码是否一致
        if (sessionCode!=null && sessionCode.equals(code)) {
            //看数据库中是否有该用户
            LambdaUpdateWrapper<User> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaUpdateWrapper);
            if (user ==null) {
                //没有，保存一个
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);

            }
            session.setAttribute("user",user.getId());
            //用户登陆成功 则将验证码删除
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("验证失败");
    }
}
