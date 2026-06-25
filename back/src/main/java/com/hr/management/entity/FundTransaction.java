package com.hr.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公积金帐务实体
 */
@TableName("fund_transaction")
public class FundTransaction {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer fundId;             // 公积金账户ID
    private String accountNo;           // 公积金账号
    private String name;                // 姓名
    private LocalDate transDate;        // 交易日期
    private BigDecimal debit;          // 借方（支出）
    private BigDecimal credit;         // 贷方（收入）
    private BigDecimal balance;        // 余额
    private String type;                // 交易类型 (deposit-缴存, withdrawal-提取, interest-利息, adjustment-调整)
    private String remark;              // 备注
    private LocalDateTime createTime;  // 创建时间

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getFundId() { return fundId; }
    public void setFundId(Integer fundId) { this.fundId = fundId; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getTransDate() { return transDate; }
    public void setTransDate(LocalDate transDate) { this.transDate = transDate; }
    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
