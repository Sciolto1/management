package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
