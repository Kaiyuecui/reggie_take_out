package com.cky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cky.common.MyException;
import com.cky.dto.SetmealDto;
import com.cky.mapper.SetmealMapper;
import com.cky.pojo.DishFlavor;
import com.cky.pojo.Setmeal;
import com.cky.pojo.SetmealDish;
import com.cky.service.SetmealDishService;
import com.cky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SetmealServiceImpl
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 20:02
 * @Version 1.0
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    //保存套餐和其对应的菜品信息
    @Override
    public void savewithdishes(SetmealDto setmealDto) {
        this.save(setmealDto);//保存套餐信息
        //获得套餐对应的菜品信息 并为其赋值(套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);//批量保存

    }

    /**
     * // 删除套餐和其对应的套餐菜品表
     * @param ids
     */
    @Transactional
    @Override
    public void deletewithdish(List<Long> ids) {
        //查询要删除的套餐是否有在售的
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(lambdaQueryWrapper);


        //如果有抛出一个异常
        if(count>0){
            throw new MyException("选择的套餐有在售的 不能删除");
        }
        //如果没有 则先删除套餐
        this.removeByIds(ids);

        //接着删除setmeal——dish表

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper1);
    }

    /**
     * 回显套餐内容
     * @param id
     */
    @Override
    public SetmealDto get(Long id) {
        SetmealDto setmealDto=new SetmealDto();
        Setmeal setmeal1 = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal1,setmealDto);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);//获得套餐对应的菜品
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);

        setmealDto.setSetmealDishes(list);//将菜品信息也放入setmealdto
        return setmealDto;


    }

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @Override
    public void updatewithdiehes(SetmealDto setmealDto) {
        //修改套餐的基本信息
        this.updateById(setmealDto);
        //清除原来套餐的菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //更新新的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }
}
