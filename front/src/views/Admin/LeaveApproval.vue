<template>
  <div class="leave-approval">
    <!-- 待审批 -->
    <el-card class="mb-20">
      <template #header>
        <div class="card-header">
          <span><el-icon><Clock /></el-icon> 待审批请假申请</span>
          <el-input v-model="searchPending" placeholder="搜索申请人" clearable style="width: 180px" />
        </div>
      </template>
      <el-table :data="pagedPending" stripe style="width: 100%">
        <el-table-column prop="employeeName" label="申请人" />
        <el-table-column prop="type" label="请假类型">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="createDate" label="申请日期" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="success" text size="small" @click="handleApprove(row)">
              <el-icon><Check /></el-icon> 通过
            </el-button>
            <el-button type="danger" text size="small" @click="handleReject(row)">
              <el-icon><Close /></el-icon> 拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="filteredPending.length === 0" description="暂无待审批申请" />
      <div v-if="filteredPending.length > 0" class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredPending.length" :page-size="pageSize" v-model:current-page="pendingPage" />
      </div>
    </el-card>

    <!-- 已处理 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Document /></el-icon> 已处理记录</span>
          <el-input v-model="searchProcessed" placeholder="搜索申请人" clearable style="width: 180px" />
        </div>
      </template>
      <el-table :data="pagedProcessed" stripe style="width: 100%">
        <el-table-column prop="employeeName" label="申请人" />
        <el-table-column prop="type" label="请假类型">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : 'danger'">
              {{ row.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="filteredProcessed.length === 0" description="暂无处理记录" />
      <div v-if="filteredProcessed.length > 0" class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredProcessed.length" :page-size="pageSize" v-model:current-page="processedPage" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Document, Check, Close } from '@element-plus/icons-vue'
import { useLeaveStore } from '../../stores/leave'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'

const leaveStore = useLeaveStore()
const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const currentEmployeeId = computed(() => authStore.currentUser?.employeeId)
const deptEmployeeIds = computed(() => new Set(employeeStore.employees.map(e => e.id)))

const searchPending = ref('')
const searchProcessed = ref('')
const pendingPage = ref(1)
const processedPage = ref(1)
const pageSize = 10

const filteredLeaves = computed(() => {
  let list = leaveStore.leaveRequests
  if (isDeptAdmin.value) {
    list = list.filter(l => deptEmployeeIds.value.has(l.employeeId) && l.employeeId !== currentEmployeeId.value)
  }
  return list
})

const filteredPending = computed(() => {
  let list = filteredLeaves.value.filter(l => l.status === 'pending')
  if (searchPending.value) {
    list = list.filter(l => l.employeeName && l.employeeName.includes(searchPending.value))
  }
  return list
})

const filteredProcessed = computed(() => {
  let list = filteredLeaves.value.filter(l => l.status !== 'pending')
  if (searchProcessed.value) {
    list = list.filter(l => l.employeeName && l.employeeName.includes(searchProcessed.value))
  }
  return list
})

const pagedPending = computed(() => {
  const start = (pendingPage.value - 1) * pageSize
  return filteredPending.value.slice(start, start + pageSize)
})

const pagedProcessed = computed(() => {
  const start = (processedPage.value - 1) * pageSize
  return filteredProcessed.value.slice(start, start + pageSize)
})

onMounted(async () => {
  await employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined)
  leaveStore.fetchLeaveRequests()
})

const getTypeLabel = (type) => {
  const map = { annual: '年假', sick: '病假', personal: '事假' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { annual: 'success', sick: 'warning', personal: 'info' }
  return map[type] || ''
}

const handleApprove = async (row) => {
  await leaveStore.approveLeave(row.id, 'approved')
  ElMessage.success('已通过')
}

const handleReject = async (row) => {
  await leaveStore.approveLeave(row.id, 'rejected')
  ElMessage.success('已拒绝')
}
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
