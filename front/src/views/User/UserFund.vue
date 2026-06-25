<template>
  <div class="user-fund">
    <!-- 公积金账户信息 -->
    <el-card class="mb-20">
      <template #header>
        <span><el-icon><Wallet /></el-icon> 我的公积金账户</span>
      </template>
      <div v-if="fundInfo" class="fund-content">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">公积金账号</span>
              <span class="value">{{ fundInfo.accountNo }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">账户状态</span>
              <el-tag :type="getStatusType(fundInfo.status)">
                {{ getStatusLabel(fundInfo.status) }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">开户日期</span>
              <span class="value">{{ fundInfo.openDate }}</span>
            </div>
          </el-col>
        </el-row>
        <el-divider />
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">个人缴额</span>
              <span class="value money">¥{{ formatMoney(fundInfo.personalPay) }}/月</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">单位缴额</span>
              <span class="value money">¥{{ formatMoney(fundInfo.companyPay) }}/月</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item highlight">
              <span class="label">账户余额</span>
              <span class="value money large">¥{{ formatMoney(fundInfo.balance) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
      <el-empty v-else description="暂无公积金账户信息" />
    </el-card>

    <!-- 缴存记录 -->
    <el-card v-if="fundInfo">
      <template #header>
        <span><el-icon><Tickets /></el-icon> 缴存记录</span>
      </template>
      <el-table :data="transactions" stripe>
        <el-table-column prop="transDate" label="日期" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTransType(row.type)">{{ getTransLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收入" width="100">
          <template #default="{ row }">
            <span class="credit">¥{{ formatMoney(row.credit) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支出" width="100">
          <template #default="{ row }">
            <span class="debit">¥{{ formatMoney(row.debit) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="余额" width="120">
          <template #default="{ row }">
            <span class="balance">¥{{ formatMoney(row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <el-empty v-if="transactions.length === 0" description="暂无缴存记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Wallet, Tickets } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useFundStore } from '../../stores/fund'

const authStore = useAuthStore()
const fundStore = useFundStore()

const fundInfo = ref(null)
const transactions = ref([])

onMounted(async () => {
  if (authStore.currentUser?.employeeId) {
    // 根据员工ID获取公积金账户
    const fund = await fundStore.getFundByEmployeeId(authStore.currentUser.employeeId)
    if (fund) {
      fundInfo.value = fund
      // 获取缴存记录
      const trans = await fundStore.fetchTransactions(fund.id)
      transactions.value = trans || []
    }
  }
})

const formatMoney = (value) => {
  if (!value) return '0.00'
  return parseFloat(value).toFixed(2)
}

const getStatusType = (status) => {
  const map = { active: 'success', sealed: 'warning', closed: 'info' }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = { active: '正常', sealed: '封存', closed: '注销' }
  return map[status] || status
}

const getTransType = (type) => {
  const map = {
    deposit: 'success',
    withdrawal: 'warning',
    interest: 'primary',
    adjustment: 'info',
    open: 'info',
    seal: 'danger',
    unseal: 'success'
  }
  return map[type] || 'info'
}

const getTransLabel = (type) => {
  const map = {
    deposit: '缴存',
    withdrawal: '提取',
    interest: '利息',
    adjustment: '调整',
    open: '开户',
    seal: '封存',
    unseal: '解封'
  }
  return map[type] || type
}
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}

.fund-content {
  padding: 10px 0;
}

.info-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.info-item .label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.info-item .value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.info-item .value.money {
  color: #67c23a;
}

.info-item .value.large {
  font-size: 24px;
}

.info-item.highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.info-item.highlight .label {
  color: rgba(255, 255, 255, 0.8);
}

.info-item.highlight .value {
  color: white;
}

.info-item.highlight .value.money {
  color: white;
}

:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.credit {
  color: #67c23a;
}

.debit {
  color: #f56c6c;
}

.balance {
  color: #409eff;
  font-weight: bold;
}
</style>
