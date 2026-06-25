package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Department> list() {
        List<Department> departments = departmentMapper.selectList(null);
        List<Employee> employees = employeeMapper.selectList(null);
        Map<String, Long> countMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        departments.forEach(d -> d.setEmployeeCount(countMap.getOrDefault(d.getName(), 0L).intValue()));
        return departments;
    }

    public Page<Department> page(int current, int size, String keyword, String status) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword)
                    .or().like("dept_code", keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        Page<Department> page = new Page<>(current, size);
        Page<Department> result = departmentMapper.selectPage(page, wrapper);
        List<Employee> employees = employeeMapper.selectList(null);
        Map<String, Long> countMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        result.getRecords().forEach(d -> d.setEmployeeCount(countMap.getOrDefault(d.getName(), 0L).intValue()));
        return result;
    }

    public Department getById(Integer id) {
        return departmentMapper.selectById(id);
    }

    public boolean add(Department department) {
        if (department.getStatus() == null || department.getStatus().isEmpty()) {
            department.setStatus("active");
        }
        boolean result = departmentMapper.insert(department) > 0;
        if (result && department.getManager() != null && !department.getManager().isEmpty()) {
            setManagerRole(department.getManager(), "dept_admin");
        }
        return result;
    }

    @Transactional
    public boolean update(Department department) {
        Department oldDept = departmentMapper.selectById(department.getId());
        if (oldDept != null && oldDept.getManager() != null && !oldDept.getManager().isEmpty()) {
            if (!oldDept.getManager().equals(department.getManager())) {
                setManagerRole(oldDept.getManager(), "user");
            }
        }
        UpdateWrapper<Department> uw = new UpdateWrapper<>();
        uw.eq("id", department.getId())
          .set("name", department.getName())
          .set("dept_code", department.getDeptCode())
          .set("description", department.getDescription())
          .set("remark", department.getRemark())
          .set("status", department.getStatus())
          .set("manager", department.getManager() != null ? department.getManager() : "");
        boolean result = departmentMapper.update(null, uw) > 0;
        if (result && department.getManager() != null && !department.getManager().isEmpty()) {
            setManagerRole(department.getManager(), "dept_admin");
        }
        return result;
    }

    public boolean delete(Integer id) {
        Department dept = departmentMapper.selectById(id);
        if (dept != null && dept.getManager() != null && !dept.getManager().isEmpty()) {
            setManagerRole(dept.getManager(), "user");
        }
        return departmentMapper.deleteById(id) > 0;
    }

    public boolean batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            Department dept = departmentMapper.selectById(id);
            if (dept != null && dept.getManager() != null && !dept.getManager().isEmpty()) {
                setManagerRole(dept.getManager(), "user");
            }
        }
        return departmentMapper.deleteBatchIds(ids) > 0;
    }

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
