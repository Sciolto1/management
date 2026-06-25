package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Training;
import com.hr.management.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 培训控制器
 */
@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    /**
     * 获取所有培训
     */
    @GetMapping("/list")
    public Result list() {
        List<Training> list = trainingService.list();
        return Result.success(list);
    }

    /**
     * 添加培训
     */
    @PostMapping("/add")
    public Result add(@RequestBody Training training) {
        boolean success = trainingService.add(training);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新培训
     */
    @PutMapping("/update")
    public Result update(@RequestBody Training training) {
        boolean success = trainingService.update(training);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除培训
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = trainingService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 报名培训
     */
    @PostMapping("/enroll")
    public Result enroll(@RequestBody Map<String, Integer> params) {
        Integer trainingId = params.get("trainingId");
        Integer employeeId = params.get("employeeId");
        boolean success = trainingService.enroll(trainingId, employeeId);
        return success ? Result.success() : Result.error("报名失败，可能已报名或人数已满");
    }

    /**
     * 取消报名
     */
    @PostMapping("/cancelEnroll")
    public Result cancelEnroll(@RequestBody Map<String, Integer> params) {
        Integer trainingId = params.get("trainingId");
        Integer employeeId = params.get("employeeId");
        boolean success = trainingService.cancelEnroll(trainingId, employeeId);
        return success ? Result.success() : Result.error("取消失败");
    }
}
