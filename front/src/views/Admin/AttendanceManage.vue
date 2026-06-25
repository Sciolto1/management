<template>
  <div class="attendance-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Clock /></el-icon> 考勤记录</span>
          <div class="filter-area">
            <el-input v-model="searchName" placeholder="搜索员工姓名" clearable style="width: 160px; margin-right: 10px" @clear="handleSearch" />
            <el-select v-model="searchStatus" placeholder="考勤状态" clearable style="width: 120px; margin-right: 10px" @clear="handleSearch">
              <el-option label="正常" value="normal" />
              <el-option label="迟到" value="late" />
              <el-option label="早退" value="early_leave" />
              <el-option label="缺勤" value="absent" />
              <el-option label="异常" value="abnormal" />
            </el-select>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 240px; margin-right: 10px" />
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="pagedRecords" stripe style="width: 100%">
        <el-table-column prop="employeeName" label="员工姓名" />
        <el-table-column prop="date" label="日期" />
        <el-table-column prop="clockIn" label="上班打卡">
          <template #default="{ row }">
            <el-tag v-if="row.clockIn" type="success">{{ row.clockIn }}</el-tag>
            <el-tag v-else type="info">未打卡</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clockOut" label="下班打卡">
          <template #default="{ row }">
            <el-tag v-if="row.clockOut" type="success">{{ row.clockOut }}</el-tag>
            <el-tag v-else type="warning">未打卡</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="考勤状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-select v-model="row.status" size="small" style="width: 100px" @change="(val) => handleStatusChange(row, val)">
              <el-option label="正常" value="normal" />
              <el-option label="迟到" value="late" />
              <el-option label="早退" value="early_leave" />
              <el-option label="缺勤" value="absent" />
              <el-option label="异常" value="abnormal" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredRecords.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Search } from '@element-plus/icons-vue'
import { useAttendanceStore } from '../../stores/attendance'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'

const attendanceStore = useAttendanceStore()
const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const deptEmployeeIds = computed(() => new Set(employeeStore.employees.map(e => e.id)))
const searchName = ref('')
const searchStatus = ref('')
const dateRange = ref(null)
const currentPage = ref(1)
const pageSize = 10

onMounted(async () => {
  await employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined)
  attendanceStore.fetchAllRecords()
})

const filteredRecords = computed(() => {
  let records = attendanceStore.allRecords
  if (isDeptAdmin.value) {
    records = records.filter(r => deptEmployeeIds.value.has(r.employeeId))
  }
  if (searchName.value) {
    records = records.filter(r => r.employeeName && r.employeeName.includes(searchName.value))
  }
  if (searchStatus.value) {
    records = records.filter(r => r.status === searchStatus.value)
  }
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    records = records.filter(r => r.date >= start && r.date <= end)
  }
  return records
})

const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredRecords.value.slice(start, start + pageSize)
})

const statusLabel = (status) => {
  const map = { normal: '正常', late: '迟到', early_leave: '早退', absent: '缺勤', abnormal: '异常' }
  return map[status] || '正常'
}
const statusType = (status) => {
  const map = { normal: 'success', late: 'warning', early_leave: 'warning', absent: 'danger', abnormal: 'danger' }
  return map[status] || 'success'
}

const handleStatusChange = async (row, val) => {
  await attendanceStore.updateStatus(row.id, val)
  ElMessage.success('状态已更新')
}

const handleSearch = () => {
  currentPage.value = 1
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
.filter-area {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
