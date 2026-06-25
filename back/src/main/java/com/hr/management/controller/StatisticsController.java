package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private LeaveRequestService leaveRequestService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private PerformanceService performanceService;

    /**
     * 获取统计数据
     */
    @GetMapping("/overview")
    public Result overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("employeeCount", employeeService.list().size());
        data.put("pendingLeaveCount", leaveRequestService.list().stream()
                .filter(l -> "pending".equals(l.getStatus())).count());
        data.put("attendanceCount", attendanceService.listAll().size());
        data.put("performanceCount", performanceService.list().size());
        return Result.success(data);
    }
}
