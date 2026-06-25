package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假申请Mapper
 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {
}
