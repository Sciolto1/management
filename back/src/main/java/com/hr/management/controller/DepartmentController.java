package com.hr.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.management.common.Result;
import com.hr.management.entity.Department;
import com.hr.management.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public Result list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Page<Department> page = departmentService.page(current, size, keyword, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        boolean success = departmentService.add(department);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result update(@RequestBody Department department) {
        boolean success = departmentService.update(department);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = departmentService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch-delete")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        boolean success = departmentService.batchDelete(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }
}
