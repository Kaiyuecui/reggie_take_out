package com.cky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cky.common.R;
import com.cky.dto.DishDto;
import com.cky.dto.OrdersDto;
import com.cky.pojo.Dish;
import com.cky.pojo.Orders;
import com.cky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/30 16:25
 * @Version 1.0
 */
/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 分页获取订单信息  用户名没解决
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Orders> page1=new Page<>(page,pageSize);
        orderService.page(page1);
        Page<OrdersDto> ordersDtoPage=new Page<>();
        BeanUtils.copyProperties(page1,ordersDtoPage,"records");
        List<Orders> records = page1.getRecords();
        List<OrdersDto> collect = records.stream().map((item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //订单id、
            Long id = ordersDto.getId();
            LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Orders::getId, id);
            Orders orders = orderService.getOne(lambdaQueryWrapper);
            String userName = orders.getUserName();
            ordersDto.setUserName(userName);
            return ordersDto;
        })).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return R.success(page1);

    }

}
