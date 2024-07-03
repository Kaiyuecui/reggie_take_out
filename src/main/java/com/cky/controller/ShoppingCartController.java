package com.cky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cky.common.MythreadLocal;
import com.cky.common.R;
import com.cky.pojo.Dish;
import com.cky.pojo.ShoppingCart;
import com.cky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ShoppingCartController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/30 11:41
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车  前端就没有给可以多个口味的选择 只要选择了一个口味 就只能点击加号 选择同样的口味了
     * 如果可以添加不同的口味 那么number就要变了 因为不能仅仅通过菜品id来number加1  如果口味不同 那就属于不同的
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置userid
        shoppingCart.setUserId(MythreadLocal.getCurrendId());
        //查看相同菜品或者套餐是否在数据库中 如果有直接分数加1
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,MythreadLocal.getCurrendId());
        lambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId, shoppingCart.getDishId());
        lambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart serviceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (serviceOne!=null){
            Integer number = serviceOne.getNumber();
            serviceOne.setNumber(number+1);
            serviceOne.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(serviceOne);
        }
        else {
            //没有查到
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            serviceOne=shoppingCart;
        }
        return R.success(serviceOne);
    }

    /**
     * 减少套餐或者菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public  R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,MythreadLocal.getCurrendId());
        lambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        lambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());

        ShoppingCart serviceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = serviceOne.getNumber();
        if(number>=2){
            serviceOne.setNumber(number-1);
            shoppingCartService.updateById(serviceOne);
        }
        else
            shoppingCartService.removeById(serviceOne);
        return R.success(serviceOne);

    }
    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        Long currendId = MythreadLocal.getCurrendId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currendId);
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        Long currendId = MythreadLocal.getCurrendId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currendId);//
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }

}
