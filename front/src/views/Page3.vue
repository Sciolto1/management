<template>
  <div class="page-container">
    <div class="page-header">
      <h2>分类管理</h2>
      <p>管理商品分类体系</p>
    </div>
    
    <div class="toolbar">
      <el-input v-model="searchText" placeholder="搜索分类名称" clearable style="width: 280px" prefix-icon="Search" />
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增分类
      </el-button>
    </div>

    <el-table :data="filteredList" border stripe class="custom-table">
      <el-table-column prop="id" label="编号" width="100" />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template #default="{ row }">
          <span class="category-icon">{{ row.icon }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="分类名称" width="150" />
      <el-table-column label="商品数量" width="120">
        <template #default="{ row }">
          <el-tag type="success" effect="plain">{{ store.getCategoryProductCount(row.name) }} 件</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="450px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入emoji图标">
            <template #append>
              <el-popover placement="bottom" :width="280" trigger="click">
                <template #reference>
                  <el-button>选择</el-button>
                </template>
                <div class="emoji-picker">
                  <span v-for="e in emojis" :key="e" class="emoji-item" @click="formData.icon = e">{{ e }}</span>
                </div>
              </el-popover>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="1" :max="99" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="启用">启用</el-radio>
            <el-radio value="禁用">禁用</el-radio>
          </el-radio-group>
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

const emojis = ['📱', '📚', '🏠', '👕', '⚽', '📦', '💻', '🎮', '🎧', '⌚', '📷', '🎸', '🚲', '🎒', '👟', '🧸']

const searchText = ref('')
const filteredList = computed(() =>
  store.categories.filter(c => c.name.includes(searchText.value))
)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({ id: '', name: '', icon: '📦', sort: 1, status: '启用' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请选择图标', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  const newId = `C${String(store.categories.length + 1).padStart(3, '0')}`
  Object.assign(formData, { id: newId, name: '', icon: '📦', sort: store.categories.length + 1, status: '启用' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  const productCount = store.getCategoryProductCount(row.name)
  if (productCount > 0) {
    ElMessageBox.confirm(
      `该分类下有 ${productCount} 件商品，删除后这些商品将失去分类。确定删除吗？`,
      '警告',
      { type: 'warning' }
    ).then(() => doDelete(row.id))
  } else {
    ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' }).then(() => doDelete(row.id))
  }
}

const doDelete = (id) => {
  const index = store.categories.findIndex(c => c.id === id)
  if (index > -1) store.categories.splice(index, 1)
  ElMessage.success('删除成功')
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    const i = store.categories.findIndex(c => c.id === formData.id)
    store.categories.splice(i, 1, { ...formData })
  } else {
    store.categories.push({ ...formData })
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

.category-icon {
  font-size: 24px;
}

.emoji-picker {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}

.emoji-item {
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  text-align: center;
  border-radius: 4px;
  transition: background 0.2s;
}

.emoji-item:hover {
  background: #f0f0f0;
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
