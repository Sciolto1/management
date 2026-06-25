package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.TrainingParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 培训报名Mapper
 */
@Mapper
public interface TrainingParticipantMapper extends BaseMapper<TrainingParticipant> {
    
    @Select("SELECT employee_id FROM training_participant WHERE training_id = #{trainingId}")
    List<Integer> getParticipantsByTrainingId(Integer trainingId);
}
