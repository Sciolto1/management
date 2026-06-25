package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Employee;
import com.hr.management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 获取所有员工（支持按部门过滤）
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String department) {
        List<Employee> list;
        if (department != null && !department.isEmpty()) {
            list = employeeService.listByDepartment(department);
        } else {
            list = employeeService.list();
        }
        return Result.success(list);
    }

    /**
     * 根据ID获取员工
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 添加员工
     */
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {
        boolean success = employeeService.add(employee);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新员工
     */
    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        boolean success = employeeService.update(employee);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = employeeService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
