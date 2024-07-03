package com.cky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cky.mapper.OrderDetailMapper;
import com.cky.pojo.OrderDetail;
import com.cky.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}