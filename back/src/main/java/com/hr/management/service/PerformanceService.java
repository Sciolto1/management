package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Performance;
import com.hr.management.mapper.PerformanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 绩效服务
 */
@Service
public class PerformanceService {

    @Autowired
    private PerformanceMapper performanceMapper;

    /**
     * 获取所有绩效记录
     */
    public List<Performance> list() {
        return performanceMapper.selectList(new QueryWrapper<Performance>().orderByDesc("period"));
    }

    /**
     * 获取员工绩效记录
     */
    public List<Performance> getByEmployeeId(Integer employeeId) {
        QueryWrapper<Performance> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).orderByDesc("period");
        return performanceMapper.selectList(wrapper);
    }

    /**
     * 添加绩效记录
     */
    public boolean add(Performance performance) {
        calculateScore(performance);
        return performanceMapper.insert(performance) > 0;
    }

    /**
     * 更新绩效记录
     */
    public boolean update(Performance performance) {
        calculateScore(performance);
        return performanceMapper.updateById(performance) > 0;
    }

    /**
     * 删除绩效记录
     */
    public boolean delete(Integer id) {
        return performanceMapper.deleteById(id) > 0;
    }

    /**
     * 计算综合得分和等级
     */
    private void calculateScore(Performance p) {
        int total = (p.getWorkScore() + p.getAttitudeScore() + p.getTeamworkScore() + p.getInnovationScore()) / 4;
        p.setTotalScore(total);
        if (total >= 90) p.setLevel("S");
        else if (total >= 80) p.setLevel("A");
        else if (total >= 70) p.setLevel("B");
        else p.setLevel("C");
    }
}
