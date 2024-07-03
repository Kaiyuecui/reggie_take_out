package com.cky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cky.common.MyException;
import com.cky.dto.DishDto;
import com.cky.mapper.DishMapper;
import com.cky.pojo.Dish;
import com.cky.pojo.DishFlavor;
import com.cky.pojo.SetmealDish;
import com.cky.service.DishFlavorService;
import com.cky.service.DishService;
import com.cky.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DishServiceImpl
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 20:03
 * @Version 1.0
 */
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public void addwithFlavor(DishDto dishDto) {
        this.save(dishDto);//保存菜品类
        //保存口味类，在保存口味类之前要先把菜品id保存了
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id修改菜品基本信息和口味信息
     * @param dishDto
     */
    @Override
    public void updatewithFlavor(DishDto dishDto) {
    //根据id修改菜品基本信息
        this.updateById(dishDto);
        //清除原来的口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //把口味信息新增
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> list = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(list);

    }

    /**
     * 删除菜品和口味
     * @param ids
     */
    @Override
    public void deletewithfalvor(List<Long> ids) {
        //删除菜品时首先要判断 是否有套餐包含该菜品 以及该菜品是否在售
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        int count = setmealDishService.count(lambdaQueryWrapper);
        //如果有套餐包含 则抛出异常
        if(count>0){
            throw new MyException("有套餐包含该菜品 不能删除");
        }
        //如果在售 则抛出异常
        LambdaQueryWrapper<Dish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(Dish::getId,ids);
        lambdaQueryWrapper1.eq(Dish::getStatus,1);
        int count1 = this.count(lambdaQueryWrapper1);
        if(count1>0){
            throw  new MyException("有菜品正在售 不能删除");
        }

        //其他正常删除 菜品 以及菜品对应的口味表
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper2=new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper2);

    }
}
