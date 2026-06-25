package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤记录Mapper
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}
