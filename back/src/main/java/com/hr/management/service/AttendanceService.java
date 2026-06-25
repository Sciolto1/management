package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Attendance;
import com.hr.management.entity.Employee;
import com.hr.management.mapper.AttendanceMapper;
import com.hr.management.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考勤服务
 */
@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 获取所有考勤记录（含员工姓名）
     */
    public List<Attendance> listAll() {
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("date");
        List<Attendance> list = attendanceMapper.selectList(wrapper);
        // 填充员工姓名
        List<Employee> employees = employeeMapper.selectList(null);
        Map<Integer, String> empNameMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getName));
        list.forEach(a -> a.setEmployeeName(empNameMap.getOrDefault(a.getEmployeeId(), "未知")));
        return list;
    }

    /**
     * 获取员工考勤记录
     */
    public List<Attendance> getByEmployeeId(Integer employeeId) {
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).orderByDesc("date");
        return attendanceMapper.selectList(wrapper);
    }

    /**
     * 获取今日考勤记录
     */
    public Attendance getTodayRecord(Integer employeeId) {
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).eq("date", LocalDate.now());
        return attendanceMapper.selectOne(wrapper);
    }

    /**
     * 上班打卡
     */
    public boolean clockIn(Integer employeeId) {
        Attendance existing = getTodayRecord(employeeId);
        if (existing != null) {
            return false;
        }
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(LocalDate.now());
        LocalTime now = LocalTime.now();
        attendance.setClockIn(now);
        // 异常：5:00前或12:00后上班打卡
        if (now.isBefore(LocalTime.of(5, 0)) || now.isAfter(LocalTime.of(12, 0))) {
            attendance.setStatus("abnormal");
        } else if (now.isAfter(LocalTime.of(9, 0))) {
            attendance.setStatus("late");
        } else {
            attendance.setStatus("normal");
        }
        return attendanceMapper.insert(attendance) > 0;
    }

    /**
     * 下班打卡
     */
    public boolean clockOut(Integer employeeId) {
        Attendance existing = getTodayRecord(employeeId);
        if (existing == null || existing.getClockOut() != null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        existing.setClockOut(now);
        // 异常：12:00前或23:00后下班打卡，或上下班间隔不足30分钟
        if (now.isBefore(LocalTime.of(12, 0)) || now.isAfter(LocalTime.of(23, 0))) {
            existing.setStatus("abnormal");
        } else if (existing.getClockIn() != null && java.time.Duration.between(existing.getClockIn(), now).toMinutes() < 30) {
            existing.setStatus("abnormal");
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            existing.setStatus("early_leave");
        }
        return attendanceMapper.updateById(existing) > 0;
    }

    /**
     * 更新考勤状态
     */
    public boolean updateStatus(Integer id, String status) {
        Attendance attendance = attendanceMapper.selectById(id);
        if (attendance == null) return false;
        attendance.setStatus(status);
        return attendanceMapper.updateById(attendance) > 0;
    }
}
