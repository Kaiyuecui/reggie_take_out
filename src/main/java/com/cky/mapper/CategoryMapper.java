package com.cky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cky.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName CategoryMapper
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 10:06
 * @Version 1.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
