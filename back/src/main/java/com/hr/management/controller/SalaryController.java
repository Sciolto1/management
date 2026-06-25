package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Salary;
import com.hr.management.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 工资控制器
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    /**
     * 获取所有工资记录
     */
    @GetMapping("/list")
    public Result list() {
        List<Salary> list = salaryService.list();
        return Result.success(list);
    }

    /**
     * 获取员工工资记录
     */
    @GetMapping("/employee/{employeeId}")
    public Result getByEmployeeId(@PathVariable Integer employeeId) {
        List<Salary> list = salaryService.getByEmployeeId(employeeId);
        return Result.success(list);
    }

    /**
     * 添加工资记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody Salary salary) {
        boolean success = salaryService.add(salary);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新工资记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody Salary salary) {
        boolean success = salaryService.update(salary);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 更新工资状态（发放）
     */
    @PutMapping("/updateStatus/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        boolean success = salaryService.updateStatus(id, status);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 删除工资记录
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = salaryService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
