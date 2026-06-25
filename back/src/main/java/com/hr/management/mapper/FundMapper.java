package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.Fund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 公积金Mapper接口
 */
@Mapper
public interface FundMapper extends BaseMapper<Fund> {

    /**
     * 根据账号查询
     */
    Fund selectByAccountNo(@Param("accountNo") String accountNo);

    /**
     * 根据员工ID查询
     */
    Fund selectByEmployeeId(@Param("employeeId") Integer employeeId);

    /**
     * 根据部门查询
     */
    List<Fund> selectByDepartment(@Param("department") String department);

    /**
     * 根据状态查询
     */
    List<Fund> selectByStatus(@Param("status") String status);

    /**
     * 查询所有有效账户
     */
    List<Fund> selectActiveAccounts();
}
