package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Department;
import com.hr.management.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有部门
     */
    @GetMapping("/list")
    public Result list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    /**
     * 添加部门
     */
    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        boolean success = departmentService.add(department);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新部门
     */
    @PutMapping("/update")
    public Result update(@RequestBody Department department) {
        boolean success = departmentService.update(department);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = departmentService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
