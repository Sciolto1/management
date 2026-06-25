<template>
  <div class="salary-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Money /></el-icon> 工资管理</span>
          <div class="filter-area">
            <el-input v-model="searchName" placeholder="搜索员工" clearable style="width: 140px; margin-right: 10px" />
            <el-select v-model="searchStatus" placeholder="发放状态" clearable style="width: 120px; margin-right: 10px">
              <el-option label="待发放" value="pending" />
              <el-option label="已发放" value="paid" />
            </el-select>
            <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 添加工资记录</el-button>
          </div>
        </div>
      </template>
      <el-table :data="pagedSalaries" stripe>
        <el-table-column prop="employeeName" label="员工" width="100" />
        <el-table-column prop="month" label="月份" width="100" />
        <el-table-column prop="baseSalary" label="基本工资" width="100">
          <template #default="{ row }">¥{{ row.baseSalary }}</template>
        </el-table-column>
        <el-table-column prop="bonus" label="奖金" width="100">
          <template #default="{ row }">
            <span style="color: #67c23a">+¥{{ row.bonus }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deduction" label="扣款" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">-¥{{ row.deduction }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualSalary" label="实发工资" width="120">
          <template #default="{ row }">
            <span style="font-weight: bold; color: #409eff">¥{{ row.actualSalary }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'paid' ? 'success' : 'warning'">
              {{ row.status === 'paid' ? '已发放' : '待发放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="success" text size="small" @click="handlePay(row)">
              <el-icon><Check /></el-icon> 发放
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredSalaries.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加工资记录" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="formData.employeeId" placeholder="选择员工" style="width: 100%" @change="onEmployeeChange">
            <el-option v-for="emp in selectableEmployees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-date-picker v-model="formData.month" type="month" value-format="YYYY-MM" placeholder="选择月份" style="width: 100%" />
        </el-form-item>
        <el-form-item label="基本工资" prop="baseSalary">
          <el-input-number v-model="formData.baseSalary" :min="0" :step="500" style="width: 100%" />
        </el-form-item>
        <el-form-item label="奖金" prop="bonus">
          <el-input-number v-model="formData.bonus" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="扣款" prop="deduction">
          <el-input-number v-model="formData.deduction" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="pending">待发放</el-radio>
            <el-radio value="paid">已发放</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" placeholder="备注信息" />
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
import { Money, Plus, Delete, Check } from '@element-plus/icons-vue'
import { useSalaryStore } from '../../stores/salary'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'

const salaryStore = useSalaryStore()
const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const currentEmployeeId = computed(() => authStore.currentUser?.employeeId)
const deptEmployeeIds = computed(() => new Set(employeeStore.employees.map(e => e.id)))

const searchName = ref('')
const searchStatus = ref('')
const currentPage = ref(1)
const pageSize = 10

// dept_admin不能管理自己的工资
const selectableEmployees = computed(() => {
  if (!isDeptAdmin.value) return employeeStore.employees
  return employeeStore.employees.filter(e => e.id !== currentEmployeeId.value)
})

const filteredSalaries = computed(() => {
  let records = salaryStore.salaryRecords
  if (isDeptAdmin.value) {
    records = records.filter(s => deptEmployeeIds.value.has(s.employeeId) && s.employeeId !== currentEmployeeId.value)
  }
  if (searchName.value) {
    records = records.filter(s => s.employeeName && s.employeeName.includes(searchName.value))
  }
  if (searchStatus.value) {
    records = records.filter(s => s.status === searchStatus.value)
  }
  return records
})

const pagedSalaries = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredSalaries.value.slice(start, start + pageSize)
})

const dialogVisible = ref(false)
const formRef = ref(null)

const formData = reactive({
  employeeId: '',
  employeeName: '',
  month: '',
  baseSalary: 5000,
  bonus: 0,
  deduction: 0,
  status: 'pending',
  remark: ''
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }]
}

onMounted(async () => {
  await employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined)
  salaryStore.fetchSalaries()
})

const onEmployeeChange = (id) => {
  const emp = employeeStore.employees.find(e => e.id === id)
  if (emp) formData.employeeName = emp.name
}

const openDialog = () => {
  Object.assign(formData, { employeeId: '', employeeName: '', month: '', baseSalary: 5000, bonus: 0, deduction: 0, status: 'pending', remark: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await salaryStore.addSalary({ ...formData })
  ElMessage.success('添加成功')
  dialogVisible.value = false
}

const handlePay = (row) => {
  ElMessageBox.confirm('确定将该工资标记为已发放吗？', '确认发放', { type: 'info' })
    .then(async () => {
      await salaryStore.updateStatus(row.id, 'paid')
      ElMessage.success('已发放')
    }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该工资记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await salaryStore.deleteSalary(row.id)
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
  gap: 0;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
