package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("transfer_record")
public class TransferRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String transferNo;
    private Integer employeeId;
    private String employeeName;
    private String employeeNo;
    private String transferType;
    private String sourceDepartment;
    private String targetDepartment;
    private String sourcePosition;
    private String targetPosition;
    private String sourceTeamGroup;
    private String targetTeamGroup;
    private String sourceEmployeeCategory;
    private String targetEmployeeCategory;
    private BigDecimal sourceBaseSalary;
    private BigDecimal targetBaseSalary;
    private LocalDate transferDate;
    private String reason;
    private String approver;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTransferNo() { return transferNo; }
    public void setTransferNo(String transferNo) { this.transferNo = transferNo; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }

    public String getSourceDepartment() { return sourceDepartment; }
    public void setSourceDepartment(String sourceDepartment) { this.sourceDepartment = sourceDepartment; }

    public String getTargetDepartment() { return targetDepartment; }
    public void setTargetDepartment(String targetDepartment) { this.targetDepartment = targetDepartment; }

    public String getSourcePosition() { return sourcePosition; }
    public void setSourcePosition(String sourcePosition) { this.sourcePosition = sourcePosition; }

    public String getTargetPosition() { return targetPosition; }
    public void setTargetPosition(String targetPosition) { this.targetPosition = targetPosition; }

    public String getSourceTeamGroup() { return sourceTeamGroup; }
    public void setSourceTeamGroup(String sourceTeamGroup) { this.sourceTeamGroup = sourceTeamGroup; }

    public String getTargetTeamGroup() { return targetTeamGroup; }
    public void setTargetTeamGroup(String targetTeamGroup) { this.targetTeamGroup = targetTeamGroup; }

    public String getSourceEmployeeCategory() { return sourceEmployeeCategory; }
    public void setSourceEmployeeCategory(String sourceEmployeeCategory) { this.sourceEmployeeCategory = sourceEmployeeCategory; }

    public String getTargetEmployeeCategory() { return targetEmployeeCategory; }
    public void setTargetEmployeeCategory(String targetEmployeeCategory) { this.targetEmployeeCategory = targetEmployeeCategory; }

    public BigDecimal getSourceBaseSalary() { return sourceBaseSalary; }
    public void setSourceBaseSalary(BigDecimal sourceBaseSalary) { this.sourceBaseSalary = sourceBaseSalary; }

    public BigDecimal getTargetBaseSalary() { return targetBaseSalary; }
    public void setTargetBaseSalary(BigDecimal targetBaseSalary) { this.targetBaseSalary = targetBaseSalary; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
