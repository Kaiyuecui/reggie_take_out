package com.cky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cky.common.R;
import com.cky.dto.SetmealDto;
import com.cky.pojo.Category;
import com.cky.pojo.Setmeal;
import com.cky.service.CategoryService;
import com.cky.service.SetmealDishService;
import com.cky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SetmealController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/28 10:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐和其对应的菜品信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        //保存套餐和其对应的菜品信息
        setmealService.savewithdishes(setmealDto);
        return R.success("套餐保存成功");
    }

    /**
     *回显套餐内容
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> showandupdate(@PathVariable Long id){
        //回显套餐内容进行修改
        SetmealDto updatewithdishes = setmealService.get(id);
        return R.success(updatewithdishes);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updatewithdiehes(setmealDto);
        return R.success("修改套餐信息成功");

    }
    /**
     * 对套餐信息进行分页展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(Integer page,Integer pageSize,String name){
        log.info("{},{},{}",page,pageSize,name);
        //首先先获取套餐的内容
        Page<Setmeal> setmealPage=new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,lambdaQueryWrapper);
        //由于我们页面展示的还有套餐所属套餐分类的名称，所以需要dto
        Page<SetmealDto> setmealDtoPage=new Page<>();
        //属性赋值 除了records这一列
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //获取原始套餐的信息
        List<Setmeal> records = setmealPage.getRecords();
        //遍历原始套餐的信息并为其赋上套餐分类的名称
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(collect); //这一步一定要写 不然没有内容
        return R.success(setmealDtoPage);
    }

    /**
     * 套餐删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("{}",ids);
        setmealService.deletewithdish(ids);
        return R.success("删除 成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @PostMapping("/status/0")
    public R<String> stop(@RequestParam List<Long> ids){
        //将当前套餐停售
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId,ids);//找出所有id
        lambdaUpdateWrapper.set(Setmeal::getStatus,0);
        boolean update = setmealService.update(lambdaUpdateWrapper);
        if (update)
         return R.success("套餐停售成功");
        return R.success("套餐停售失败");

    }
    @PostMapping("/status/1")
    public R<String> start(@RequestParam List<Long> ids){
        //将当前套餐停售
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId,ids);//找出所有id
        lambdaUpdateWrapper.set(Setmeal::getStatus,1);
        boolean update = setmealService.update(lambdaUpdateWrapper);
        if (update)
            return R.success("套餐起售成功");
        return R.success("套餐起售失败");

    }
}
