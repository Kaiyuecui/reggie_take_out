package com.cky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cky.mapper.UserMapper;
import com.cky.pojo.User;
import com.cky.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/29 10:40
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
