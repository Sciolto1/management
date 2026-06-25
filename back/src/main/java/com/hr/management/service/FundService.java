package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Fund;
import com.hr.management.entity.FundTransaction;
import com.hr.management.mapper.FundMapper;
import com.hr.management.mapper.FundTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公积金服务
 */
@Service
public class FundService {

    @Autowired
    private FundMapper fundMapper;

    @Autowired
    private FundTransactionMapper fundTransactionMapper;

    /**
     * 获取所有公积金账户
     */
    public List<Fund> list() {
        QueryWrapper<Fund> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return fundMapper.selectList(wrapper);
    }

    /**
     * 根据ID获取公积金账户
     */
    public Fund getById(Integer id) {
        return fundMapper.selectById(id);
    }

    /**
     * 根据账号获取
     */
    public Fund getByAccountNo(String accountNo) {
        return fundMapper.selectByAccountNo(accountNo);
    }

    /**
     * 根据员工ID获取
     */
    public Fund getByEmployeeId(Integer employeeId) {
        return fundMapper.selectByEmployeeId(employeeId);
    }

    /**
     * 根据部门查询
     */
    public List<Fund> listByDepartment(String department) {
        return fundMapper.selectByDepartment(department);
    }

    /**
     * 根据状态查询
     */
    public List<Fund> listByStatus(String status) {
        return fundMapper.selectByStatus(status);
    }

    /**
     * 获取有效账户
     */
    public List<Fund> listActiveAccounts() {
        QueryWrapper<Fund> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "active");
        wrapper.orderByDesc("create_time");
        return fundMapper.selectList(wrapper);
    }

    /**
     * 添加公积金账户
     */
    @Transactional
    public boolean add(Fund fund) {
        // 生成公积金账号
        if (fund.getAccountNo() == null || fund.getAccountNo().isEmpty()) {
            fund.setAccountNo(generateAccountNo());
        }
        // 默认状态为正常
        if (fund.getStatus() == null || fund.getStatus().isEmpty()) {
            fund.setStatus("active");
        }
        // 默认开户日期
        if (fund.getOpenDate() == null) {
            fund.setOpenDate(LocalDate.now());
        }
        // 初始余额为0
        if (fund.getBalance() == null) {
            fund.setBalance(BigDecimal.ZERO);
        }

        int result = fundMapper.insert(fund);
        if (result > 0) {
            // 创建初始开户记录
            createTransaction(fund, "open", "开户", BigDecimal.ZERO);
            return true;
        }
        return false;
    }

    /**
     * 更新公积金账户
     */
    @Transactional
    public boolean update(Fund fund) {
        Fund oldFund = fundMapper.selectById(fund.getId());
        int result = fundMapper.updateById(fund);

        if (result > 0 && oldFund != null) {
            // 如果缴额发生变化，记录调整
            if (!fund.getPersonalPay().equals(oldFund.getPersonalPay()) ||
                !fund.getCompanyPay().equals(oldFund.getCompanyPay())) {
                createTransaction(fund, "adjustment", "缴额调整", BigDecimal.ZERO);
            }
            return true;
        }
        return false;
    }

    /**
     * 封存账户
     */
    @Transactional
    public boolean seal(Integer id) {
        Fund fund = fundMapper.selectById(id);
        if (fund != null) {
            fund.setStatus("sealed");
            int result = fundMapper.updateById(fund);
            if (result > 0) {
                createTransaction(fund, "seal", "账户封存", BigDecimal.ZERO);
                return true;
            }
        }
        return false;
    }

    /**
     * 解封账户（开户）
     */
    @Transactional
    public boolean unseal(Integer id) {
        Fund fund = fundMapper.selectById(id);
        if (fund != null) {
            fund.setStatus("active");
            int result = fundMapper.updateById(fund);
            if (result > 0) {
                createTransaction(fund, "unseal", "账户解封", BigDecimal.ZERO);
                return true;
            }
        }
        return false;
    }

    /**
     * 缴存公积金（每月自动调用）
     */
    @Transactional
    public boolean deposit(Integer id) {
        Fund fund = fundMapper.selectById(id);
        if (fund != null && "active".equals(fund.getStatus())) {
            BigDecimal personalPay = fund.getPersonalPay() != null ? fund.getPersonalPay() : BigDecimal.ZERO;
            BigDecimal companyPay = fund.getCompanyPay() != null ? fund.getCompanyPay() : BigDecimal.ZERO;
            BigDecimal amount = personalPay.add(companyPay);

            // 更新余额
            BigDecimal newBalance = fund.getBalance().add(amount);
            fund.setBalance(newBalance);
            fundMapper.updateById(fund);

            // 记录缴存
            createTransaction(fund, "deposit", "月度缴存", amount);
            return true;
        }
        return false;
    }

    /**
     * 删除公积金账户
     */
    public boolean delete(Integer id) {
        return fundMapper.deleteById(id) > 0;
    }

    /**
     * 创建帐务记录
     */
    private void createTransaction(Fund fund, String type, String remark, BigDecimal amount) {
        FundTransaction transaction = new FundTransaction();
        transaction.setFundId(fund.getId());
        transaction.setAccountNo(fund.getAccountNo());
        transaction.setName(fund.getName());
        transaction.setTransDate(LocalDate.now());
        transaction.setType(type);

        if ("deposit".equals(type)) {
            transaction.setCredit(amount);
            transaction.setDebit(BigDecimal.ZERO);
        } else if ("withdrawal".equals(type)) {
            transaction.setDebit(amount);
            transaction.setCredit(BigDecimal.ZERO);
        } else {
            transaction.setDebit(BigDecimal.ZERO);
            transaction.setCredit(BigDecimal.ZERO);
        }

        // 获取最新余额
        BigDecimal latestBalance = fundTransactionMapper.getLatestBalance(fund.getId());
        if (latestBalance == null) {
            latestBalance = fund.getBalance();
        }
        if ("deposit".equals(type)) {
            latestBalance = latestBalance.add(amount);
        } else if ("withdrawal".equals(type)) {
            latestBalance = latestBalance.subtract(amount);
        }
        transaction.setBalance(latestBalance);
        transaction.setRemark(remark);

        fundTransactionMapper.insert(transaction);
    }

    /**
     * 生成公积金账号
     */
    private String generateAccountNo() {
        return "GJJ" + System.currentTimeMillis();
    }

    // ==================== 帐务管理 ====================

    /**
     * 获取公积金帐务记录
     */
    public List<FundTransaction> getTransactions(Integer fundId) {
        return fundTransactionMapper.selectByFundId(fundId);
    }

    /**
     * 获取所有帐务记录
     */
    public List<FundTransaction> getAllTransactions() {
        QueryWrapper<FundTransaction> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("trans_date");
        return fundTransactionMapper.selectList(wrapper);
    }

    /**
     * 根据时间范围查询帐务
     */
    public List<FundTransaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return fundTransactionMapper.selectByDateRange(startDate, endDate);
    }

    /**
     * 根据类型查询帐务
     */
    public List<FundTransaction> getTransactionsByType(String type) {
        return fundTransactionMapper.selectByType(type);
    }

    /**
     * 添加帐务记录
     */
    @Transactional
    public boolean addTransaction(FundTransaction transaction) {
        // 计算余额
        BigDecimal latestBalance = fundTransactionMapper.getLatestBalance(transaction.getFundId());
        if (latestBalance == null) {
            Fund fund = fundMapper.selectById(transaction.getFundId());
            latestBalance = fund != null ? fund.getBalance() : BigDecimal.ZERO;
        }

        if (transaction.getCredit() != null) {
            latestBalance = latestBalance.add(transaction.getCredit());
        }
        if (transaction.getDebit() != null) {
            latestBalance = latestBalance.subtract(transaction.getDebit());
        }
        transaction.setBalance(latestBalance);

        return fundTransactionMapper.insert(transaction) > 0;
    }

    /**
     * 删除帐务记录
     */
    public boolean deleteTransaction(Integer id) {
        return fundTransactionMapper.deleteById(id) > 0;
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        List<Fund> allFunds = list();
        List<Fund> activeFunds = listActiveAccounts();
        List<Fund> sealedFunds = listByStatus("sealed");

        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Fund fund : allFunds) {
            if (fund.getBalance() != null) {
                totalBalance = totalBalance.add(fund.getBalance());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalAccounts", allFunds.size());
        result.put("activeAccounts", activeFunds.size());
        result.put("sealedAccounts", sealedFunds.size());
        result.put("totalBalance", totalBalance);
        return result;
    }
}
