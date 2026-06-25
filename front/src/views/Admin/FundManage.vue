<template>
  <div class="fund-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Wallet /></el-icon> 公积金管理</span>
          <div class="filter-area">
            <el-select v-model="filterStatus" placeholder="账户状态" clearable style="width: 120px; margin-right: 10px">
              <el-option label="正常" value="active" />
              <el-option label="封存" value="sealed" />
              <el-option label="注销" value="closed" />
            </el-select>
            <el-input v-model="searchKey" placeholder="搜索姓名/账号/部门" clearable style="width: 200px; margin-right: 10px" />
            <el-button type="primary" @click="openDialog()">
              <el-icon><Plus /></el-icon> 添加账户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <div class="statistics-cards">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalAccounts }}</div>
            <div class="stat-label">总账户数</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.activeAccounts }}</div>
            <div class="stat-label">有效账户</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.sealedAccounts }}</div>
            <div class="stat-label">封存账户</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">¥{{ formatMoney(stats.totalBalance) }}</div>
            <div class="stat-label">总余额</div>
          </div>
        </el-card>
      </div>

      <!-- 公积金账户表格 -->
      <el-table :data="filteredFunds" stripe>
        <el-table-column prop="accountNo" label="公积金账号" width="140" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="salary" label="工资额" width="100">
          <template #default="{ row }">
            ¥{{ formatMoney(row.salary) }}
          </template>
        </el-table-column>
        <el-table-column label="缴存金额" width="120">
          <template #default="{ row }">
            <span>个人: ¥{{ formatMoney(row.personalPay) }}</span>
            <br>
            <span>单位: ¥{{ formatMoney(row.companyPay) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="120">
          <template #default="{ row }">
            <span class="balance-text">¥{{ formatMoney(row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'active'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'sealed'" type="warning">封存</el-tag>
            <el-tag v-else type="info">注销</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="openDate" label="开户日期" width="110" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openDialog(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button v-if="row.status === 'active'" type="warning" text size="small" @click="handleSeal(row)">
              <el-icon><Lock /></el-icon> 封存
            </el-button>
            <el-button v-else type="success" text size="small" @click="handleUnseal(row)">
              <el-icon><Unlock /></el-icon> 解封
            </el-button>
            <el-button v-if="row.status === 'active'" type="info" text size="small" @click="handleDeposit(row)">
              <el-icon><Coin /></el-icon> 缴存
            </el-button>
            <el-button type="info" text size="small" @click="openTransactionDialog(row)">
              <el-icon><Tickets /></el-icon> 账务
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公积金账户' : '添加公积金账户'" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-select v-model="formData.department" placeholder="请选择部门" style="width: 100%">
                <el-option v-for="dept in departmentStore.departments" :key="dept.id" :label="dept.name" :value="dept.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公积金账号" prop="accountNo">
              <el-input v-model="formData.accountNo" placeholder="自动生成" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工资额" prop="salary">
              <el-input-number v-model="formData.salary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴存比例" prop="personalPay">
              <el-input-number v-model="formData.personalPay" :min="0" :precision="2" placeholder="个人缴额" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位缴额" prop="companyPay">
              <el-input-number v-model="formData.companyPay" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户状态" prop="status">
              <el-select v-model="formData.status" style="width: 100%">
                <el-option label="正常" value="active" />
                <el-option label="封存" value="sealed" />
                <el-option label="注销" value="closed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="formData.birthDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户日期" prop="openDate">
              <el-date-picker v-model="formData.openDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联员工ID" prop="employeeId">
              <el-input-number v-model="formData.employeeId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始余额" prop="balance">
              <el-input-number v-model="formData.balance" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 帐务记录对话框 -->
    <el-dialog v-model="transactionDialogVisible" :title="'公积金账务 - ' + currentFund?.name" width="900px">
      <div class="transaction-header">
        <el-button type="primary" @click="openAddTransactionDialog">
          <el-icon><Plus /></el-icon> 添加记录
        </el-button>
        <el-select v-model="transactionType" placeholder="交易类型" clearable style="width: 150px; margin-left: 10px">
          <el-option label="缴存" value="deposit" />
          <el-option label="提取" value="withdrawal" />
          <el-option label="利息" value="interest" />
          <el-option label="调整" value="adjustment" />
        </el-select>
      </div>
      <el-table :data="filteredTransactions" stripe style="margin-top: 15px">
        <el-table-column prop="transDate" label="日期" width="110" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'deposit'" type="success">缴存</el-tag>
            <el-tag v-else-if="row.type === 'withdrawal'" type="warning">提取</el-tag>
            <el-tag v-else-if="row.type === 'interest'" type="primary">利息</el-tag>
            <el-tag v-else type="info">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="借方" width="100">
          <template #default="{ row }">
            <span class="debit-text">¥{{ formatMoney(row.debit) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="贷方" width="100">
          <template #default="{ row }">
            <span class="credit-text">¥{{ formatMoney(row.credit) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="余额" width="120">
          <template #default="{ row }">
            <span class="balance-text">¥{{ formatMoney(row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="handleDeleteTransaction(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加账务记录对话框 -->
    <el-dialog v-model="addTransactionDialogVisible" title="添加账务记录" width="450px">
      <el-form ref="transactionFormRef" :model="transactionFormData" label-width="80px">
        <el-form-item label="交易日期" prop="transDate">
          <el-date-picker v-model="transactionFormData.transDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="交易类型" prop="type">
          <el-select v-model="transactionFormData.type" style="width: 100%">
            <el-option label="缴存" value="deposit" />
            <el-option label="提取" value="withdrawal" />
            <el-option label="利息" value="interest" />
            <el-option label="调整" value="adjustment" />
          </el-select>
        </el-form-item>
        <el-form-item label="贷方" prop="credit">
          <el-input-number v-model="transactionFormData.credit" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="借方" prop="debit">
          <el-input-number v-model="transactionFormData.debit" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="transactionFormData.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addTransactionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddTransaction">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet, Plus, Edit, Delete, Lock, Unlock, Coin, Tickets } from '@element-plus/icons-vue'
import { useFundStore } from '../../stores/fund'
import { useDepartmentStore } from '../../stores/department'

const fundStore = useFundStore()
const departmentStore = useDepartmentStore()

// 统计数据
const stats = reactive({
  totalAccounts: 0,
  activeAccounts: 0,
  sealedAccounts: 0,
  totalBalance: 0
})

// 搜索和过滤
const searchKey = ref('')
const filterStatus = ref('')

// 过滤后的公积金列表
const filteredFunds = computed(() => {
  let list = fundStore.funds
  if (filterStatus.value) {
    list = list.filter(f => f.status === filterStatus.value)
  }
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    list = list.filter(f =>
      (f.name && f.name.toLowerCase().includes(key)) ||
      (f.accountNo && f.accountNo.toLowerCase().includes(key)) ||
      (f.department && f.department.toLowerCase().includes(key))
    )
  }
  return list
})

// 添加/编辑对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  accountNo: '',
  name: '',
  gender: '',
  department: '',
  salary: 0,
  personalPay: 0,
  companyPay: 0,
  birthDate: '',
  balance: 0,
  status: 'active',
  openDate: '',
  employeeId: null
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  salary: [{ required: true, message: '请输入工资额', trigger: 'blur' }]
}

// 账务对话框
const transactionDialogVisible = ref(false)
const currentFund = ref(null)
const transactionType = ref('')
const transactions = ref([])

const filteredTransactions = computed(() => {
  let list = transactions.value
  if (transactionType.value) {
    list = list.filter(t => t.type === transactionType.value)
  }
  return list
})

// 添加账务记录对话框
const addTransactionDialogVisible = ref(false)
const transactionFormRef = ref(null)

const transactionFormData = reactive({
  transDate: '',
  type: 'deposit',
  credit: 0,
  debit: 0,
  remark: ''
})

onMounted(async () => {
  await loadData()
  departmentStore.fetchDepartments()
})

const loadData = async () => {
  await fundStore.fetchFunds()
  const statistics = await fundStore.fetchStatistics()
  if (statistics) {
    stats.totalAccounts = statistics.totalAccounts
    stats.activeAccounts = statistics.activeAccounts
    stats.sealedAccounts = statistics.sealedAccounts
    stats.totalBalance = statistics.totalBalance
  }
}

// 打开添加/编辑对话框
const openDialog = (row = null) => {
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: null,
      accountNo: '',
      name: '',
      gender: '',
      department: '',
      salary: 0,
      personalPay: 0,
      companyPay: 0,
      birthDate: '',
      balance: 0,
      status: 'active',
      openDate: '',
      employeeId: null
    })
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await fundStore.updateFund(formData)
    ElMessage.success('编辑成功')
  } else {
    await fundStore.addFund(formData)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  await loadData()
}

// 封存账户
const handleSeal = (row) => {
  ElMessageBox.confirm('确定要封存该公积金账户吗？', '提示', { type: 'warning' })
    .then(async () => {
      await fundStore.sealFund(row.id)
      ElMessage.success('封存成功')
      await loadData()
    })
    .catch(() => {})
}

// 解封账户
const handleUnseal = (row) => {
  ElMessageBox.confirm('确定要解封该公积金账户吗？', '提示', { type: 'warning' })
    .then(async () => {
      await fundStore.unsealFund(row.id)
      ElMessage.success('解封成功')
      await loadData()
    })
    .catch(() => {})
}

// 缴存
const handleDeposit = (row) => {
  ElMessageBox.confirm(`确定要为 ${row.name} 缴存公积金吗？\n个人缴额: ¥${row.personalPay}\n单位缴额: ¥${row.companyPay}`, '缴存确认')
    .then(async () => {
      await fundStore.depositFund(row.id)
      ElMessage.success('缴存成功')
      await loadData()
    })
    .catch(() => {})
}

// 打开账务对话框
const openTransactionDialog = async (row) => {
  currentFund.value = row
  transactions.value = await fundStore.fetchTransactions(row.id)
  transactionDialogVisible.value = true
}

// 打开添加账务记录对话框
const openAddTransactionDialog = () => {
  if (!currentFund.value) return
  Object.assign(transactionFormData, {
    transDate: '',
    type: 'deposit',
    credit: 0,
    debit: 0,
    remark: ''
  })
  addTransactionDialogVisible.value = true
}

// 添加账务记录
const handleAddTransaction = async () => {
  await fundStore.addTransaction({
    fundId: currentFund.value.id,
    accountNo: currentFund.value.accountNo,
    name: currentFund.value.name,
    transDate: transactionFormData.transDate,
    type: transactionFormData.type,
    credit: transactionFormData.credit,
    debit: transactionFormData.debit,
    remark: transactionFormData.remark
  })
  ElMessage.success('添加成功')
  addTransactionDialogVisible.value = false
  transactions.value = await fundStore.fetchTransactions(currentFund.value.id)
  await loadData()
}

// 删除账务记录
const handleDeleteTransaction = (row) => {
  ElMessageBox.confirm('确定要删除该账务记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await fundStore.deleteTransaction(row.id)
      ElMessage.success('删除成功')
      transactions.value = await fundStore.fetchTransactions(currentFund.value.id)
    })
    .catch(() => {})
}

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00'
  return parseFloat(value).toFixed(2)
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
.statistics-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
}
.stat-content {
  padding: 10px 0;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
.balance-text {
  color: #67C23A;
  font-weight: bold;
}
.debit-text {
  color: #F56C6C;
}
.credit-text {
  color: #67C23A;
}
.transaction-header {
  display: flex;
  align-items: center;
}
</style>
