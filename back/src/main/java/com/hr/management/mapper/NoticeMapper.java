package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
