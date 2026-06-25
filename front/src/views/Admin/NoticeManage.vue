<template>
  <div class="notice-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Bell /></el-icon> 通知管理</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 发布通知
          </el-button>
        </div>
      </template>
      <el-table :data="sortedNotices" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="author" label="发布人" width="120" />
        <el-table-column prop="publishDate" label="发布日期" width="120" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布通知对话框 -->
    <el-dialog v-model="dialogVisible" title="发布通知" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="5" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="发布人" prop="author">
          <el-input v-model="formData.author" placeholder="请输入发布人/部门" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Plus, Delete } from '@element-plus/icons-vue'
import { useNoticeStore } from '../../stores/notice'

const noticeStore = useNoticeStore()
const dialogVisible = ref(false)
const formRef = ref(null)

const formData = reactive({
  title: '',
  content: '',
  author: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  author: [{ required: true, message: '请输入发布人', trigger: 'blur' }]
}

const sortedNotices = computed(() => {
  return [...noticeStore.notices].sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))
})

onMounted(() => {
  noticeStore.fetchNotices()
})

const openDialog = () => {
  Object.assign(formData, { title: '', content: '', author: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await noticeStore.addNotice({ ...formData })
  ElMessage.success('发布成功')
  dialogVisible.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该通知吗？', '提示', { type: 'warning' })
    .then(async () => {
      await noticeStore.deleteNotice(row.id)
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}
</script>

<style scoped>
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
</style>
