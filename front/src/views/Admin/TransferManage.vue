<template>
  <div class="transfer-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Switch /></el-icon> 人员调动管理</span>
          <div class="filter-area">
            <el-input v-model="searchForm.keyword" placeholder="搜索姓名/工号/单号" clearable style="width: 200px; margin-right: 10px" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.transferType" placeholder="调动类型" clearable style="width: 140px; margin-right: 10px" @change="handleSearch">
              <el-option label="内部调动" value="internal" />
              <el-option label="外单位调入" value="external_in" />
              <el-option label="调出外单位" value="external_out" />
              <el-option label="离退休" value="retire" />
              <el-option label="除名" value="dismiss" />
            </el-select>
            <el-select v-model="searchForm.status" placeholder="审批状态" clearable style="width: 120px; margin-right: 10px" @change="handleSearch">
              <el-option label="待审批" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已驳回" value="rejected" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon> 查询
            </el-button>
            <el-button type="success" @click="openAddDialog()" style="margin-left: 10px">
              <el-icon><Plus /></el-icon> 发起调动
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="20" style="margin-bottom: 16px">
        <el-col :span="4">
          <el-statistic title="总调动数" :value="statistics.total || 0" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="待审批" :value="statistics.pendingCount || 0" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="已通过" :value="statistics.approvedCount || 0" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="已驳回" :value="statistics.rejectedCount || 0" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="内部调动" :value="statistics.internalCount || 0" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="离退休" :value="statistics.retireCount || 0" />
        </el-col>
      </el-row>

      <el-table :data="transferPage.records" stripe v-loading="loading">
        <el-table-column prop="transferNo" label="调动单号" width="160" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="employeeNo" label="工号" width="100" />
        <el-table-column prop="transferType" label="调动类型" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.transferType === 'internal'" type="primary">内部调动</el-tag>
            <el-tag v-else-if="row.transferType === 'external_in'" type="success">外单位调入</el-tag>
            <el-tag v-else-if="row.transferType === 'external_out'" type="warning">调出外单位</el-tag>
            <el-tag v-else-if="row.transferType === 'retire'" type="info">离退休</el-tag>
            <el-tag v-else-if="row.transferType === 'dismiss'" type="danger">除名</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调动信息" min-width="300">
          <template #default="{ row }">
            <div class="transfer-info">
              <div><span class="label">原部门：</span>{{ row.sourceDepartment || '-' }}</div>
              <el-icon><Right /></el-icon>
              <div><span class="label">目标部门：</span>{{ row.targetDepartment || '-' }}</div>
            </div>
            <div class="transfer-info" style="margin-top: 4px">
              <div><span class="label">原职位：</span>{{ row.sourcePosition || '-' }}</div>
              <el-icon><Right /></el-icon>
              <div><span class="label">目标职位：</span>{{ row.targetPosition || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="transferDate" label="调动日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 'rejected'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approver" label="审批人" width="100" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'pending'" type="success" text size="small" @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 'pending'" type="danger" text size="small" @click="handleReject(row)">驳回</el-button>
            <el-button type="info" text size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="transferPage.total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="transferPage.size"
          v-model:current-page="transferPage.current"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 发起调动对话框 -->
    <el-dialog v-model="addDialogVisible" title="发起人员调动" width="700px">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="调动类型" prop="transferType">
              <el-radio-group v-model="addForm.transferType" @change="onTransferTypeChange">
                <el-radio value="internal">内部调动</el-radio>
                <el-radio value="external_in">外单位调入</el-radio>
                <el-radio value="external_out">调出外单位</el-radio>
                <el-radio value="retire">离退休</el-radio>
                <el-radio value="dismiss">除名</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工" prop="employeeId">
              <el-select v-model="addForm.employeeId" placeholder="请选择员工" filterable style="width: 100%" @change="onEmployeeChange">
                <el-option v-for="emp in activeEmployees" :key="emp.id" :label="`${emp.name} (${emp.employeeNo || emp.id})`" :value="emp.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调动日期" prop="transferDate">
              <el-date-picker v-model="addForm.transferDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>调动详情</el-divider>

        <el-row v-if="addForm.transferType !== 'external_in'" :gutter="20">
          <el-col :span="12">
            <el-form-item label="原部门">
              <el-input v-model="currentEmployee.department" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原职位">
              <el-input v-model="currentEmployee.position" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原班组">
              <el-input :model-value="currentEmployee.teamGroup || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原基本工资">
              <el-input :model-value="currentEmployee.baseSalary || 0" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="addForm.transferType === 'internal' || addForm.transferType === 'external_in'" :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标部门" prop="targetDepartment">
              <el-select v-model="addForm.targetDepartment" placeholder="请选择目标部门" style="width: 100%">
                <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标职位" prop="targetPosition">
              <el-input v-model="addForm.targetPosition" placeholder="请输入目标职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标班组">
              <el-input v-model="addForm.targetTeamGroup" placeholder="请输入目标班组" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标人员类别">
              <el-select v-model="addForm.targetEmployeeCategory" placeholder="请选择" clearable style="width: 100%">
                <el-option label="干部" value="cadre" />
                <el-option label="工人" value="worker" />
                <el-option label="临时工" value="temp" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标基本工资">
              <el-input-number v-model="addForm.targetBaseSalary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="调动原因" prop="reason">
              <el-input v-model="addForm.reason" type="textarea" :rows="3" placeholder="请输入调动原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="调动详情" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="调动单号">{{ detailData.transferNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调动类型">
          <el-tag v-if="detailData.transferType === 'internal'" type="primary">内部调动</el-tag>
          <el-tag v-else-if="detailData.transferType === 'external_in'" type="success">外单位调入</el-tag>
          <el-tag v-else-if="detailData.transferType === 'external_out'" type="warning">调出外单位</el-tag>
          <el-tag v-else-if="detailData.transferType === 'retire'" type="info">离退休</el-tag>
          <el-tag v-else-if="detailData.transferType === 'dismiss'" type="danger">除名</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="员工姓名">{{ detailData.employeeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职工编号">{{ detailData.employeeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原部门">{{ detailData.sourceDepartment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标部门">{{ detailData.targetDepartment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原职位">{{ detailData.sourcePosition || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标职位">{{ detailData.targetPosition || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原班组">{{ detailData.sourceTeamGroup || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标班组">{{ detailData.targetTeamGroup || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原基本工资">{{ detailData.sourceBaseSalary || 0 }}</el-descriptions-item>
        <el-descriptions-item label="目标基本工资">{{ detailData.targetBaseSalary || 0 }}</el-descriptions-item>
        <el-descriptions-item label="调动日期">{{ detailData.transferDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === 'pending'" type="warning">待审批</el-tag>
          <el-tag v-else-if="detailData.status === 'approved'" type="success">已通过</el-tag>
          <el-tag v-else-if="detailData.status === 'rejected'" type="danger">已驳回</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调动原因" :span="2">{{ detailData.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Switch, Search, Plus, Right } from '@element-plus/icons-vue'
import { useTransferStore } from '../../stores/transfer'
import { useEmployeeStore } from '../../stores/employee'
import { useDepartmentStore } from '../../stores/department'
import { useAuthStore } from '../../stores/auth'

const transferStore = useTransferStore()
const employeeStore = useEmployeeStore()
const departmentStore = useDepartmentStore()
const authStore = useAuthStore()

const transferPage = computed(() => transferStore.transferPage)
const statistics = computed(() => transferStore.statistics || {})
const departments = computed(() => departmentStore.departments)
const employees = computed(() => employeeStore.employees)

const activeEmployees = computed(() => 
  employees.value.filter(e => e.status === 'active' || !e.status)
)

const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  transferType: '',
  status: ''
})

const addDialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = reactive({
  employeeId: null,
  transferType: 'internal',
  targetDepartment: '',
  targetPosition: '',
  targetTeamGroup: '',
  targetEmployeeCategory: '',
  targetBaseSalary: 0,
  transferDate: null,
  reason: '',
  remark: ''
})
const currentEmployee = ref({
  department: '',
  position: '',
  teamGroup: '',
  baseSalary: 0
})

const addRules = {
  transferType: [{ required: true, message: '请选择调动类型', trigger: 'change' }],
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  transferDate: [{ required: true, message: '请选择调动日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入调动原因', trigger: 'blur' }]
}

const detailVisible = ref(false)
const detailData = ref(null)

onMounted(async () => {
  await loadData()
  await loadStatistics()
  employeeStore.fetchEmployees()
  departmentStore.fetchDepartments()
})

const loadData = async () => {
  loading.value = true
  try {
    await transferStore.fetchTransferPage({
      current: transferPage.value.current,
      size: transferPage.value.size,
      keyword: searchForm.keyword,
      transferType: searchForm.transferType,
      status: searchForm.status
    })
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    await transferStore.fetchStatistics()
  } catch (e) {}
}

const handleSearch = () => {
  transferStore.transferPage.current = 1
  loadData()
}

const handleSizeChange = (val) => {
  transferStore.transferPage.size = val
  loadData()
}

const handleCurrentChange = (val) => {
  transferStore.transferPage.current = val
  loadData()
}

const openAddDialog = () => {
  Object.assign(addForm, {
    employeeId: null,
    transferType: 'internal',
    targetDepartment: '',
    targetPosition: '',
    targetTeamGroup: '',
    targetEmployeeCategory: '',
    targetBaseSalary: 0,
    transferDate: new Date(),
    reason: '',
    remark: ''
  })
  currentEmployee.value = { department: '', position: '', teamGroup: '', baseSalary: 0 }
  addDialogVisible.value = true
}

const onTransferTypeChange = () => {
  if (addForm.transferType === 'retire' || addForm.transferType === 'dismiss' || addForm.transferType === 'external_out') {
    addForm.targetDepartment = ''
    addForm.targetPosition = ''
    addForm.targetTeamGroup = ''
    addForm.targetEmployeeCategory = ''
    addForm.targetBaseSalary = 0
  }
}

const onEmployeeChange = (empId) => {
  const emp = employees.value.find(e => e.id === empId)
  if (emp) {
    currentEmployee.value = {
      department: emp.department || '',
      position: emp.position || '',
      teamGroup: emp.teamGroup || '',
      baseSalary: emp.baseSalary || 0
    }
    if (addForm.transferType === 'internal') {
      addForm.targetDepartment = emp.department
      addForm.targetPosition = emp.position
      addForm.targetTeamGroup = emp.teamGroup
      addForm.targetEmployeeCategory = emp.employeeCategory
      addForm.targetBaseSalary = emp.baseSalary
    }
  }
}

const handleAddSubmit = async () => {
  await addFormRef.value.validate()
  try {
    await transferStore.addTransfer(addForm)
    ElMessage.success('调动申请提交成功')
    addDialogVisible.value = false
    loadData()
    loadStatistics()
  } catch (e) {
    ElMessage.error(e.message || '提交失败')
  }
}

const handleApprove = (row) => {
  ElMessageBox.confirm(`确定通过 ${row.employeeName} 的调动申请吗？`, '确认', {
    type: 'warning'
  }).then(async () => {
    try {
      const approver = authStore.user?.name || '管理员'
      await transferStore.approveTransfer(row.id, approver)
      ElMessage.success('审批通过')
      loadData()
      loadStatistics()
    } catch (e) {
      ElMessage.error(e.message || '审批失败')
    }
  }).catch(() => {})
}

const handleReject = (row) => {
  ElMessageBox.prompt('请输入驳回原因', '驳回调动', {
    confirmButtonText: '确认驳回',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入驳回原因',
    type: 'warning'
  }).then(async ({ value }) => {
    try {
      const approver = authStore.user?.name || '管理员'
      await transferStore.rejectTransfer(row.id, approver, value || '')
      ElMessage.success('已驳回')
      loadData()
      loadStatistics()
    } catch (e) {
      ElMessage.error(e.message || '操作失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该调动记录吗？', '确认', { type: 'warning' })
    .then(async () => {
      await transferStore.deleteTransfer(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadStatistics()
    })
    .catch(() => {})
}

const viewDetail = (row) => {
  detailData.value = row
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
.transfer-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}
.transfer-info .label {
  color: #909399;
}
</style>
