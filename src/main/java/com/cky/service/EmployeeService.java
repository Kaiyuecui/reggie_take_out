package com.cky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cky.pojo.Employee;

/**
 * @ClassName EmployeeService
 * @Description TODO
 * @Author lukcy
 * @Date 2024/5/29 21:03
 * @Version 1.0
 */
public interface EmployeeService extends IService<Employee> {
  boolean isUsernameExist(String username);
}
