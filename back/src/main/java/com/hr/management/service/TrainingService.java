package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Training;
import com.hr.management.entity.TrainingParticipant;
import com.hr.management.mapper.TrainingMapper;
import com.hr.management.mapper.TrainingParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 培训服务
 */
@Service
public class TrainingService {

    @Autowired
    private TrainingMapper trainingMapper;

    @Autowired
    private TrainingParticipantMapper participantMapper;

    /**
     * 获取所有培训
     */
    public List<Training> list() {
        List<Training> trainings = trainingMapper.selectList(new QueryWrapper<Training>().orderByDesc("date"));
        for (Training t : trainings) {
            List<Integer> participants = participantMapper.getParticipantsByTrainingId(t.getId());
            t.setParticipants(participants);
            t.setParticipantCount(participants.size());
        }
        return trainings;
    }

    /**
     * 添加培训
     */
    public boolean add(Training training) {
        return trainingMapper.insert(training) > 0;
    }

    /**
     * 更新培训
     */
    public boolean update(Training training) {
        return trainingMapper.updateById(training) > 0;
    }

    /**
     * 删除培训
     */
    public boolean delete(Integer id) {
        // 先删除报名记录
        QueryWrapper<TrainingParticipant> wrapper = new QueryWrapper<>();
        wrapper.eq("training_id", id);
        participantMapper.delete(wrapper);
        return trainingMapper.deleteById(id) > 0;
    }

    /**
     * 报名培训
     */
    public boolean enroll(Integer trainingId, Integer employeeId) {
        // 检查是否已报名
        QueryWrapper<TrainingParticipant> wrapper = new QueryWrapper<>();
        wrapper.eq("training_id", trainingId).eq("employee_id", employeeId);
        if (participantMapper.selectOne(wrapper) != null) {
            return false;
        }
        // 检查人数上限
        Training training = trainingMapper.selectById(trainingId);
        List<Integer> participants = participantMapper.getParticipantsByTrainingId(trainingId);
        if (participants.size() >= training.getMaxParticipants()) {
            return false;
        }
        TrainingParticipant participant = new TrainingParticipant();
        participant.setTrainingId(trainingId);
        participant.setEmployeeId(employeeId);
        return participantMapper.insert(participant) > 0;
    }

    /**
     * 取消报名
     */
    public boolean cancelEnroll(Integer trainingId, Integer employeeId) {
        QueryWrapper<TrainingParticipant> wrapper = new QueryWrapper<>();
        wrapper.eq("training_id", trainingId).eq("employee_id", employeeId);
        return participantMapper.delete(wrapper) > 0;
    }
}
