package com.hr.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.management.common.Result;
import com.hr.management.entity.TransferRecord;
import com.hr.management.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String transferType,
            @RequestParam(required = false) String status) {
        Page<TransferRecord> page = transferService.page(current, size, keyword, transferType, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        TransferRecord record = transferService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/employee/{employeeId}")
    public Result getByEmployeeId(@PathVariable Integer employeeId) {
        List<TransferRecord> list = transferService.getByEmployeeId(employeeId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TransferRecord record) {
        boolean success = transferService.createTransfer(record);
        return success ? Result.success("调动申请已提交") : Result.error("提交失败");
    }

    @PutMapping("/approve/{id}")
    public Result approve(@PathVariable Integer id, @RequestParam String approver) {
        boolean success = transferService.approveTransfer(id, approver);
        return success ? Result.success("审批通过") : Result.error("审批失败");
    }

    @PutMapping("/reject/{id}")
    public Result reject(@PathVariable Integer id,
                         @RequestParam String approver,
                         @RequestParam(required = false) String remark) {
        boolean success = transferService.rejectTransfer(id, approver, remark);
        return success ? Result.success("已驳回") : Result.error("操作失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = transferService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batchDelete")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        boolean success = transferService.batchDelete(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }

    @GetMapping("/statistics")
    public Result statistics() {
        Map<String, Object> stats = transferService.getStatistics();
        return Result.success(stats);
    }
}
