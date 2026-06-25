package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 培训报名实体
 */
@TableName("training_participant")
public class TrainingParticipant {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer trainingId;
    private Integer employeeId;
    private LocalDateTime createTime;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTrainingId() { return trainingId; }
    public void setTrainingId(Integer trainingId) { this.trainingId = trainingId; }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
