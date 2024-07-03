package com.cky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cky.mapper.ShoppingCartMapper;
import com.cky.pojo.ShoppingCart;
import com.cky.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
