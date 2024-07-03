package com.cky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cky.dto.DishDto;
import com.cky.pojo.Dish;

import java.util.List;

/**
 * @ClassName DishService
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 20:01
 * @Version 1.0
 */
public interface DishService extends IService<Dish> {
    //增加菜品基本信息和口味信息
    void addwithFlavor(DishDto dishDto);
    //根据id修改菜品基本信息和口味信息
    void updatewithFlavor(DishDto dishDto);
    //删除菜品和口味
    void deletewithfalvor(List<Long> ids);
}
