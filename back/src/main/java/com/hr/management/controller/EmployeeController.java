package com.hr.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.management.common.Result;
import com.hr.management.entity.Employee;
import com.hr.management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

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

    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String employeeCategory,
            @RequestParam(required = false) String status) {
        Page<Employee> page = employeeService.page(current, size, keyword, department, employeeCategory, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @GetMapping("/search")
    public Result search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position) {
        List<Employee> list = employeeService.search(name, department, position);
        return Result.success(list);
    }

    @GetMapping("/statistics")
    public Result statistics() {
        Map<String, Object> stats = employeeService.getStatistics();
        return Result.success(stats);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {
        boolean success = employeeService.add(employee);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        boolean success = employeeService.update(employee);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = employeeService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch-delete")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        boolean success = employeeService.batchDelete(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }
}
