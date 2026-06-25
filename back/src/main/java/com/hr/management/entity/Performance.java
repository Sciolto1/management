package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 绩效考核实体
 */
@TableName("performance")
public class Performance {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String period;
    private Integer workScore;
    private Integer attitudeScore;
    private Integer teamworkScore;
    private Integer innovationScore;
    private Integer totalScore;
    private String level;
    private String comment;
    private String evaluator;
    private LocalDateTime createTime;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Integer getWorkScore() { return workScore; }
    public void setWorkScore(Integer workScore) { this.workScore = workScore; }
    public Integer getAttitudeScore() { return attitudeScore; }
    public void setAttitudeScore(Integer attitudeScore) { this.attitudeScore = attitudeScore; }
    public Integer getTeamworkScore() { return teamworkScore; }
    public void setTeamworkScore(Integer teamworkScore) { this.teamworkScore = teamworkScore; }
    public Integer getInnovationScore() { return innovationScore; }
    public void setInnovationScore(Integer innovationScore) { this.innovationScore = innovationScore; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getEvaluator() { return evaluator; }
    public void setEvaluator(String evaluator) { this.evaluator = evaluator; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
