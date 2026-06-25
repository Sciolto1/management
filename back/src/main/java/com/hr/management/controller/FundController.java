package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Fund;
import com.hr.management.entity.FundTransaction;
import com.hr.management.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 公积金控制器
 */
@RestController
@RequestMapping("/api/fund")
public class FundController {

    @Autowired
    private FundService fundService;

    // ==================== 公积金账户管理 ====================

    /**
     * 获取所有公积金账户
     */
    @GetMapping("/list")
    public Result<List<Fund>> list(@RequestParam(required = false) String department,
                      @RequestParam(required = false) String status) {
        List<Fund> list;
        if (department != null && !department.isEmpty()) {
            list = fundService.listByDepartment(department);
        } else if (status != null && !status.isEmpty()) {
            list = fundService.listByStatus(status);
        } else {
            list = fundService.list();
        }
        return Result.success(list);
    }

    /**
     * 获取有效账户
     */
    @GetMapping("/active")
    public Result<List<Fund>> listActive() {
        List<Fund> list = fundService.listActiveAccounts();
        return Result.success(list);
    }

    /**
     * 根据ID获取公积金账户
     */
    @GetMapping("/{id}")
    public Result<Fund> getById(@PathVariable Integer id) {
        Fund fund = fundService.getById(id);
        return Result.success(fund);
    }

    /**
     * 根据账号获取
     */
    @GetMapping("/account/{accountNo}")
    public Result<Fund> getByAccountNo(@PathVariable String accountNo) {
        Fund fund = fundService.getByAccountNo(accountNo);
        return Result.success(fund);
    }

    /**
     * 根据员工ID获取
     */
    @GetMapping("/employee/{employeeId}")
    public Result<Fund> getByEmployeeId(@PathVariable Integer employeeId) {
        Fund fund = fundService.getByEmployeeId(employeeId);
        return Result.success(fund);
    }

    /**
     * 添加公积金账户
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Fund fund) {
        boolean success = fundService.add(fund);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新公积金账户
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Fund fund) {
        boolean success = fundService.update(fund);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 封存账户
     */
    @PutMapping("/seal/{id}")
    public Result<Void> seal(@PathVariable Integer id) {
        boolean success = fundService.seal(id);
        return success ? Result.success() : Result.error("封存失败");
    }

    /**
     * 解封账户（开户）
     */
    @PutMapping("/unseal/{id}")
    public Result<Void> unseal(@PathVariable Integer id) {
        boolean success = fundService.unseal(id);
        return success ? Result.success() : Result.error("解封失败");
    }

    /**
     * 缴存公积金
     */
    @PostMapping("/deposit/{id}")
    public Result<Void> deposit(@PathVariable Integer id) {
        boolean success = fundService.deposit(id);
        return success ? Result.success() : Result.error("缴存失败");
    }

    /**
     * 删除公积金账户
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = fundService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        Map<String, Object> stats = fundService.getStatistics();
        return Result.success(stats);
    }

    // ==================== 公积金帐务管理 ====================

    /**
     * 获取指定公积金的帐务记录
     */
    @GetMapping("/transaction/list/{fundId}")
    public Result<List<FundTransaction>> getTransactions(@PathVariable Integer fundId) {
        List<FundTransaction> list = fundService.getTransactions(fundId);
        return Result.success(list);
    }

    /**
     * 获取所有帐务记录
     */
    @GetMapping("/transaction/all")
    public Result<List<FundTransaction>> getAllTransactions() {
        List<FundTransaction> list = fundService.getAllTransactions();
        return Result.success(list);
    }

    /**
     * 根据时间范围查询帐务
     */
    @GetMapping("/transaction/dateRange")
    public Result<List<FundTransaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FundTransaction> list = fundService.getTransactionsByDateRange(startDate, endDate);
        return Result.success(list);
    }

    /**
     * 根据类型查询帐务
     */
    @GetMapping("/transaction/type/{type}")
    public Result<List<FundTransaction>> getTransactionsByType(@PathVariable String type) {
        List<FundTransaction> list = fundService.getTransactionsByType(type);
        return Result.success(list);
    }

    /**
     * 添加帐务记录
     */
    @PostMapping("/transaction/add")
    public Result<Void> addTransaction(@RequestBody FundTransaction transaction) {
        boolean success = fundService.addTransaction(transaction);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 删除帐务记录
     */
    @DeleteMapping("/transaction/delete/{id}")
    public Result<Void> deleteTransaction(@PathVariable Integer id) {
        boolean success = fundService.deleteTransaction(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量缴存（每月执行）
     */
    @PostMapping("/batchDeposit")
    public Result<String> batchDeposit(@RequestBody Map<String, List<Integer>> params) {
        List<Integer> ids = params.get("ids");
        int successCount = 0;
        for (Integer id : ids) {
            if (fundService.deposit(id)) {
                successCount++;
            }
        }
        return Result.success(successCount + " 条记录缴存成功");
    }
}
