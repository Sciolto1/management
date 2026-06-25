<template>
  <div class="department-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><OfficeBuilding /></el-icon> 部门管理</span>
          <div class="filter-area">
            <el-input v-model="searchKey" placeholder="搜索部门名称" clearable style="width: 180px; margin-right: 10px" />
            <el-button type="primary" @click="openDialog()">
              <el-icon><Plus /></el-icon> 添加部门
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="pagedDepartments" stripe style="width: 100%">
        <el-table-column prop="name" label="部门名称" />
        <el-table-column prop="manager" label="负责人">
          <template #default="{ row }">
            {{ row.manager || '未指定' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="部门描述" />
        <el-table-column prop="employeeCount" label="员工人数" width="100">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.employeeCount || 0 }} 人</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openDialog(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredDepartments.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '添加部门'" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-select v-model="formData.manager" placeholder="请选择负责人" clearable style="width: 100%">
            <el-option v-for="emp in deptEmployees" :key="emp.id" :label="emp.name" :value="emp.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useDepartmentStore } from '../../stores/department'
import { useEmployeeStore } from '../../stores/employee'

const departmentStore = useDepartmentStore()
const employeeStore = useEmployeeStore()
const currentDeptName = ref('')
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = 10

const filteredDepartments = computed(() => {
  let list = departmentStore.departments
  if (searchKey.value) {
    list = list.filter(d => d.name && d.name.includes(searchKey.value))
  }
  return list
})

const pagedDepartments = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredDepartments.value.slice(start, start + pageSize)
})

const deptEmployees = computed(() => {
  if (!currentDeptName.value) return []
  return employeeStore.employees.filter(e => e.department === currentDeptName.value)
})
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const formData = reactive({
  name: '',
  manager: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

onMounted(() => {
  departmentStore.fetchDepartments()
  employeeStore.fetchEmployees()
})

const openDialog = (row = null) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    currentDeptName.value = row.name
    Object.assign(formData, { name: row.name, manager: row.manager, description: row.description })
  } else {
    isEdit.value = false
    editId.value = null
    currentDeptName.value = ''
    Object.assign(formData, { name: '', manager: '', description: '' })
  }
  dialogVisible.value = true
}

// 新增部门时，部门名称变化时更新员工列表
watch(() => formData.name, (val) => {
  if (!isEdit.value) {
    currentDeptName.value = val
  }
})

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await departmentStore.updateDepartment({ id: editId.value, ...formData })
    ElMessage.success('编辑成功')
  } else {
    await departmentStore.addDepartment({ ...formData })
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
}

const handleDelete = (row) => {
  if (row.employeeCount > 0) {
    ElMessage.warning('该部门下还有员工，无法删除')
    return
  }
  ElMessageBox.confirm('确定要删除该部门吗？', '提示', { type: 'warning' })
    .then(async () => {
      await departmentStore.deleteDepartment(row.id)
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
