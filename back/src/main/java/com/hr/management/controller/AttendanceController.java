package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Attendance;
import com.hr.management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 考勤控制器
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 获取所有考勤记录（管理员）
     */
    @GetMapping("/list")
    public Result list() {
        List<Attendance> list = attendanceService.listAll();
        return Result.success(list);
    }

    /**
     * 获取员工考勤记录
     */
    @GetMapping("/employee/{employeeId}")
    public Result getByEmployeeId(@PathVariable Integer employeeId) {
        List<Attendance> list = attendanceService.getByEmployeeId(employeeId);
        return Result.success(list);
    }

    /**
     * 获取今日考勤
     */
    @GetMapping("/today/{employeeId}")
    public Result getTodayRecord(@PathVariable Integer employeeId) {
        Attendance attendance = attendanceService.getTodayRecord(employeeId);
        return Result.success(attendance);
    }

    /**
     * 上班打卡
     */
    @PostMapping("/clockIn/{employeeId}")
    public Result clockIn(@PathVariable Integer employeeId) {
        boolean success = attendanceService.clockIn(employeeId);
        return success ? Result.success() : Result.error("今日已打卡");
    }

    /**
     * 下班打卡
     */
    @PostMapping("/clockOut/{employeeId}")
    public Result clockOut(@PathVariable Integer employeeId) {
        boolean success = attendanceService.clockOut(employeeId);
        return success ? Result.success() : Result.error("未打卡或已下班");
    }

    /**
     * 更新考勤状态
     */
    @PutMapping("/updateStatus/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        boolean success = attendanceService.updateStatus(id, status);
        return success ? Result.success() : Result.error("更新失败");
    }
}
