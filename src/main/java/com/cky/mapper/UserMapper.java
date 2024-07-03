package com.cky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cky.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/29 10:39
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
