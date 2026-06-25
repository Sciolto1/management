<template>
  <div class="performance-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><TrendCharts /></el-icon> 绩效考核</span>
          <div class="filter-area">
            <el-input v-model="searchName" placeholder="搜索员工" clearable style="width: 160px; margin-right: 10px" />
            <el-button type="primary" @click="openDialog">
              <el-icon><Plus /></el-icon> 添加考核
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="pagedPerformances" stripe>
        <el-table-column prop="employeeName" label="员工" width="100" />
        <el-table-column prop="period" label="考核周期" width="100" />
        <el-table-column prop="workScore" label="工作能力" width="90" />
        <el-table-column prop="attitudeScore" label="工作态度" width="90" />
        <el-table-column prop="teamworkScore" label="团队协作" width="90" />
        <el-table-column prop="innovationScore" label="创新能力" width="90" />
        <el-table-column prop="totalScore" label="综合得分" width="90">
          <template #default="{ row }">
            <span style="font-weight: bold">{{ row.totalScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredPerformances.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加绩效考核" width="550px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="formData.employeeId" placeholder="选择员工" style="width: 100%" @change="onEmployeeChange">
            <el-option v-for="emp in selectableEmployees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核周期" prop="period">
          <el-input v-model="formData.period" placeholder="如：2026-Q1" />
        </el-form-item>
        <el-form-item label="工作能力" prop="workScore">
          <el-slider v-model="formData.workScore" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="工作态度" prop="attitudeScore">
          <el-slider v-model="formData.attitudeScore" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="团队协作" prop="teamworkScore">
          <el-slider v-model="formData.teamworkScore" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="创新能力" prop="innovationScore">
          <el-slider v-model="formData.innovationScore" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="formData.comment" type="textarea" :rows="2" placeholder="请输入评语" />
        </el-form-item>
        <el-form-item label="评估人">
          <el-input v-model="formData.evaluator" placeholder="请输入评估人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { TrendCharts, Plus, Delete } from '@element-plus/icons-vue'
import { usePerformanceStore } from '../../stores/performance'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'

const performanceStore = usePerformanceStore()
const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const currentEmployeeId = computed(() => authStore.currentUser?.employeeId)
const deptEmployeeIds = computed(() => new Set(employeeStore.employees.map(e => e.id)))

const searchName = ref('')
const currentPage = ref(1)
const pageSize = 10

// dept_admin不能管理自己
const selectableEmployees = computed(() => {
  if (!isDeptAdmin.value) return employeeStore.employees
  return employeeStore.employees.filter(e => e.id !== currentEmployeeId.value)
})

const filteredPerformances = computed(() => {
  let list = performanceStore.performances
  if (isDeptAdmin.value) {
    list = list.filter(p => deptEmployeeIds.value.has(p.employeeId) && p.employeeId !== currentEmployeeId.value)
  }
  if (searchName.value) {
    list = list.filter(p => p.employeeName && p.employeeName.includes(searchName.value))
  }
  return list
})

const pagedPerformances = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredPerformances.value.slice(start, start + pageSize)
})

const dialogVisible = ref(false)
const formRef = ref(null)

const formData = reactive({
  employeeId: '',
  employeeName: '',
  period: '',
  workScore: 80,
  attitudeScore: 80,
  teamworkScore: 80,
  innovationScore: 80,
  comment: '',
  evaluator: ''
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  period: [{ required: true, message: '请输入考核周期', trigger: 'blur' }]
}

onMounted(async () => {
  await employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined)
  performanceStore.fetchPerformances()
})

const getLevelType = (level) => {
  const map = { S: 'danger', A: 'success', B: 'warning', C: 'info' }
  return map[level] || 'info'
}

const onEmployeeChange = (id) => {
  const emp = employeeStore.employees.find(e => e.id === id)
  if (emp) formData.employeeName = emp.name
}

const openDialog = () => {
  Object.assign(formData, { employeeId: '', employeeName: '', period: '', workScore: 80, attitudeScore: 80, teamworkScore: 80, innovationScore: 80, comment: '', evaluator: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await performanceStore.addPerformance({ ...formData })
  ElMessage.success('添加成功')
  dialogVisible.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该考核记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await performanceStore.deletePerformance(row.id)
      ElMessage.success('删除成功')
    }).catch(() => {})
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
.filter-area {
  display: flex;
  align-items: center;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
