package com.hr.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.management.entity.FundTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 公积金帐务Mapper接口
 */
@Mapper
public interface FundTransactionMapper extends BaseMapper<FundTransaction> {

    /**
     * 根据公积金ID查询帐务记录
     */
    List<FundTransaction> selectByFundId(@Param("fundId") Integer fundId);

    /**
     * 根据时间范围查询
     */
    List<FundTransaction> selectByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 根据类型查询
     */
    List<FundTransaction> selectByType(@Param("type") String type);

    /**
     * 获取指定公积金账户的最新余额
     */
    BigDecimal getLatestBalance(@Param("fundId") Integer fundId);
}
