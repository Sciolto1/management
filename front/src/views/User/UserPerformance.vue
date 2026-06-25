<template>
  <div class="user-performance">
    <el-card>
      <template #header>
        <span><el-icon><TrendCharts /></el-icon> 我的绩效</span>
      </template>
      <div v-if="performanceStore.performances.length > 0">
        <div v-for="perf in performanceStore.performances" :key="perf.id" class="performance-item">
          <div class="perf-header">
            <span class="period">{{ perf.period }}</span>
            <el-tag :type="getLevelType(perf.level)" size="large">
              {{ perf.level }}级 - {{ perf.totalScore }}分
            </el-tag>
          </div>
          <div class="scores">
            <div class="score-item">
              <span class="label">工作能力</span>
              <el-progress :percentage="perf.workScore" :color="getColor(perf.workScore)" />
            </div>
            <div class="score-item">
              <span class="label">工作态度</span>
              <el-progress :percentage="perf.attitudeScore" :color="getColor(perf.attitudeScore)" />
            </div>
            <div class="score-item">
              <span class="label">团队协作</span>
              <el-progress :percentage="perf.teamworkScore" :color="getColor(perf.teamworkScore)" />
            </div>
            <div class="score-item">
              <span class="label">创新能力</span>
              <el-progress :percentage="perf.innovationScore" :color="getColor(perf.innovationScore)" />
            </div>
          </div>
          <div class="comment" v-if="perf.comment">
            <el-icon><ChatDotRound /></el-icon>
            <span>评语：{{ perf.comment }}</span>
          </div>
          <div class="evaluator">
            评估人：{{ perf.evaluator }} | 评估日期：{{ perf.createTime?.replace('T', ' ')?.substring(0, 16) }}
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无绩效记录" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { TrendCharts, ChatDotRound } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { usePerformanceStore } from '../../stores/performance'

const authStore = useAuthStore()
const performanceStore = usePerformanceStore()

onMounted(() => {
  if (authStore.currentUser?.employeeId) {
    performanceStore.fetchByEmployeeId(authStore.currentUser.employeeId)
  }
})

const getLevelType = (level) => {
  const map = { S: 'danger', A: 'success', B: 'warning', C: 'info' }
  return map[level] || 'info'
}

const getColor = (score) => {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}
</script>

<style scoped>
:deep(.el-card__header) span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.performance-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.performance-item:last-child {
  margin-bottom: 0;
}

.perf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.perf-header .period {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.scores {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-item .label {
  width: 70px;
  font-size: 14px;
  color: #606266;
}

.score-item .el-progress {
  flex: 1;
}

.comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 10px;
  color: #606266;
}

.evaluator {
  font-size: 13px;
  color: #909399;
  text-align: right;
}
</style>
