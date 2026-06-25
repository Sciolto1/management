package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.LeaveRequest;
import com.hr.management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 请假控制器
 */
@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    /**
     * 获取所有请假申请
     */
    @GetMapping("/list")
    public Result list() {
        List<LeaveRequest> list = leaveRequestService.list();
        return Result.success(list);
    }

    /**
     * 获取员工请假记录
     */
    @GetMapping("/employee/{employeeId}")
    public Result getByEmployeeId(@PathVariable Integer employeeId) {
        List<LeaveRequest> list = leaveRequestService.getByEmployeeId(employeeId);
        return Result.success(list);
    }

    /**
     * 提交请假申请
     */
    @PostMapping("/add")
    public Result add(@RequestBody LeaveRequest leaveRequest) {
        boolean success = leaveRequestService.add(leaveRequest);
        return success ? Result.success() : Result.error("提交失败");
    }

    /**
     * 审批请假
     */
    @PutMapping("/approve/{id}")
    public Result approve(@PathVariable Integer id, @RequestParam String status) {
        boolean success = leaveRequestService.approve(id, status);
        return success ? Result.success() : Result.error("审批失败");
    }

    /**
     * 删除请假申请
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = leaveRequestService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
