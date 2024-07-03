package com.cky.controller;

import com.alibaba.druid.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cky.common.MyException;
import com.cky.common.R;
import com.cky.dto.DishDto;
import com.cky.pojo.*;
import com.cky.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DishController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/26 11:10
 * @Version 1.0
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
  private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private  SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        dishService.addwithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页管理
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page,int pageSize,String name){
        Page<Dish> dishPage=new Page<>(page,pageSize);
        Page<DishDto> dishdtoPage=new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,lambdaQueryWrapper);

        BeanUtils.copyProperties(dishPage,dishdtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //通过id获取菜品分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String name1 = category.getName();
            dishDto.setCategoryName(name1);
            return dishDto;
        }).collect(Collectors.toList());
        dishdtoPage.setRecords(collect);
        return R.success(dishdtoPage);
    }

    /**
     * 根据id回显菜品以及口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> show(@PathVariable Long id){
        //根据菜品id查询菜品基本信息
        Dish dish = dishService.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //根据菜品id查询菜品口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }
    /**
     * 保存 修改过的菜品和菜品对应的口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updatewithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 批量删除菜品  同时要把对应菜品的口味也对应删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
//        List<Long> idList = Arrays.stream(ids.split(","))
//                .map(Long::valueOf)
//                .collect(Collectors.toList());
        dishService.deletewithfalvor(ids);
        return R.success("删除成功");
    }

    /**
     * 批量停售 需要判断 是否有套餐包含该菜品 如果该套餐在售 则不能停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stop(@RequestParam("ids") List<Long> ids) {
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId, ids); // 先把包含菜品的给选出来

        // 接着把该菜品对应的套餐选出来
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        Set<Long> setmealIds = list.stream()
                .map(SetmealDish::getSetmealId)
                .collect(Collectors.toSet()); // 去重复

        if (!setmealIds.isEmpty()) {
            LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.in(Setmeal::getId, setmealIds);
            lambdaQueryWrapper1.eq(Setmeal::getStatus, 1); // 需要确保套餐在售，套餐在售则菜品不能停售，否则可以

            int count = setmealService.count(lambdaQueryWrapper1);
            if (count > 0) {
                throw new MyException("有套餐包含该菜品，正在售");
            }
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, 0); // 修改状态为停售

        boolean update = dishService.update(updateWrapper);
        if (update) {
            return R.success("批量停售成功");
        } else {
            return R.error("批量停售失败");
        }
    }

    /**
     * 批量起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> start(@RequestParam("ids") List<Long> ids){
        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Dish::getId,ids);
        lambdaUpdateWrapper.set(Dish::getStatus,1);
        boolean update = dishService.update(lambdaUpdateWrapper);
        if (update) {
            return R.success("批量起售成功");
        } else {
            return R.error("批量起售成功");
        }
    }

    /**
     * 根据菜品的分类id获得所有菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> listR(Dish dish)
    {
        Long id = dish.getCategoryId();
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(id!=null,Dish::getCategoryId,id);
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);//获得所有的菜品 但是为了让前端能够展示规格 我们需要将口味也返回
        List<DishDto> listdto=null;
        List<DishDto> collect = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //通过id获取菜品分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String name1 = category.getName();
            dishDto.setCategoryName(name1);

            //根据菜品id获得 口味信息
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> list1 = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(collect);

    }
}
