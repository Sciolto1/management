<template>
  <div class="user-salary">
    <el-card>
      <template #header>
        <span><el-icon><Money /></el-icon> 我的工资条</span>
      </template>
      <el-table :data="salaryStore.salaryRecords" stripe>
        <el-table-column prop="month" label="月份" width="100" />
        <el-table-column prop="baseSalary" label="基本工资" width="120">
          <template #default="{ row }">¥{{ row.baseSalary }}</template>
        </el-table-column>
        <el-table-column prop="bonus" label="奖金" width="100">
          <template #default="{ row }">
            <span style="color: #67c23a">+¥{{ row.bonus }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deduction" label="扣款" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">-¥{{ row.deduction }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualSalary" label="实发工资" width="120">
          <template #default="{ row }">
            <span style="font-weight: bold; color: #409eff; font-size: 16px">¥{{ row.actualSalary }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'paid' ? 'success' : 'warning'">
              {{ row.status === 'paid' ? '已发放' : '待发放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <el-empty v-if="salaryStore.salaryRecords.length === 0" description="暂无工资记录" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { Money } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useSalaryStore } from '../../stores/salary'

const authStore = useAuthStore()
const salaryStore = useSalaryStore()

onMounted(() => {
  if (authStore.currentUser?.employeeId) {
    salaryStore.fetchByEmployeeId(authStore.currentUser.employeeId)
  }
})
</script>

<style scoped>
:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
</style>
