<template>
  <div class="user-profile">
    <!-- 基本信息 -->
    <el-card class="mb-20">
      <template #header>
        <span><el-icon><User /></el-icon> 基本信息</span>
      </template>
      <div v-if="employeeInfo" class="profile-content">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="员工编号">
            <el-tag type="info">{{ employeeInfo.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="姓名">
            <span class="name">{{ employeeInfo.name }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="部门">
            <el-tag type="success">{{ employeeInfo.department }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="职位">{{ employeeInfo.position }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ employeeInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ employeeInfo.email }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">
            <el-icon><Calendar /></el-icon>
            {{ employeeInfo.hireDate }}
          </el-descriptions-item>
          <el-descriptions-item label="账号">{{ authStore.currentUser?.username }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleType(authStore.currentUser?.role)">
              {{ getRoleLabel(authStore.currentUser?.role) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else description="未找到员工信息" />
    </el-card>

    <!-- 快捷入口 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" @click="router.push('/user/fund')">
          <div class="quick-entry">
            <el-icon :size="40" color="#8E44AD"><Wallet /></el-icon>
            <span>公积金账户</span>
            <small>查看缴存明细</small>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="router.push('/user/salary')">
          <div class="quick-entry">
            <el-icon :size="40" color="#67C23A"><Money /></el-icon>
            <span>工资条</span>
            <small>查看工资明细</small>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="router.push('/user/performance')">
          <div class="quick-entry">
            <el-icon :size="40" color="#409EFF"><TrendCharts /></el-icon>
            <span>绩效考核</span>
            <small>查看绩效记录</small>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="router.push('/user/training')">
          <div class="quick-entry">
            <el-icon :size="40" color="#10B981"><Reading /></el-icon>
            <span>培训记录</span>
            <small>查看培训情况</small>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近考核记录 -->
    <el-card class="mt-20" v-if="recentPerformance">
      <template #header>
        <div class="card-header">
          <span><el-icon><TrendCharts /></el-icon> 最近考核</span>
          <el-button type="primary" text @click="router.push('/user/performance')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="performance-summary">
        <el-tag type="success" size="large">考核周期: {{ recentPerformance.period }}</el-tag>
        <el-tag :type="getLevelType(recentPerformance.level)" size="large">
          {{ recentPerformance.level }}级 - {{ recentPerformance.totalScore }}分
        </el-tag>
        <el-progress
          :percentage="recentPerformance.totalScore"
          :color="getScoreColor(recentPerformance.totalScore)"
          style="width: 300px"
        />
      </div>
      <div class="score-details">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="score-item">
              <span class="label">工作能力</span>
              <span class="value">{{ recentPerformance.workScore }}分</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="score-item">
              <span class="label">工作态度</span>
              <span class="value">{{ recentPerformance.attitudeScore }}分</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="score-item">
              <span class="label">团队协作</span>
              <span class="value">{{ recentPerformance.teamworkScore }}分</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="score-item">
              <span class="label">创新能力</span>
              <span class="value">{{ recentPerformance.innovationScore }}分</span>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="perf-comment" v-if="recentPerformance.comment">
        <el-icon><ChatDotRound /></el-icon>
        评语：{{ recentPerformance.comment }}
      </div>
    </el-card>

    <!-- 最近培训记录 -->
    <el-card class="mt-20" v-if="recentTraining">
      <template #header>
        <div class="card-header">
          <span><el-icon><Reading /></el-icon> 最近培训</span>
          <el-button type="primary" text @click="router.push('/user/training')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="training-summary">
        <h4>{{ recentTraining.title }}</h4>
        <div class="training-meta">
          <span><el-icon><User /></el-icon> {{ recentTraining.trainer }}</span>
          <span><el-icon><Calendar /></el-icon> {{ recentTraining.date }}</span>
          <span><el-icon><Location /></el-icon> {{ recentTraining.location }}</span>
        </div>
        <p class="desc">{{ recentTraining.description }}</p>
      </div>
    </el-card>

    <!-- 考勤统计 -->
    <el-card class="mt-20">
      <template #header>
        <span><el-icon><Clock /></el-icon> 本月考勤统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="attendance-stat">
            <span class="number">{{ attendanceStats.normal }}</span>
            <span class="label">正常</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="attendance-stat">
            <span class="number warning">{{ attendanceStats.late }}</span>
            <span class="label">迟到</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="attendance-stat">
            <span class="number danger">{{ attendanceStats.absent }}</span>
            <span class="label">缺勤</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="attendance-stat">
            <span class="number info">{{ attendanceStats.leave }}</span>
            <span class="label">请假</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useEmployeeStore } from '../../stores/employee'
import { usePerformanceStore } from '../../stores/performance'
import { useTrainingStore } from '../../stores/training'
import { useAttendanceStore } from '../../stores/attendance'
import {
  User, Calendar, Wallet, Money, TrendCharts, Reading,
  Clock, ChatDotRound, ArrowRight, Location
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const employeeStore = useEmployeeStore()
const performanceStore = usePerformanceStore()
const trainingStore = useTrainingStore()
const attendanceStore = useAttendanceStore()

const employeeInfo = ref(null)
const recentPerformance = ref(null)
const recentTraining = ref(null)
const attendanceStats = ref({
  normal: 0,
  late: 0,
  absent: 0,
  leave: 0
})

onMounted(async () => {
  if (authStore.currentUser?.employeeId) {
    const empId = authStore.currentUser.employeeId

    // 获取员工信息
    employeeInfo.value = await employeeStore.getEmployeeById(empId)

    // 获取绩效记录
    await performanceStore.fetchByEmployeeId(empId)
    if (performanceStore.performances.length > 0) {
      recentPerformance.value = performanceStore.performances[0]
    }

    // 获取培训记录
    await trainingStore.fetchTrainings()
    if (trainingStore.trainings.length > 0) {
      recentTraining.value = trainingStore.trainings[0]
    }

    // 获取考勤统计
    await attendanceStore.fetchByEmployeeId(empId)
    calculateAttendanceStats()
  }
})

const calculateAttendanceStats = () => {
  const records = attendanceStore.attendanceRecords
  const stats = { normal: 0, late: 0, absent: 0, leave: 0 }

  records.forEach(record => {
    if (record.clockIn && record.clockOut) {
      stats.normal++
    } else if (record.clockIn) {
      stats.late++
    } else {
      stats.absent++
    }
  })

  attendanceStats.value = stats
}

const getRoleType = (role) => {
  const map = { admin: 'danger', dept_admin: 'warning', user: 'info' }
  return map[role] || 'info'
}

const getRoleLabel = (role) => {
  const map = { admin: '超级管理员', dept_admin: '部门管理员', user: '普通员工' }
  return map[role] || '普通员工'
}

const getLevelType = (level) => {
  const map = { S: 'danger', A: 'success', B: 'warning', C: 'info' }
  return map[level] || 'info'
}

const getScoreColor = (score) => {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.name {
  font-weight: bold;
  color: #409eff;
  font-size: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.quick-entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  gap: 8px;
  cursor: pointer;
}

.quick-entry span {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.quick-entry small {
  font-size: 12px;
  color: #909399;
}

.performance-summary {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.score-details {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.score-item {
  text-align: center;
}

.score-item .label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 5px;
}

.score-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.perf-comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 15px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  color: #606266;
}

.training-summary h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.training-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.training-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.training-summary .desc {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.attendance-stat {
  text-align: center;
  padding: 20px;
}

.attendance-stat .number {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #67c23a;
}

.attendance-stat .number.warning {
  color: #e6a23c;
}

.attendance-stat .number.danger {
  color: #f56c6c;
}

.attendance-stat .number.info {
  color: #909399;
}

.attendance-stat .label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>
