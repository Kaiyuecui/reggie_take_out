package com.cky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cky.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}