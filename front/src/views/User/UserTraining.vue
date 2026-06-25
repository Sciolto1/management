<template>
  <div class="user-training">
    <el-card>
      <template #header>
        <span><el-icon><Reading /></el-icon> 培训报名</span>
      </template>
      <div class="training-list">
        <div v-for="training in sortedTrainings" :key="training.id" class="training-item">
          <div class="training-info">
            <h3>{{ training.title }}</h3>
            <p class="desc">{{ training.description }}</p>
            <div class="meta">
              <span><el-icon><User /></el-icon> {{ training.trainer }}</span>
              <span><el-icon><Calendar /></el-icon> {{ training.date }} {{ training.time }}</span>
              <span><el-icon><Location /></el-icon> {{ training.location }}</span>
            </div>
            <div class="participants">
              <el-tag :type="(training.participants?.length || 0) >= training.maxParticipants ? 'danger' : 'success'">
                已报名 {{ training.participants?.length || 0 }}/{{ training.maxParticipants }} 人
              </el-tag>
            </div>
          </div>
          <div class="training-action">
            <el-button 
              v-if="!isJoined(training)" 
              type="primary" 
              :disabled="(training.participants?.length || 0) >= training.maxParticipants"
              @click="handleJoin(training)"
            >
              <el-icon><Plus /></el-icon> 报名参加
            </el-button>
            <el-button v-else type="danger" @click="handleCancel(training)">
              <el-icon><Close /></el-icon> 取消报名
            </el-button>
          </div>
        </div>
        <el-empty v-if="sortedTrainings.length === 0" description="暂无培训" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading, User, Calendar, Location, Plus, Close } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useTrainingStore } from '../../stores/training'

const authStore = useAuthStore()
const trainingStore = useTrainingStore()

const sortedTrainings = computed(() => {
  return [...trainingStore.trainings].sort((a, b) => new Date(b.date) - new Date(a.date))
})

onMounted(() => {
  trainingStore.fetchTrainings()
})

const isJoined = (training) => {
  return training.participants?.includes(authStore.currentUser?.employeeId)
}

const handleJoin = async (training) => {
  try {
    await trainingStore.enrollTraining(training.id, authStore.currentUser?.employeeId)
    ElMessage.success('报名成功')
  } catch (error) {
    ElMessage.warning('报名失败')
  }
}

const handleCancel = async (training) => {
  try {
    await trainingStore.cancelEnroll(training.id, authStore.currentUser?.employeeId)
    ElMessage.success('已取消报名')
  } catch (error) {
    ElMessage.warning('取消失败')
  }
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

.training-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.training-item:last-child {
  border-bottom: none;
}

.training-info h3 {
  margin: 0 0 8px 0;
  color: #303133;
}

.training-info .desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.training-info .meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.training-info .meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.training-action {
  flex-shrink: 0;
}
</style>
