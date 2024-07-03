package com.cky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cky.mapper.EmployeeMapper;
import com.cky.pojo.Employee;
import com.cky.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName EmployeeServiceImpl
 * @Description TODO
 * @Author lukcy
 * @Date 2024/5/29 21:04
 * @Version 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 检查数据库中是否存在指定的用户名
     *
     * @param username 要检查的用户名
     * @return 如果存在返回true，否则返回false
     */
    public boolean isUsernameExist(String username) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        int count = employeeMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
