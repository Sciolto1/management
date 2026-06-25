<template>
  <div class="user-notice">
    <el-card>
      <template #header>
        <span><el-icon><Bell /></el-icon> 公司通知</span>
      </template>
      <div class="notice-list">
        <div 
          v-for="notice in sortedNotices" 
          :key="notice.id" 
          class="notice-item"
          @click="showDetail(notice)"
        >
          <div class="notice-title">
            <el-icon><Document /></el-icon>
            {{ notice.title }}
          </div>
          <div class="notice-meta">
            <span><el-icon><User /></el-icon> {{ notice.author }}</span>
            <span><el-icon><Clock /></el-icon> {{ notice.publishDate }}</span>
          </div>
        </div>
        <el-empty v-if="sortedNotices.length === 0" description="暂无通知" />
      </div>
    </el-card>

    <!-- 通知详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="currentNotice?.title" width="600px">
      <div class="notice-detail">
        <div class="meta">
          <span>发布人：{{ currentNotice?.author }}</span>
          <span>发布日期：{{ currentNotice?.publishDate }}</span>
        </div>
        <el-divider />
        <div class="content">{{ currentNotice?.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useNoticeStore } from '../../stores/notice'
import { Bell, Document, User, Clock } from '@element-plus/icons-vue'

const noticeStore = useNoticeStore()
const dialogVisible = ref(false)
const currentNotice = ref(null)

const sortedNotices = computed(() => {
  return [...noticeStore.notices].sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))
})

onMounted(() => {
  noticeStore.fetchNotices()
})

const showDetail = (notice) => {
  currentNotice.value = notice
  dialogVisible.value = true
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

.notice-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.3s;
}

.notice-item:hover {
  background: #f5f7fa;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.notice-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.notice-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-detail .meta {
  display: flex;
  gap: 30px;
  color: #909399;
  font-size: 14px;
}

.notice-detail .content {
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}
</style>
