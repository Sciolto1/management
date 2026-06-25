<template>
  <div class="user-leave">
    <!-- 申请按钮 -->
    <el-card class="mb-20">
      <template #header>
        <div class="card-header">
          <span><el-icon><Document /></el-icon> 请假申请</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 申请请假
          </el-button>
        </div>
      </template>
      
      <!-- 我的申请记录 -->
      <el-table :data="leaveStore.leaveRequests" stripe>
        <el-table-column prop="type" label="请假类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="createTime" label="申请日期" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="leaveStore.leaveRequests.length === 0" description="暂无请假记录" />
    </el-card>

    <!-- 申请对话框 -->
    <el-dialog v-model="dialogVisible" title="申请请假" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="年假" value="annual" />
            <el-option label="病假" value="sick" />
            <el-option label="事假" value="personal" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input v-model="formData.reason" type="textarea" :rows="3" placeholder="请输入请假原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import { useEmployeeStore } from '../../stores/employee'
import { useLeaveStore } from '../../stores/leave'

const authStore = useAuthStore()
const employeeStore = useEmployeeStore()
const leaveStore = useLeaveStore()

const dialogVisible = ref(false)
const formRef = ref(null)
const employeeInfo = ref(null)

const formData = reactive({
  type: '',
  startDate: '',
  endDate: '',
  reason: ''
})

const rules = {
  type: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入请假原因', trigger: 'blur' }]
}

onMounted(async () => {
  if (authStore.currentUser?.employeeId) {
    await leaveStore.fetchByEmployeeId(authStore.currentUser.employeeId)
    employeeInfo.value = await employeeStore.getEmployeeById(authStore.currentUser.employeeId)
  }
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const getTypeLabel = (type) => {
  const map = { annual: '年假', sick: '病假', personal: '事假' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { annual: 'success', sick: 'warning', personal: 'info' }
  return map[type] || ''
}

const getStatusLabel = (status) => {
  const map = { pending: '待审批', approved: '已通过', rejected: '已拒绝' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || ''
}

const openDialog = () => {
  Object.assign(formData, { type: '', startDate: '', endDate: '', reason: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await leaveStore.addLeaveRequest({
    ...formData,
    employeeId: authStore.currentUser?.employeeId,
    employeeName: employeeInfo.value?.name || authStore.currentUser?.username
  })
  await leaveStore.fetchByEmployeeId(authStore.currentUser.employeeId)
  ElMessage.success('申请已提交')
  dialogVisible.value = false
}
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}

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
