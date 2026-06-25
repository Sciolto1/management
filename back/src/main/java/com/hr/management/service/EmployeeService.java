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

/**
 * 员工服务
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 获取所有员工（不含超级管理员）
     */
    public List<Employee> list() {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.ne("role", "admin");
        return employeeMapper.selectList(wrapper);
    }

    /**
     * 按部门获取员工
     */
    public List<Employee> listByDepartment(String department) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("department", department).ne("role", "admin");
        return employeeMapper.selectList(wrapper);
    }

    /**
     * 根据ID获取员工
     */
    public Employee getById(Integer id) {
        return employeeMapper.selectById(id);
    }

    /**
     * 添加员工（用户名密码角色直接存储在employee表）
     */
    public boolean add(Employee employee) {
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            employee.setRole("user");
        }
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword("123456");
        }
        return employeeMapper.insert(employee) > 0;
    }

    /**
     * 更新员工（同步部门负责人）
     */
    @Transactional
    public boolean update(Employee employee) {
        Employee oldEmp = employeeMapper.selectById(employee.getId());
        boolean result = employeeMapper.updateById(employee) > 0;
        if (result && oldEmp != null) {
            String oldRole = oldEmp.getRole();
            String newRole = employee.getRole();
            // 从dept_admin改为其他角色：清空部门负责人
            if ("dept_admin".equals(oldRole) && !"dept_admin".equals(newRole)) {
                UpdateWrapper<Department> uw = new UpdateWrapper<>();
                uw.eq("manager", oldEmp.getName()).set("manager", "");
                departmentMapper.update(null, uw);
            }
            // 从其他角色改为dept_admin：设置部门负责人
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

    /**
     * 删除员工
     */
    public boolean delete(Integer id) {
        return employeeMapper.deleteById(id) > 0;
    }
}
