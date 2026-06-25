package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Employee;
import com.hr.management.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String role = params.get("role");
        
        Employee user = sysUserService.login(username, password, role);
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("employeeId", user.getId());
            // 部门管理员返回所属部门
            if ("dept_admin".equals(user.getRole())) {
                data.put("department", user.getDepartment());
            }
            return Result.success(data);
        }
        return Result.error("用户名或密码错误");
    }
}
