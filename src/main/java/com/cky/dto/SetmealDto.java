package com.cky.dto;

import com.cky.pojo.Setmeal;
import com.cky.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;


}
