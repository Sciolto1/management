package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Performance;
import com.hr.management.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 绩效控制器
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    /**
     * 获取所有绩效记录
     */
    @GetMapping("/list")
    public Result list() {
        List<Performance> list = performanceService.list();
        return Result.success(list);
    }

    /**
     * 获取员工绩效记录
     */
    @GetMapping("/employee/{employeeId}")
    public Result getByEmployeeId(@PathVariable Integer employeeId) {
        List<Performance> list = performanceService.getByEmployeeId(employeeId);
        return Result.success(list);
    }

    /**
     * 添加绩效记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody Performance performance) {
        boolean success = performanceService.add(performance);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新绩效记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody Performance performance) {
        boolean success = performanceService.update(performance);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除绩效记录
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = performanceService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
