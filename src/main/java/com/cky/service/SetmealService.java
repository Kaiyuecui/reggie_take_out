package com.cky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cky.dto.SetmealDto;
import com.cky.pojo.Setmeal;

import java.util.List;

/**
 * @ClassName SetmealService
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 20:02
 * @Version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
// //保存套餐和其对应的菜品信息
    void savewithdishes(SetmealDto setmealDto);
// 删除套餐和其对应的套餐菜品表
    void deletewithdish(List<Long> ids);

    SetmealDto get(Long id);
    void updatewithdiehes(SetmealDto setmealDto);
}
