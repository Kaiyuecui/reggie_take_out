package com.cky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cky.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName EmployeeMapper
 * @Description TODO
 * @Author lukcy
 * @Date 2024/5/29 21:02
 * @Version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
