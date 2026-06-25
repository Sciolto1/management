<template>
  <div class="training-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Reading /></el-icon> 培训管理</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 发布培训
          </el-button>
        </div>
      </template>
      <el-table :data="sortedTrainings" stripe>
        <el-table-column prop="title" label="培训主题" width="200" />
        <el-table-column prop="trainer" label="讲师" width="140" />
        <el-table-column prop="date" label="日期" width="110" />
        <el-table-column prop="time" label="时间" width="120" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column label="报名情况" width="100">
          <template #default="{ row }">
            <el-tag :type="row.participants.length >= row.maxParticipants ? 'danger' : 'success'">
              {{ row.participants.length }}/{{ row.maxParticipants }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布培训" width="550px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="培训主题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入培训主题" />
        </el-form-item>
        <el-form-item label="培训描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入培训描述" />
        </el-form-item>
        <el-form-item label="讲师" prop="trainer">
          <el-input v-model="formData.trainer" placeholder="请输入讲师信息" />
        </el-form-item>
        <el-form-item label="培训日期" prop="date">
          <el-date-picker v-model="formData.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="培训时间" prop="time">
          <el-input v-model="formData.time" placeholder="如：14:00-17:00" />
        </el-form-item>
        <el-form-item label="培训地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入培训地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="formData.maxParticipants" :min="1" :max="100" />
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
import { Reading, Plus, Delete } from '@element-plus/icons-vue'
import { useTrainingStore } from '../../stores/training'

const trainingStore = useTrainingStore()
const dialogVisible = ref(false)
const formRef = ref(null)

const sortedTrainings = computed(() => {
  return [...trainingStore.trainings].sort((a, b) => new Date(b.date) - new Date(a.date))
})

const formData = reactive({
  title: '',
  description: '',
  trainer: '',
  date: '',
  time: '',
  location: '',
  maxParticipants: 20
})

const rules = {
  title: [{ required: true, message: '请输入培训主题', trigger: 'blur' }],
  trainer: [{ required: true, message: '请输入讲师', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  time: [{ required: true, message: '请输入时间', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }]
}

onMounted(() => {
  trainingStore.fetchTrainings()
})

const openDialog = () => {
  Object.assign(formData, { title: '', description: '', trainer: '', date: '', time: '', location: '', maxParticipants: 20 })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await trainingStore.addTraining({ ...formData })
  ElMessage.success('发布成功')
  dialogVisible.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该培训吗？', '提示', { type: 'warning' })
    .then(async () => {
      await trainingStore.deleteTraining(row.id)
      ElMessage.success('删除成功')
    }).catch(() => {})
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
