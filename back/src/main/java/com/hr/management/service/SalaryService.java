package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Salary;
import com.hr.management.mapper.SalaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 工资服务
 */
@Service
public class SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;

    /**
     * 获取所有工资记录
     */
    public List<Salary> list() {
        QueryWrapper<Salary> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("month");
        return salaryMapper.selectList(wrapper);
    }

    /**
     * 获取员工工资记录
     */
    public List<Salary> getByEmployeeId(Integer employeeId) {
        QueryWrapper<Salary> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).orderByDesc("month");
        return salaryMapper.selectList(wrapper);
    }

    /**
     * 添加工资记录
     */
    public boolean add(Salary salary) {
        salary.setActualSalary(salary.getBaseSalary().add(salary.getBonus()).subtract(salary.getDeduction()));
        return salaryMapper.insert(salary) > 0;
    }

    /**
     * 更新工资记录
     */
    public boolean update(Salary salary) {
        salary.setActualSalary(salary.getBaseSalary().add(salary.getBonus()).subtract(salary.getDeduction()));
        return salaryMapper.updateById(salary) > 0;
    }

    /**
     * 更新工资状态
     */
    public boolean updateStatus(Integer id, String status) {
        Salary salary = salaryMapper.selectById(id);
        if (salary == null) return false;
        salary.setStatus(status);
        return salaryMapper.updateById(salary) > 0;
    }

    /**
     * 删除工资记录
     */
    public boolean delete(Integer id) {
        return salaryMapper.deleteById(id) > 0;
    }
}
