<template>
  <div class="employee-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><User /></el-icon> 员工管理</span>
          <div class="filter-area">
            <el-input v-model="searchKey" placeholder="搜索姓名/部门/职位" clearable style="width: 200px; margin-right: 10px" />
            <el-button type="primary" @click="openDialog()">
              <el-icon><Plus /></el-icon> 添加员工
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="pagedEmployees" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="hireDate" label="入职日期" width="120" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="role" label="角色" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'admin'" type="danger">超级管理员</el-tag>
            <el-tag v-else-if="row.role === 'dept_admin'" type="warning">部门管理员</el-tag>
            <el-tag v-else type="info">普通员工</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="isDeptAdmin && row.id === currentEmployeeId">
              <el-tag type="info" size="small">本人</el-tag>
            </template>
            <template v-else>
              <el-button type="primary" text size="small" @click="openDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredEmployees.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '添加员工'" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="formData.department" placeholder="请选择部门" style="width: 100%" :disabled="isDeptAdmin">
            <el-option v-for="dept in departmentStore.departments" :key="dept.id" :label="dept.name" :value="dept.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="formData.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="入职日期" prop="hireDate">
          <el-date-picker v-model="formData.hireDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-divider>账号信息</el-divider>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入登录用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" placeholder="请输入登录密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" value="admin" />
            <el-option label="部门管理员" value="dept_admin" />
            <el-option label="普通员工" value="user" />
          </el-select>
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
import { User, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'
import { useDepartmentStore } from '../../stores/department'

const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const departmentStore = useDepartmentStore()
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)
const currentEmployeeId = computed(() => authStore.currentUser?.employeeId)
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = 10

const filteredEmployees = computed(() => {
  let list = employeeStore.employees
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    list = list.filter(e => (e.name && e.name.toLowerCase().includes(key)) || (e.department && e.department.toLowerCase().includes(key)) || (e.position && e.position.toLowerCase().includes(key)))
  }
  return list
})

const pagedEmployees = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredEmployees.value.slice(start, start + pageSize)
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const formData = reactive({
  name: '',
  department: '',
  position: '',
  phone: '',
  email: '',
  hireDate: '',
  username: '',
  password: '',
  role: 'user'
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(() => {
  employeeStore.fetchEmployees(isDeptAdmin.value ? userDepartment.value : undefined)
  departmentStore.fetchDepartments()
})

const openDialog = (row = null) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    editId.value = null
    Object.assign(formData, { name: '', department: isDeptAdmin.value ? userDepartment.value : '', position: '', phone: '', email: '', hireDate: '', username: '', password: '', role: 'user' })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await employeeStore.updateEmployee({ id: editId.value, ...formData })
    ElMessage.success('编辑成功')
  } else {
    await employeeStore.addEmployee({ ...formData })
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该员工吗？', '提示', { type: 'warning' })
    .then(async () => {
      await employeeStore.deleteEmployee(row.id)
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
