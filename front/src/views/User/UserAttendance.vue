<template>
  <div class="user-attendance">
    <!-- 打卡区域 -->
    <el-card class="mb-20">
      <template #header>
        <span><el-icon><Clock /></el-icon> 今日打卡</span>
      </template>
      <div class="clock-area">
        <div class="current-time">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
        <div class="clock-status">
          <div class="status-item">
            <span class="label">上班打卡</span>
            <span class="value" :class="{ done: attendanceStore.todayRecord?.clockIn }">
              {{ attendanceStore.todayRecord?.clockIn || '未打卡' }}
            </span>
          </div>
          <div class="status-item">
            <span class="label">下班打卡</span>
            <span class="value" :class="{ done: attendanceStore.todayRecord?.clockOut }">
              {{ attendanceStore.todayRecord?.clockOut || '未打卡' }}
            </span>
          </div>
        </div>
        <div class="clock-buttons">
          <el-button 
            v-if="!attendanceStore.todayRecord?.clockIn" 
            type="primary" 
            size="large" 
            @click="handleClockIn"
          >
            <el-icon><Sunrise /></el-icon> 上班打卡
          </el-button>
          <el-button 
            v-else-if="!attendanceStore.todayRecord?.clockOut" 
            type="success" 
            size="large" 
            @click="handleClockOut"
          >
            <el-icon><Sunset /></el-icon> 下班打卡
          </el-button>
          <el-tag v-else type="success" size="large">
            <el-icon><Check /></el-icon> 今日打卡已完成
          </el-tag>
        </div>
      </div>
    </el-card>

    <!-- 考勤记录 -->
    <el-card>
      <template #header>
        <span><el-icon><Document /></el-icon> 考勤记录</span>
      </template>
      <el-table :data="attendanceStore.attendanceRecords" stripe>
        <el-table-column prop="date" label="日期" width="150" />
        <el-table-column prop="clockIn" label="上班打卡" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.clockIn" type="success">{{ row.clockIn }}</el-tag>
            <el-tag v-else type="info">未打卡</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clockOut" label="下班打卡" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.clockOut" type="success">{{ row.clockOut }}</el-tag>
            <el-tag v-else type="info">未打卡</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.clockIn && row.clockOut" type="success">正常</el-tag>
            <el-tag v-else-if="row.clockIn" type="warning">未下班</el-tag>
            <el-tag v-else type="danger">缺勤</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="attendanceStore.attendanceRecords.length === 0" description="暂无考勤记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Document, Sunrise, Sunset, Check } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useAttendanceStore } from '../../stores/attendance'

const authStore = useAuthStore()
const attendanceStore = useAttendanceStore()

const currentTime = ref('')
const currentDate = ref('')
let timer = null

const updateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', { 
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' 
  })
  currentTime.value = now.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', minute: '2-digit', second: '2-digit' 
  })
}

const handleClockIn = async () => {
  try {
    await attendanceStore.clockIn(authStore.currentUser?.employeeId)
    ElMessage.success('上班打卡成功')
  } catch (error) {
    ElMessage.warning('打卡失败')
  }
}

const handleClockOut = async () => {
  try {
    await attendanceStore.clockOut(authStore.currentUser?.employeeId)
    ElMessage.success('下班打卡成功')
  } catch (error) {
    ElMessage.warning('打卡失败')
  }
}

onMounted(async () => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  if (authStore.currentUser?.employeeId) {
    await attendanceStore.fetchTodayRecord(authStore.currentUser.employeeId)
    await attendanceStore.fetchByEmployeeId(authStore.currentUser.employeeId)
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}

:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.clock-area {
  text-align: center;
  padding: 20px;
}

.current-time {
  margin-bottom: 30px;
}

.current-time .date {
  font-size: 18px;
  color: #606266;
  margin-bottom: 10px;
}

.current-time .time {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
}

.clock-status {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-bottom: 30px;
}

.status-item {
  text-align: center;
}

.status-item .label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.status-item .value {
  font-size: 20px;
  color: #909399;
}

.status-item .value.done {
  color: #67c23a;
  font-weight: bold;
}

.clock-buttons {
  margin-top: 20px;
}

.clock-buttons .el-button {
  padding: 15px 40px;
  font-size: 16px;
}
</style>
