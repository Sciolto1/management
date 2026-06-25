<template>
  <div class="page-container">
    <div class="page-header">
      <h2>消息中心</h2>
      <p>查看和管理您的消息</p>
    </div>
    
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="searchText" placeholder="搜索消息内容" clearable style="width: 280px" prefix-icon="Search" />
        <el-select v-model="filterType" placeholder="消息类型" clearable style="width: 140px; margin-left: 12px">
          <el-option v-for="t in messageTypes" :key="t" :label="t" :value="t" />
        </el-select>
      </div>
      <el-button type="success" @click="markAllRead" :disabled="store.unreadMessages === 0">
        全部已读
      </el-button>
    </div>

    <div class="message-list">
      <div 
        v-for="msg in filteredList" 
        :key="msg.id" 
        class="message-item"
        :class="{ unread: !msg.isRead }"
        @click="handleRead(msg)"
      >
        <div class="message-avatar">
          <span v-if="msg.type === '系统通知'">🔔</span>
          <span v-else-if="msg.type === '交易消息'">💰</span>
          <span v-else>💬</span>
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="sender">{{ msg.sender }}</span>
            <el-tag :type="typeTag[msg.type]" size="small">{{ msg.type }}</el-tag>
            <span class="time">{{ msg.createTime }}</span>
          </div>
          <div class="message-body">{{ msg.content }}</div>
        </div>
        <div class="message-actions">
          <el-button v-if="!msg.isRead" size="small" type="primary" link @click.stop="handleRead(msg)">
            标为已读
          </el-button>
          <el-button size="small" type="danger" link @click.stop="handleDelete(msg.id)">
            删除
          </el-button>
        </div>
      </div>
      
      <el-empty v-if="filteredList.length === 0" description="暂无消息" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSecondhandStore } from '@/stores/secondhand'

const store = useSecondhandStore()

const messageTypes = ['系统通知', '交易消息', '咨询消息']
const typeTag = { '系统通知': 'info', '交易消息': 'success', '咨询消息': 'warning' }

const searchText = ref('')
const filterType = ref('')

const filteredList = computed(() => {
  return store.messages.filter(m => {
    const matchSearch = m.content.includes(searchText.value) || m.sender.includes(searchText.value)
    const matchType = !filterType.value || m.type === filterType.value
    return matchSearch && matchType
  })
})

const handleRead = (msg) => {
  if (!msg.isRead) {
    msg.isRead = true
    ElMessage.success('已标记为已读')
  }
}

const markAllRead = () => {
  store.messages.forEach(m => { m.isRead = true })
  ElMessage.success('已全部标记为已读')
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该消息？', '提示', { type: 'warning' }).then(() => {
    const index = store.messages.findIndex(m => m.id === id)
    if (index > -1) store.messages.splice(index, 1)
    ElMessage.success('删除成功')
  })
}
</script>

<style scoped>
.page-container {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  min-height: calc(100vh - 120px);
}

.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #333; margin: 0 0 8px 0; }
.page-header p { color: #999; margin: 0; font-size: 14px; }

.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.toolbar-left { display: flex; align-items: center; }

.message-list { display: flex; flex-direction: column; gap: 12px; }

.message-item {
  display: flex; align-items: flex-start; padding: 16px;
  border: 1px solid #f0f0f0; border-radius: 10px;
  cursor: pointer; transition: all 0.3s; background: #fff;
}
.message-item:hover { border-color: #52c41a; box-shadow: 0 2px 8px rgba(82, 196, 26, 0.1); }
.message-item.unread { background: linear-gradient(90deg, rgba(82, 196, 26, 0.05) 0%, #fff 100%); border-left: 3px solid #52c41a; }

.message-avatar {
  width: 44px; height: 44px; border-radius: 10px; background: #f5f5f5;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; margin-right: 14px; flex-shrink: 0;
}

.message-content { flex: 1; min-width: 0; }
.message-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.sender { font-weight: 600; color: #333; }
.time { color: #999; font-size: 12px; margin-left: auto; }
.message-body { color: #666; font-size: 14px; line-height: 1.5; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.message-actions { display: flex; flex-direction: column; gap: 4px; margin-left: 12px; }

:deep(.el-button--primary) { background: #52c41a; border-color: #52c41a; }
:deep(.el-button--success) { background: #52c41a; border-color: #52c41a; }
</style>
