package com.cky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cky.pojo.Category;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryService
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 10:06
 * @Version 1.0
 */
@Service
public interface CategoryService extends IService<Category> {
    void deleteByid(Long id);
}
