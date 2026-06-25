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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    public List<Employee> list() {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.ne("role", "admin");
        return employeeMapper.selectList(wrapper);
    }

    public List<Employee> listByDepartment(String department) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("department", department).ne("role", "admin");
        return employeeMapper.selectList(wrapper);
    }

    public Page<Employee> page(int current, int size, String keyword, String department, String employeeCategory, String status) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.ne("role", "admin");
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword)
                    .or().like("employee_no", keyword)
                    .or().like("phone", keyword)
                    .or().like("position", keyword));
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq("department", department);
        }
        if (employeeCategory != null && !employeeCategory.isEmpty()) {
            wrapper.eq("employee_category", employeeCategory);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        Page<Employee> page = new Page<>(current, size);
        return employeeMapper.selectPage(page, wrapper);
    }

    public Employee getById(Integer id) {
        return employeeMapper.selectById(id);
    }

    public boolean add(Employee employee) {
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            employee.setRole("user");
        }
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword("123456");
        }
        if (employee.getStatus() == null || employee.getStatus().isEmpty()) {
            employee.setStatus("active");
        }
        if (employee.getEmployeeNo() == null || employee.getEmployeeNo().isEmpty()) {
            employee.setEmployeeNo("EMP" + System.currentTimeMillis());
        }
        try {
            return employeeMapper.insert(employee) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加员工失败: " + e.getMessage());
        }
    }

    @Transactional
    public boolean update(Employee employee) {
        Employee oldEmp = employeeMapper.selectById(employee.getId());
        boolean result = employeeMapper.updateById(employee) > 0;
        if (result && oldEmp != null) {
            String oldRole = oldEmp.getRole();
            String newRole = employee.getRole();
            if ("dept_admin".equals(oldRole) && !"dept_admin".equals(newRole)) {
                UpdateWrapper<Department> uw = new UpdateWrapper<>();
                uw.eq("manager", oldEmp.getName()).set("manager", "");
                departmentMapper.update(null, uw);
            }
            if (!"dept_admin".equals(oldRole) && "dept_admin".equals(newRole)) {
                QueryWrapper<Department> dw = new QueryWrapper<>();
                dw.eq("name", employee.getDepartment() != null ? employee.getDepartment() : oldEmp.getDepartment());
                Department dept = departmentMapper.selectOne(dw);
                if (dept != null) {
                    dept.setManager(employee.getName() != null ? employee.getName() : oldEmp.getName());
                    departmentMapper.updateById(dept);
                }
            }
        }
        return result;
    }

    public boolean delete(Integer id) {
        return employeeMapper.deleteById(id) > 0;
    }

    public boolean batchDelete(List<Integer> ids) {
        return employeeMapper.deleteBatchIds(ids) > 0;
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        long total = employeeMapper.selectCount(new QueryWrapper<Employee>().ne("role", "admin"));
        long activeCount = employeeMapper.selectCount(new QueryWrapper<Employee>().eq("status", "active"));
        long leaveCount = employeeMapper.selectCount(new QueryWrapper<Employee>().eq("status", "left"));
        long retireCount = employeeMapper.selectCount(new QueryWrapper<Employee>().eq("status", "retired"));

        List<Employee> employees = employeeMapper.selectList(new QueryWrapper<Employee>().ne("role", "admin"));
        Map<String, Long> deptMap = new HashMap<>();
        Map<String, Long> categoryMap = new HashMap<>();
        for (Employee emp : employees) {
            String dept = emp.getDepartment() != null ? emp.getDepartment() : "未分配";
            deptMap.put(dept, deptMap.getOrDefault(dept, 0L) + 1);
            String cat = emp.getEmployeeCategory() != null ? emp.getEmployeeCategory() : "未分类";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0L) + 1);
        }

        result.put("total", total);
        result.put("activeCount", activeCount);
        result.put("leaveCount", leaveCount);
        result.put("retireCount", retireCount);
        result.put("deptStatistics", deptMap);
        result.put("categoryStatistics", categoryMap);
        return result;
    }

    public List<Employee> search(String name, String department, String position) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.ne("role", "admin");
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq("department", department);
        }
        if (position != null && !position.isEmpty()) {
            wrapper.like("position", position);
        }
        return employeeMapper.selectList(wrapper);
    }
}
