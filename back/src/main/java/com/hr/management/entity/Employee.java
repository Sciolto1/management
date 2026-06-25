package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("employee")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String employeeNo;
    private String name;
    private String photo;
    private Integer age;
    private String gender;
    private LocalDate birthDate;
    private String ethnicity;
    private String politicalStatus;
    private String maritalStatus;
    private String idCard;

    private String department;
    private String position;
    private String teamGroup;
    private String employeeCategory;
    private String title;
    private String major;
    private String education;
    private String graduationSchool;
    private LocalDate graduationDate;
    private BigDecimal baseSalary;

    private LocalDate entryDate;
    private LocalDate workStartDate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String homeAddress;
    private String phone;
    private String email;
    private String remark;

    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String password;
    private String role;

    @TableField(exist = false)
    private String departmentName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }

    public String getPoliticalStatus() { return politicalStatus; }
    public void setPoliticalStatus(String politicalStatus) { this.politicalStatus = politicalStatus; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getTeamGroup() { return teamGroup; }
    public void setTeamGroup(String teamGroup) { this.teamGroup = teamGroup; }

    public String getEmployeeCategory() { return employeeCategory; }
    public void setEmployeeCategory(String employeeCategory) { this.employeeCategory = employeeCategory; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getGraduationSchool() { return graduationSchool; }
    public void setGraduationSchool(String graduationSchool) { this.graduationSchool = graduationSchool; }

    public LocalDate getGraduationDate() { return graduationDate; }
    public void setGraduationDate(LocalDate graduationDate) { this.graduationDate = graduationDate; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public LocalDate getWorkStartDate() { return workStartDate; }
    public void setWorkStartDate(LocalDate workStartDate) { this.workStartDate = workStartDate; }

    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }

    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
