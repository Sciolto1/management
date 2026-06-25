package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.Salary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工资记录Mapper
 */
@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {
}
