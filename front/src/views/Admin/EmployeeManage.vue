<template>
  <div class="employee-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><User /></el-icon> 员工管理</span>
          <div class="filter-area">
            <el-input v-model="searchForm.keyword" placeholder="搜索姓名/工号/电话" clearable style="width: 200px; margin-right: 10px" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.department" placeholder="选择部门" clearable style="width: 140px; margin-right: 10px" @change="handleSearch">
              <el-option v-for="dept in departmentStore.departments" :key="dept.id" :label="dept.name" :value="dept.name" />
            </el-select>
            <el-select v-model="searchForm.status" placeholder="员工状态" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
              <el-option label="在职" value="active" />
              <el-option label="离职" value="left" />
              <el-option label="退休" value="retired" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon> 查询
            </el-button>
            <el-button type="success" @click="openDialog()" style="margin-left: 10px">
              <el-icon><Plus /></el-icon> 添加员工
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="employeePage.records" stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="employeeNo" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="department" label="部门" width="110" />
        <el-table-column prop="position" label="职位" width="110" />
        <el-table-column prop="employeeCategory" label="类别" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.employeeCategory === 'cadre'" type="warning">干部</el-tag>
            <el-tag v-else-if="row.employeeCategory === 'worker'" type="info">工人</el-tag>
            <el-tag v-else-if="row.employeeCategory === 'temp'" type="danger">临时工</el-tag>
            <el-tag v-else>未分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="entryDate" label="入职日期" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'active'" type="success">在职</el-tag>
            <el-tag v-else-if="row.status === 'left'" type="info">离职</el-tag>
            <el-tag v-else-if="row.status === 'retired'" type="warning">退休</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewEmployee(row)">详情</el-button>
            <el-button type="success" text size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="employeePage.total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="employeePage.size"
          v-model:current-page="employeePage.current"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" top="5vh">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="工号" prop="employeeNo">
                  <el-input v-model="formData.employeeNo" placeholder="请输入工号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="formData.name" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="formData.gender">
                    <el-radio value="男">男</el-radio>
                    <el-radio value="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker v-model="formData.birthDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="民族" prop="ethnicity">
                  <el-input v-model="formData.ethnicity" placeholder="请输入民族" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="政治面貌" prop="politicalStatus">
                  <el-select v-model="formData.politicalStatus" style="width: 100%">
                    <el-option label="群众" value="群众" />
                    <el-option label="共青团员" value="共青团员" />
                    <el-option label="中共党员" value="中共党员" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="婚否" prop="maritalStatus">
                  <el-radio-group v-model="formData.maritalStatus">
                    <el-radio value="未婚">未婚</el-radio>
                    <el-radio value="已婚">已婚</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门" prop="department">
                  <el-select v-model="formData.department" style="width: 100%" :disabled="isDeptAdmin">
                    <el-option v-for="dept in departmentStore.departments" :key="dept.id" :label="dept.name" :value="dept.name" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位" prop="position">
                  <el-input v-model="formData.position" placeholder="请输入职位" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="班组" prop="teamGroup">
                  <el-input v-model="formData.teamGroup" placeholder="请输入班组" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="人员类别" prop="employeeCategory">
                  <el-select v-model="formData.employeeCategory" style="width: 100%">
                    <el-option label="干部" value="cadre" />
                    <el-option label="工人" value="worker" />
                    <el-option label="临时工" value="temp" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="电话" prop="phone">
                  <el-input v-model="formData.phone" placeholder="请输入电话" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="formData.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="家庭住址" prop="homeAddress">
                  <el-input v-model="formData.homeAddress" placeholder="请输入家庭住址" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="教育经历" name="education">
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-input v-model="formData.title" placeholder="请输入职称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业" prop="major">
                  <el-input v-model="formData.major" placeholder="请输入专业" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学历" prop="education">
                  <el-select v-model="formData.education" style="width: 100%">
                    <el-option label="高中" value="高中" />
                    <el-option label="大专" value="大专" />
                    <el-option label="本科" value="本科" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="博士" value="博士" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="毕业学校" prop="graduationSchool">
                  <el-input v-model="formData.graduationSchool" placeholder="请输入毕业学校" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="毕业时间" prop="graduationDate">
                  <el-date-picker v-model="formData.graduationDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="工作信息" name="work">
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="入厂时间" prop="entryDate">
                  <el-date-picker v-model="formData.entryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="参加工作时间" prop="workStartDate">
                  <el-date-picker v-model="formData.workStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="基本工资" prop="baseSalary">
                  <el-input-number v-model="formData.baseSalary" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="formData.status" style="width: 100%">
                    <el-option label="在职" value="active" />
                    <el-option label="离职" value="left" />
                    <el-option label="退休" value="retired" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="合同开始" prop="contractStartDate">
                  <el-date-picker v-model="formData.contractStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="合同结束" prop="contractEndDate">
                  <el-date-picker v-model="formData.contractEndDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="账号信息" name="account">
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="formData.username" placeholder="请输入登录用户名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="formData.password" placeholder="请输入登录密码" show-password />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="角色" prop="role">
                  <el-select v-model="formData.role" style="width: 100%">
                    <el-option label="超级管理员" value="admin" />
                    <el-option label="部门管理员" value="dept_admin" />
                    <el-option label="普通员工" value="user" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>

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
import { User, Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import { useEmployeeStore } from '../../stores/employee'
import { useAuthStore } from '../../stores/auth'
import { useDepartmentStore } from '../../stores/department'

const employeeStore = useEmployeeStore()
const authStore = useAuthStore()
const departmentStore = useDepartmentStore()

const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')
const userDepartment = computed(() => authStore.currentUser?.department)

const loading = ref(false)
const employeePage = reactive({
  records: [],
  total: 0,
  current: 1,
  size: 10
})

const searchForm = reactive({
  keyword: '',
  department: '',
  employeeCategory: '',
  status: ''
})

const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const isView = ref(false)
const activeTab = ref('basic')
const formRef = ref(null)

const dialogTitle = computed(() => {
  if (isView.value) return '员工详情'
  return isEdit.value ? '编辑员工' : '添加员工'
})

const formData = reactive({
  employeeNo: '',
  name: '',
  photo: '',
  age: null,
  gender: '男',
  birthDate: '',
  ethnicity: '',
  politicalStatus: '',
  maritalStatus: '',
  idCard: '',
  department: '',
  position: '',
  teamGroup: '',
  employeeCategory: '',
  title: '',
  major: '',
  education: '',
  graduationSchool: '',
  graduationDate: '',
  baseSalary: 0,
  entryDate: '',
  workStartDate: '',
  contractStartDate: '',
  contractEndDate: '',
  homeAddress: '',
  phone: '',
  email: '',
  remark: '',
  status: 'active',
  username: '',
  password: '',
  role: 'user'
})

const rules = {
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(async () => {
  await departmentStore.fetchDepartments()
  await loadEmployeeList()
})

async function loadEmployeeList() {
  loading.value = true
  try {
    const params = {
      current: employeePage.current,
      size: employeePage.size,
      keyword: searchForm.keyword,
      department: searchForm.department,
      employeeCategory: searchForm.employeeCategory,
      status: searchForm.status
    }
    if (isDeptAdmin.value) {
      params.department = userDepartment.value
    }
    const data = await employeeStore.fetchEmployeePage(params)
    employeePage.records = data.records
    employeePage.total = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  employeePage.current = 1
  loadEmployeeList()
}

function handleSizeChange(size) {
  employeePage.size = size
  loadEmployeeList()
}

function handleCurrentChange(page) {
  employeePage.current = page
  loadEmployeeList()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function openDialog(row = null) {
  isView.value = false
  activeTab.value = 'basic'
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, {
      employeeNo: '',
      name: '',
      photo: '',
      age: null,
      gender: '男',
      birthDate: '',
      ethnicity: '',
      politicalStatus: '',
      maritalStatus: '',
      idCard: '',
      department: isDeptAdmin.value ? userDepartment.value : '',
      position: '',
      teamGroup: '',
      employeeCategory: '',
      title: '',
      major: '',
      education: '',
      graduationSchool: '',
      graduationDate: '',
      baseSalary: 0,
      entryDate: '',
      workStartDate: '',
      contractStartDate: '',
      contractEndDate: '',
      homeAddress: '',
      phone: '',
      email: '',
      remark: '',
      status: 'active',
      username: '',
      password: '',
      role: 'user'
    })
  }
  dialogVisible.value = true
}

function viewEmployee(row) {
  isView.value = true
  isEdit.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (isView.value) {
    dialogVisible.value = false
    return
  }
  await formRef.value.validate()
  if (isEdit.value) {
    await employeeStore.updateEmployee(formData)
    ElMessage.success('编辑成功')
  } else {
    await employeeStore.addEmployee(formData)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadEmployeeList()
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该员工吗？', '提示', { type: 'warning' })
    .then(async () => {
      await employeeStore.deleteEmployee(row.id)
      ElMessage.success('删除成功')
      loadEmployeeList()
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
.card-header > span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
.filter-area {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
