<template>
  <div class="page-container">
    <div class="page-header">
      <h2>订单管理</h2>
      <p>管理交易订单记录</p>
    </div>
    
    <div class="toolbar">
      <el-input v-model="searchText" placeholder="搜索订单号或商品名称" clearable style="width: 280px" prefix-icon="Search" />
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增订单
      </el-button>
    </div>

    <el-table :data="filteredList" border stripe class="custom-table">
      <el-table-column prop="id" label="订单号" width="150" />
      <el-table-column prop="productName" label="商品名称" width="160" />
      <el-table-column prop="buyer" label="买家" width="90" />
      <el-table-column prop="seller" label="卖家" width="90" />
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">
          <span class="amount">¥{{ row.amount.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createDate" label="创建日期" width="110" />
      <el-table-column prop="completeDate" label="完成日期" width="110">
        <template #default="{ row }">
          {{ row.completeDate || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑订单' : '新增订单'" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="商品" prop="productName">
          <el-input v-model="formData.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="买家" prop="buyer">
              <el-input v-model="formData.buyer" placeholder="买家昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="卖家" prop="seller">
              <el-input v-model="formData.seller" placeholder="卖家昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="formData.amount" :min="0" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%" @change="handleStatusChange">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建日期" prop="createDate">
              <el-date-picker v-model="formData.createDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="完成日期">
              <el-date-picker v-model="formData.completeDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" :disabled="formData.status !== '已完成'" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useSecondhandStore } from '@/stores/secondhand'

const store = useSecondhandStore()

const statuses = ['待付款', '已付款', '已完成', '已取消']
const statusType = { '待付款': 'warning', '已付款': 'primary', '已完成': 'success', '已取消': 'info' }

const searchText = ref('')
const filteredList = computed(() =>
  store.orders.filter(o => o.id.includes(searchText.value) || o.productName.includes(searchText.value))
)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: '', productName: '', productId: '', buyer: '', seller: '',
  amount: 0, status: '待付款', createDate: '', completeDate: ''
})

const rules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  buyer: [{ required: true, message: '请输入买家', trigger: 'blur' }],
  seller: [{ required: true, message: '请输入卖家', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

const handleStatusChange = (val) => {
  if (val === '已完成' && !formData.completeDate) {
    formData.completeDate = new Date().toISOString().slice(0, 10)
  } else if (val !== '已完成') {
    formData.completeDate = ''
  }
}

const handleAdd = () => {
  isEdit.value = false
  const today = new Date().toISOString().slice(0, 10)
  const orderId = `ORD${today.replace(/-/g, '')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  Object.assign(formData, {
    id: orderId, productName: '', productId: '', buyer: '', seller: '',
    amount: 0, status: '待付款', createDate: today, completeDate: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该订单？', '提示', { type: 'warning' }).then(() => {
    const index = store.orders.findIndex(o => o.id === id)
    if (index > -1) store.orders.splice(index, 1)
    ElMessage.success('删除成功')
  })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    const i = store.orders.findIndex(o => o.id === formData.id)
    store.orders.splice(i, 1, { ...formData })
  } else {
    store.orders.push({ ...formData })
  }
  dialogVisible.value = false
  ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
}
</script>

<style scoped>
.page-container {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #333;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #999;
  margin: 0;
  font-size: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.amount {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

:deep(.el-button--primary) {
  background: #52c41a;
  border-color: #52c41a;
}

:deep(.el-button--primary:hover) {
  background: #73d13d;
  border-color: #73d13d;
}
</style>
