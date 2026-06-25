package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Employee;
import com.hr.management.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

/**
 * 系统用户服务（已合并到employee表）
 */
@Service
public class SysUserService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 用户登录
     * dept_admin可以通过“管理员登录”和“员工登录”两种方式登录
     */
    public Employee login(String username, String password, String role) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
               .eq("password", password);
        if ("admin".equals(role)) {
            wrapper.in("role", Arrays.asList("admin", "dept_admin"));
        } else if ("user".equals(role)) {
            wrapper.in("role", Arrays.asList("user", "dept_admin"));
        } else {
            wrapper.eq("role", role);
        }
        return employeeMapper.selectOne(wrapper);
    }

    /**
     * 根据ID获取用户（员工）
     */
    public Employee getById(Integer id) {
        return employeeMapper.selectById(id);
    }
}
