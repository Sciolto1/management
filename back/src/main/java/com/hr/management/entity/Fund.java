package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公积金账户实体
 */
@TableName("fund")
public class Fund {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String accountNo;           // 公积金账号
    private String name;                // 姓名
    private String gender;              // 性别
    private String department;          // 部门
    private BigDecimal salary;          // 工资额
    private BigDecimal personalPay;     // 个人缴额
    private BigDecimal companyPay;      // 单位缴额
    private LocalDate birthDate;       // 出生日期
    private BigDecimal balance;        // 原始余额
    private String status;              // 账户状态 (active-正常, sealed-封存, closed-注销)
    private LocalDate openDate;        // 开户日期
    private Integer employeeId;        // 关联员工ID
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public BigDecimal getPersonalPay() { return personalPay; }
    public void setPersonalPay(BigDecimal personalPay) { this.personalPay = personalPay; }
    public BigDecimal getCompanyPay() { return companyPay; }
    public void setCompanyPay(BigDecimal companyPay) { this.companyPay = companyPay; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getOpenDate() { return openDate; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
