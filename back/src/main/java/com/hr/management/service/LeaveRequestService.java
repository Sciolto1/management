package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.LeaveRequest;
import com.hr.management.mapper.LeaveRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 请假申请服务
 */
@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    /**
     * 获取所有请假申请
     */
    public List<LeaveRequest> list() {
        QueryWrapper<LeaveRequest> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return leaveRequestMapper.selectList(wrapper);
    }

    /**
     * 获取待审批请假
     */
    public List<LeaveRequest> getPendingList() {
        QueryWrapper<LeaveRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "pending").orderByDesc("create_time");
        return leaveRequestMapper.selectList(wrapper);
    }

    /**
     * 获取已处理请假
     */
    public List<LeaveRequest> getProcessedList() {
        QueryWrapper<LeaveRequest> wrapper = new QueryWrapper<>();
        wrapper.ne("status", "pending").orderByDesc("create_time");
        return leaveRequestMapper.selectList(wrapper);
    }

    /**
     * 根据员工ID获取请假记录
     */
    public List<LeaveRequest> getByEmployeeId(Integer employeeId) {
        QueryWrapper<LeaveRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).orderByDesc("create_time");
        return leaveRequestMapper.selectList(wrapper);
    }

    /**
     * 添加请假申请
     */
    public boolean add(LeaveRequest leaveRequest) {
        leaveRequest.setStatus("pending");
        return leaveRequestMapper.insert(leaveRequest) > 0;
    }

    /**
     * 审批
     */
    public boolean approve(Integer id, String status) {
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(id);
        if (leaveRequest != null) {
            leaveRequest.setStatus(status);
            return leaveRequestMapper.updateById(leaveRequest) > 0;
        }
        return false;
    }

    /**
     * 删除请假申请
     */
    public boolean delete(Integer id) {
        return leaveRequestMapper.deleteById(id) > 0;
    }
}
