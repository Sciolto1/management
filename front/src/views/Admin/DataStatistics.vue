<template>
  <div class="data-statistics">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409eff">
            <el-icon :size="28"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ employeeStore.employees.length }}</div>
            <div class="stat-label">员工总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon :size="28"><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ noticeStore.notices.length }}</div>
            <div class="stat-label">通知公告</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingLeaves.length }}</div>
            <div class="stat-label">待审批请假</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon :size="28"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ filteredAttendance.length }}</div>
            <div class="stat-label">考勤记录</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span><el-icon><PieChart /></el-icon> 部门人员分布</span>
          </template>
          <div ref="deptChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span><el-icon><TrendCharts /></el-icon> 请假类型统计</span>
          </template>
          <div ref="leaveChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span><el-icon><Histogram /></el-icon> 绩效等级分布</span>
          </template>
          <div ref="perfChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span><el-icon><DataLine /></el-icon> 考勤情况统计</span>
          </template>
          <div ref="attendChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { User, Bell, Document, Clock, PieChart, TrendCharts, Histogram, DataLine } from '@element-plus/icons-vue'
import { useEmployeeStore } from '../../stores/employee'
import { useNoticeStore } from '../../stores/notice'
import { useLeaveStore } from '../../stores/leave'
import { useAttendanceStore } from '../../stores/attendance'
import { usePerformanceStore } from '../../stores/performance'
import { useAuthStore } from '../../stores/auth'

const employeeStore = useEmployeeStore()
const noticeStore = useNoticeStore()
const leaveStore = useLeaveStore()
const attendanceStore = useAttendanceStore()
const performanceStore = usePerformanceStore()
const authStore = useAuthStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const deptEmployeeIds = computed(() => new Set(employeeStore.employees.map(e => e.id)))

const deptChartRef = ref(null)
const leaveChartRef = ref(null)
const perfChartRef = ref(null)
const attendChartRef = ref(null)

const filteredLeaves = computed(() => {
  if (!isDeptAdmin.value) return leaveStore.leaveRequests
  return leaveStore.leaveRequests.filter(l => deptEmployeeIds.value.has(l.employeeId))
})
const filteredAttendance = computed(() => {
  if (!isDeptAdmin.value) return attendanceStore.allRecords
  return attendanceStore.allRecords.filter(a => deptEmployeeIds.value.has(a.employeeId))
})
const filteredPerformances = computed(() => {
  if (!isDeptAdmin.value) return performanceStore.performances
  return performanceStore.performances.filter(p => deptEmployeeIds.value.has(p.employeeId))
})
const pendingLeaves = computed(() => filteredLeaves.value.filter(l => l.status === 'pending'))

// 部门统计
const deptData = computed(() => {
  const deptMap = {}
  employeeStore.employees.forEach(emp => {
    deptMap[emp.department] = (deptMap[emp.department] || 0) + 1
  })
  return Object.entries(deptMap).map(([name, value]) => ({ name, value }))
})

// 请假类型统计
const leaveData = computed(() => {
  const typeMap = { annual: 0, sick: 0, personal: 0 }
  filteredLeaves.value.forEach(l => {
    if (typeMap[l.type] !== undefined) typeMap[l.type]++
  })
  return [
    { name: '年假', value: typeMap.annual },
    { name: '病假', value: typeMap.sick },
    { name: '事假', value: typeMap.personal }
  ]
})

// 绩效等级统计
const perfData = computed(() => {
  const levelMap = { S: 0, A: 0, B: 0, C: 0 }
  filteredPerformances.value.forEach(p => {
    if (levelMap[p.level] !== undefined) levelMap[p.level]++
  })
  return Object.entries(levelMap).map(([level, count]) => ({ level, count }))
})

// 考勤统计（按员工统计考勤天数）
const attendData = computed(() => {
  const empMap = {}
  filteredAttendance.value.forEach(a => {
    const name = a.employeeName || ('员工' + a.employeeId)
    empMap[name] = (empMap[name] || 0) + 1
  })
  return Object.entries(empMap).map(([name, count]) => ({ name, count }))
})

let deptChart, leaveChart, perfChart, attendChart

const initCharts = () => {
  // 部门饼图
  deptChart = echarts.init(deptChartRef.value)
  deptChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: deptData.value,
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
    }]
  })

  // 请假类型饼图
  leaveChart = echarts.init(leaveChartRef.value)
  leaveChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#67c23a', '#e6a23c', '#909399'],
    series: [{
      type: 'pie',
      radius: '60%',
      data: leaveData.value
    }]
  })

  // 绩效柱状图
  perfChart = echarts.init(perfChartRef.value)
  perfChart.setOption({
    tooltip: {},
    xAxis: { type: 'category', data: perfData.value.map(p => p.level + '级') },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: perfData.value.map(p => p.count),
      itemStyle: {
        color: (params) => {
          const colors = ['#f56c6c', '#67c23a', '#e6a23c', '#909399']
          return colors[params.dataIndex]
        }
      }
    }]
  })

  // 考勤情况柱状图
  attendChart = echarts.init(attendChartRef.value)
  attendChart.setOption({
    tooltip: {},
    xAxis: { type: 'category', data: attendData.value.map(a => a.name), axisLabel: { rotate: 15 } },
    yAxis: { type: 'value', minInterval: 1, name: '考勤天数' },
    series: [{
      type: 'bar',
      data: attendData.value.map(a => a.count),
      itemStyle: { color: '#409eff' }
    }]
  })

  window.addEventListener('resize', () => {
    deptChart.resize()
    leaveChart.resize()
    perfChart.resize()
    attendChart.resize()
  })
}

onMounted(async () => {
  await Promise.all([
    employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined),
    noticeStore.fetchNotices(),
    leaveStore.fetchLeaveRequests(),
    attendanceStore.fetchAllRecords(),
    performanceStore.fetchPerformances()
  ])
  await nextTick()
  initCharts()
})

// 监听数据变化更新图表
watch([deptData, leaveData, perfData, attendData], () => {
  if (deptChart) {
    deptChart.setOption({ series: [{ data: deptData.value }] })
    leaveChart.setOption({ series: [{ data: leaveData.value }] })
    perfChart.setOption({ 
      xAxis: { data: perfData.value.map(p => p.level + '级') },
      series: [{ data: perfData.value.map(p => p.count) }] 
    })
    attendChart.setOption({
      xAxis: { data: attendData.value.map(a => a.name) },
      series: [{ data: attendData.value.map(a => a.count) }]
    })
  }
}, { deep: true })
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart {
  height: 280px;
}

:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
</style>
