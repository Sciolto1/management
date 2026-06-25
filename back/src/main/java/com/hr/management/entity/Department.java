package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("department")
public class Department {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String deptCode;
    private String name;
    private String manager;
    private String description;
    private String remark;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer employeeCount;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
}
