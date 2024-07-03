package com.cky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cky.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName DishMapper
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 19:47
 * @Version 1.0
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
