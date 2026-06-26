package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.management.entity.Employee;
import com.hr.management.entity.TransferRecord;
import com.hr.management.mapper.EmployeeMapper;
import com.hr.management.mapper.TransferRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransferService {

    @Autowired
    private TransferRecordMapper transferRecordMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    public Page<TransferRecord> page(int current, int size, String keyword, String transferType, String status) {
        QueryWrapper<TransferRecord> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("employee_name", keyword)
                    .or().like("employee_no", keyword)
                    .or().like("transfer_no", keyword));
        }
        if (transferType != null && !transferType.isEmpty()) {
            wrapper.eq("transfer_type", transferType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        Page<TransferRecord> page = new Page<>(current, size);
        return transferRecordMapper.selectPage(page, wrapper);
    }

    public TransferRecord getById(Integer id) {
        return transferRecordMapper.selectById(id);
    }

    public List<TransferRecord> getByEmployeeId(Integer employeeId) {
        QueryWrapper<TransferRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_id", employeeId).orderByDesc("transfer_date");
        return transferRecordMapper.selectList(wrapper);
    }

    @Transactional
    public boolean createTransfer(TransferRecord record) {
        Employee emp = employeeMapper.selectById(record.getEmployeeId());
        if (emp == null) {
            throw new RuntimeException("员工不存在");
        }
        String transferNo = "TR" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        record.setTransferNo(transferNo);
        record.setEmployeeName(emp.getName());
        record.setEmployeeNo(emp.getEmployeeNo());
        record.setSourceDepartment(emp.getDepartment());
        record.setSourcePosition(emp.getPosition());
        record.setSourceTeamGroup(emp.getTeamGroup());
        record.setSourceEmployeeCategory(emp.getEmployeeCategory());
        record.setSourceBaseSalary(emp.getBaseSalary());
        if (record.getStatus() == null || record.getStatus().isEmpty()) {
            record.setStatus("pending");
        }
        return transferRecordMapper.insert(record) > 0;
    }

    @Transactional
    public boolean approveTransfer(Integer id, String approver) {
        TransferRecord record = transferRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("调动记录不存在");
        }
        if (!"pending".equals(record.getStatus())) {
            throw new RuntimeException("该调动已处理，不能重复审批");
        }
        Employee emp = employeeMapper.selectById(record.getEmployeeId());
        if (emp == null) {
            throw new RuntimeException("员工不存在");
        }
        String transferType = record.getTransferType();

        if ("internal".equals(transferType) || "external_in".equals(transferType)) {
            if (record.getTargetDepartment() != null) {
                emp.setDepartment(record.getTargetDepartment());
            }
            if (record.getTargetPosition() != null) {
                emp.setPosition(record.getTargetPosition());
            }
            if (record.getTargetTeamGroup() != null) {
                emp.setTeamGroup(record.getTargetTeamGroup());
            }
            if (record.getTargetEmployeeCategory() != null) {
                emp.setEmployeeCategory(record.getTargetEmployeeCategory());
            }
            if (record.getTargetBaseSalary() != null) {
                emp.setBaseSalary(record.getTargetBaseSalary());
            }
            if ("external_in".equals(transferType)) {
                emp.setStatus("active");
            }
        } else if ("retire".equals(transferType)) {
            emp.setStatus("retired");
        } else if ("dismiss".equals(transferType)) {
            emp.setStatus("dismissed");
        } else if ("external_out".equals(transferType)) {
            emp.setStatus("left");
        }

        employeeMapper.updateById(emp);
        record.setStatus("approved");
        record.setApprover(approver);
        return transferRecordMapper.updateById(record) > 0;
    }

    @Transactional
    public boolean rejectTransfer(Integer id, String approver, String remark) {
        TransferRecord record = transferRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("调动记录不存在");
        }
        if (!"pending".equals(record.getStatus())) {
            throw new RuntimeException("该调动已处理，不能重复审批");
        }
        record.setStatus("rejected");
        record.setApprover(approver);
        if (remark != null) {
            record.setRemark(remark);
        }
        return transferRecordMapper.updateById(record) > 0;
    }

    public boolean delete(Integer id) {
        return transferRecordMapper.deleteById(id) > 0;
    }

    public boolean batchDelete(List<Integer> ids) {
        return transferRecordMapper.deleteBatchIds(ids) > 0;
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        long total = transferRecordMapper.selectCount(new QueryWrapper<>());
        long pendingCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("status", "pending"));
        long approvedCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("status", "approved"));
        long rejectedCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("status", "rejected"));

        long internalCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("transfer_type", "internal"));
        long externalInCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("transfer_type", "external_in"));
        long externalOutCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("transfer_type", "external_out"));
        long retireCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("transfer_type", "retire"));
        long dismissCount = transferRecordMapper.selectCount(new QueryWrapper<TransferRecord>().eq("transfer_type", "dismiss"));

        result.put("total", total);
        result.put("pendingCount", pendingCount);
        result.put("approvedCount", approvedCount);
        result.put("rejectedCount", rejectedCount);
        result.put("internalCount", internalCount);
        result.put("externalInCount", externalInCount);
        result.put("externalOutCount", externalOutCount);
        result.put("retireCount", retireCount);
        result.put("dismissCount", dismissCount);
        return result;
    }
}
