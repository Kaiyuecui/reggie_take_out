package com.cky.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cky.common.R;
import com.cky.pojo.Employee;
import com.cky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @ClassName EmployeeController
 * @Description TODO
 * @Author lukcy
 * @Date 2024/5/29 21:05
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     *
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping(value = "/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        //MD5密码加密
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //根据用户名查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);
        //看是否查到 成功或者失败 失败返回结果集R
        if(emp == null)
            return R.error("登陆失败");
        //比对密码 比对不成功 返回结果集R
        if(!emp.getPassword().equals(password))
            return R.error("登陆失败");
        //查看是否禁用
        if(emp.getStatus()==0)
            return R.error("用户已经禁用");
        //登陆成功 id存入session并返回登陆成功结果
        httpServletRequest.getSession().setAttribute("employee", emp.getId());
//        httpServletRequest.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 用户退出登录
     * @param httpServletRequest
     * @return
     */
    @PostMapping(value = "/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        //移除sessionid
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出登录");
    }

    /**
     * 新增员工
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> saveemp(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
//        boolean usernameExist = employeeService.isUsernameExist(employee.getUsername());
//        if (usernameExist){
//            return R.error(String.format("%s 用户名已经存在", employee.getUsername()));
//        }
        //设置默认初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //其他字段
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long) httpServletRequest.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        //直接调用接口的save方法
        employeeService.save(employee);
        return R.success("员工添加成功");
    }

    /**
     * 分页功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page:{},pagesize:{},name:{}",page,pageSize,name);
        //构造分页插件
        Page pageInfo=new Page(page,pageSize);
        //构造查询语句
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工状态
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest,@RequestBody  Employee employee){
//        Long id = (Long) httpServletRequest.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(id);
//        long id1 = Thread.currentThread().getId();
//        log.info("当前线程id{}",id1);
        employeeService.updateById(employee);
        return R.success("更改状态成功");
    }

    /**
     * 编辑员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getByID(@PathVariable Long id){

        Employee employee = employeeService.getById(id);
        if (employee!=null)
            return R.success(employee);
        return R.error("没有查询到对应员工");
    }
}
