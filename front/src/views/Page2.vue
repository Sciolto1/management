<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品管理</h2>
      <p>管理您发布的二手商品</p>
    </div>
    
    <div class="toolbar">
      <el-input v-model="searchText" placeholder="搜索商品名称或编号" clearable style="width: 280px" prefix-icon="Search" />
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>发布商品
      </el-button>
    </div>

    <el-table :data="filteredList" border stripe class="custom-table">
      <el-table-column prop="id" label="编号" width="90" />
      <el-table-column prop="name" label="商品名称" width="160" />
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">
          <el-tag type="success" effect="plain">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="售价" width="100">
        <template #default="{ row }">
          <span class="price">¥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="原价" width="90">
        <template #default="{ row }">
          <span class="original-price">¥{{ row.originalPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="condition" label="成色" width="90">
        <template #default="{ row }">
          <el-tag :type="conditionType[row.condition]" size="small">{{ row.condition }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="seller" label="卖家" width="90" />
      <el-table-column prop="publishDate" label="发布日期" width="110" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '发布商品'" width="550px" class="custom-dialog">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="formData.originalPrice" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="成色" prop="condition">
              <el-select v-model="formData.condition" style="width: 100%">
                <el-option v-for="c in conditions" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" style="width: 100%">
                <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="卖家" prop="seller">
          <el-input v-model="formData.seller" placeholder="请输入卖家昵称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请描述商品详情" />
        </el-form-item>
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

const categories = ['电子产品', '书籍教材', '生活用品', '服饰鞋包', '运动户外', '其他']
const conditions = ['全新', '九成新', '八成新', '七成新']
const statuses = ['在售', '已售', '下架']

const conditionType = { '全新': 'success', '九成新': 'primary', '八成新': 'warning', '七成新': 'info' }
const statusType = { '在售': 'success', '已售': 'info', '下架': 'danger' }

const searchText = ref('')
const filteredList = computed(() =>
  store.products.filter(p => p.name.includes(searchText.value) || p.id.includes(searchText.value))
)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: '', name: '', category: '电子产品', price: 0, originalPrice: 0,
  condition: '九成新', status: '在售', seller: '', description: '', publishDate: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  seller: [{ required: true, message: '请输入卖家', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  const newId = `P${String(store.products.length + 1).padStart(3, '0')}`
  Object.assign(formData, {
    id: newId, name: '', category: '电子产品', price: 0, originalPrice: 0,
    condition: '九成新', status: '在售', seller: '', description: '',
    publishDate: new Date().toISOString().slice(0, 10)
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' }).then(() => {
    const index = store.products.findIndex(p => p.id === id)
    if (index > -1) store.products.splice(index, 1)
    ElMessage.success('删除成功')
  })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    const i = store.products.findIndex(p => p.id === formData.id)
    store.products.splice(i, 1, { ...formData })
  } else {
    store.products.push({ ...formData })
  }
  dialogVisible.value = false
  ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
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

.price {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 13px;
}

:deep(.el-button--primary) {
  background: #52c41a;
  border-color: #52c41a;
}

:deep(.el-button--primary:hover) {
  background: #73d13d;
  border-color: #73d13d;
}

:deep(.custom-table .el-table__header th) {
  background: #fafafa;
}
</style>
