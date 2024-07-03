package com.cky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cky.common.MyException;
import com.cky.mapper.CategoryMapper;
import com.cky.mapper.EmployeeMapper;
import com.cky.pojo.Category;
import com.cky.pojo.Dish;
import com.cky.pojo.Employee;
import com.cky.pojo.Setmeal;
import com.cky.service.CategoryService;
import com.cky.service.DishService;
import com.cky.service.EmployeeService;
import com.cky.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 10:07
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 根据id删除菜品或者套餐
     * @param id
     */
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void deleteByid(Long id) {
        //查看id是否绑定菜品
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(lambdaQueryWrapper);
        if(count>0){
            throw new MyException("该分类关联了菜品，不能删除");
        }
        //查看id是否绑定套餐
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(lambdaQueryWrapper1);
        if(count1>0){
            //异常
           throw  new MyException("该分类关联了套餐，不能删除");
        }
        //都没有，正常删除
        super.removeById(id);
    }
}
