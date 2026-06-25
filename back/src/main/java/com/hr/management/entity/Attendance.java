package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤记录实体
 */
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer employeeId;
    private LocalDate date;
    private LocalTime clockIn;
    private LocalTime clockOut;
    private String status;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String employeeName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getClockIn() { return clockIn; }
    public void setClockIn(LocalTime clockIn) { this.clockIn = clockIn; }
    public LocalTime getClockOut() { return clockOut; }
    public void setClockOut(LocalTime clockOut) { this.clockOut = clockOut; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}
