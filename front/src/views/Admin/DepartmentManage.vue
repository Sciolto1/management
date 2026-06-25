<template>
  <div class="department-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><OfficeBuilding /></el-icon> 部门管理</span>
          <div class="filter-area">
            <el-input v-model="searchForm.keyword" placeholder="搜索部门名称/编码" clearable style="width: 200px; margin-right: 10px" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.status" placeholder="部门状态" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
              <el-option label="正常" value="active" />
              <el-option label="停用" value="inactive" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon> 查询
            </el-button>
            <el-button type="success" @click="openDialog()" style="margin-left: 10px">
              <el-icon><Plus /></el-icon> 添加部门
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0" style="margin-left: 10px">
              <el-icon><Delete /></el-icon> 批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="departmentPage.records" stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="deptCode" label="部门编码" width="120" />
        <el-table-column prop="name" label="部门名称" width="150" />
        <el-table-column prop="manager" label="负责人" width="100">
          <template #default="{ row }">
            {{ row.manager || '未指定' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="部门描述" show-overflow-tooltip />
        <el-table-column prop="employeeCount" label="员工人数" width="100">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.employeeCount || 0 }} 人</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'active'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'inactive'" type="info">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewDepartment(row)">详情</el-button>
            <el-button type="success" text size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="departmentPage.total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="departmentPage.size"
          v-model:current-page="departmentPage.current"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="部门编码" prop="deptCode">
                  <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门名称" prop="name">
                  <el-input v-model="formData.name" placeholder="请输入部门名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门状态" prop="status">
                  <el-radio-group v-model="formData.status">
                    <el-radio value="active">正常</el-radio>
                    <el-radio value="inactive">停用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门负责人" prop="manager">
                  <el-select v-model="formData.manager" placeholder="请选择负责人" clearable style="width: 100%">
                    <el-option v-for="emp in allEmployees" :key="emp.id" :label="emp.name" :value="emp.name" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="部门描述" prop="description">
                  <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="员工信息" name="employees">
          <div class="employee-info-section">
            <el-empty v-if="!currentDeptName || deptEmployees.length === 0" description="暂无员工信息" />
            <div v-else>
              <h4>部门员工列表</h4>
              <el-table :data="deptEmployees" stripe size="small">
                <el-table-column prop="employeeNo" label="工号" width="100" />
                <el-table-column prop="name" label="姓名" width="100" />
                <el-table-column prop="position" label="职位" width="120" />
                <el-table-column prop="phone" label="电话" width="130" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag v-if="row.status === 'active'" type="success" size="small">在职</el-tag>
                    <el-tag v-else-if="row.status === 'left'" type="info" size="small">离职</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="部门详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="部门编码">{{ detailData.deptCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门名称">{{ detailData.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门状态">
          <el-tag v-if="detailData.status === 'active'" type="success">正常</el-tag>
          <el-tag v-else-if="detailData.status === 'inactive'" type="info">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="部门负责人">{{ detailData.manager || '未指定' }}</el-descriptions-item>
        <el-descriptions-item label="员工人数">{{ detailData.employeeCount || 0 }} 人</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门描述" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>部门员工</el-divider>
      <el-table :data="detailEmployees" stripe size="small" max-height="300">
        <el-table-column prop="employeeNo" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'active'" type="success" size="small">在职</el-tag>
            <el-tag v-else-if="row.status === 'left'" type="info" size="small">离职</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="detailEmployees.length === 0" description="暂无员工" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { useDepartmentStore } from '../../stores/department'
import { useEmployeeStore } from '../../stores/employee'

const departmentStore = useDepartmentStore()
const employeeStore = useEmployeeStore()

const departmentPage = ref({
  records: [],
  total: 0,
  current: 1,
  size: 10
})

const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  status: ''
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('添加部门')
const activeTab = ref('basic')
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const currentDeptName = ref('')
const selectedIds = ref([])
const selectedRows = ref([])

const allEmployees = computed(() => employeeStore.employees)
const deptEmployees = computed(() => {
  if (!currentDeptName.value) return []
  return employeeStore.employees.filter(e => e.department === currentDeptName.value)
})

const detailData = ref({})
const detailEmployees = ref([])

const formData = reactive({
  deptCode: '',
  name: '',
  manager: '',
  description: '',
  remark: '',
  status: 'active'
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
  employeeStore.fetchEmployees()
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: departmentPage.value.current,
      size: departmentPage.value.size,
      keyword: searchForm.keyword
    }
    const res = await departmentStore.fetchDepartmentPage(params)
    departmentPage.value.records = res.records
    departmentPage.value.total = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  departmentPage.value.current = 1
  loadData()
}

const handleSizeChange = (val) => {
  departmentPage.value.size = val
  loadData()
}

const handleCurrentChange = (val) => {
  departmentPage.value.current = val
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
  selectedIds.value = rows.map(row => row.id)
}

const openDialog = (row = null) => {
  if (row) {
    isEdit.value = true
    dialogTitle.value = '编辑部门'
    editId.value = row.id
    currentDeptName.value = row.name
    Object.assign(formData, {
      deptCode: row.deptCode,
      name: row.name,
      manager: row.manager,
      description: row.description,
      remark: row.remark,
      status: row.status || 'active'
    })
  } else {
    isEdit.value = false
    dialogTitle.value = '添加部门'
    editId.value = null
    currentDeptName.value = ''
    Object.assign(formData, {
      deptCode: '',
      name: '',
      manager: '',
      description: '',
      remark: '',
      status: 'active'
    })
  }
  activeTab.value = 'basic'
  dialogVisible.value = true
}

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
  loadData()
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
      loadData()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  const hasEmployeeDepts = selectedRows.value.filter(row => row.employeeCount > 0)
  if (hasEmployeeDepts.length > 0) {
    const names = hasEmployeeDepts.map(row => row.name).join('、')
    ElMessage.warning(`以下部门还有员工，无法删除：${names}`)
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个部门吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await departmentStore.batchDeleteDepartments(selectedIds.value)
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      selectedRows.value = []
      loadData()
    })
    .catch(() => {})
}

const viewDepartment = async (row) => {
  detailData.value = row
  currentDeptName.value = row.name
  detailEmployees.value = employeeStore.employees.filter(e => e.department === row.name)
  detailVisible.value = true
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
  flex-wrap: wrap;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.employee-info-section {
  padding: 10px 0;
}
.employee-info-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
}
</style>
