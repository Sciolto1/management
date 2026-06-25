package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hr.management.entity.Department;
import com.hr.management.entity.Employee;
import com.hr.management.mapper.DepartmentMapper;
import com.hr.management.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 获取所有部门（含员工人数）
     */
    public List<Department> list() {
        List<Department> departments = departmentMapper.selectList(null);
        // 统计每个部门的员工数
        List<Employee> employees = employeeMapper.selectList(null);
        Map<String, Long> countMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        departments.forEach(d -> d.setEmployeeCount(countMap.getOrDefault(d.getName(), 0L).intValue()));
        return departments;
    }

    /**
     * 根据ID获取部门
     */
    public Department getById(Integer id) {
        return departmentMapper.selectById(id);
    }

    /**
     * 添加部门
     */
    public boolean add(Department department) {
        boolean result = departmentMapper.insert(department) > 0;
        if (result && department.getManager() != null && !department.getManager().isEmpty()) {
            setManagerRole(department.getManager(), "dept_admin");
        }
        return result;
    }

    /**
     * 更新部门（同步更新负责人角色）
     */
    @Transactional
    public boolean update(Department department) {
        // 获取旧的部门信息，恢复旧负责人角色
        Department oldDept = departmentMapper.selectById(department.getId());
        if (oldDept != null && oldDept.getManager() != null && !oldDept.getManager().isEmpty()) {
            // 旧负责人改回 user（如果不再是任何部门的负责人）
            if (!oldDept.getManager().equals(department.getManager())) {
                setManagerRole(oldDept.getManager(), "user");
            }
        }
        // 使用UpdateWrapper显式处理manager可能为null的情况
        UpdateWrapper<Department> uw = new UpdateWrapper<>();
        uw.eq("id", department.getId())
          .set("name", department.getName())
          .set("description", department.getDescription())
          .set("manager", department.getManager() != null ? department.getManager() : "");
        boolean result = departmentMapper.update(null, uw) > 0;
        // 新负责人设为 dept_admin
        if (result && department.getManager() != null && !department.getManager().isEmpty()) {
            setManagerRole(department.getManager(), "dept_admin");
        }
        return result;
    }

    /**
     * 删除部门
     */
    public boolean delete(Integer id) {
        // 恢复负责人角色
        Department dept = departmentMapper.selectById(id);
        if (dept != null && dept.getManager() != null && !dept.getManager().isEmpty()) {
            setManagerRole(dept.getManager(), "user");
        }
        return departmentMapper.deleteById(id) > 0;
    }

    /**
     * 根据员工姓名设置其角色
     */
    private void setManagerRole(String employeeName, String role) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("name", employeeName);
        Employee emp = employeeMapper.selectOne(wrapper);
        if (emp != null) {
            emp.setRole(role);
            employeeMapper.updateById(emp);
        }
    }
}
