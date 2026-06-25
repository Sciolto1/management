<template>
  <div class="page-container">
    <div class="page-header">
      <h2>个人设置</h2>
      <p>管理您的账户信息</p>
    </div>

    <div class="settings-grid">
      <div class="settings-card">
        <div class="card-header">
          <span class="card-icon">👤</span>
          <h3>个人信息</h3>
        </div>
        <el-form :model="formData" label-width="90px" class="settings-form">
          <el-form-item label="用户名">
            <el-input v-model="formData.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="formData.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="formData.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="学校">
            <el-input v-model="formData.school" placeholder="请输入学校名称" />
          </el-form-item>
          <el-form-item label="宿舍地址">
            <el-input v-model="formData.dormitory" placeholder="请输入宿舍地址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="settings-card">
        <div class="card-header">
          <span class="card-icon">🔐</span>
          <h3>修改密码</h3>
        </div>
        <el-form ref="pwdFormRef" :model="pwdData" :rules="pwdRules" label-width="90px" class="settings-form">
          <el-form-item label="原密码" prop="oldPwd">
            <el-input v-model="pwdData.oldPwd" type="password" show-password placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPwd">
            <el-input v-model="pwdData.newPwd" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPwd">
            <el-input v-model="pwdData.confirmPwd" type="password" show-password placeholder="请再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleChangePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useSecondhandStore } from '@/stores/secondhand'

const store = useSecondhandStore()

// 使用store中的用户信息
const formData = reactive({ ...store.userProfile })

const pwdFormRef = ref(null)
const pwdData = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdData.newPwd) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const handleSave = () => {
  store.updateUserProfile(formData)
  ElMessage.success('个人信息保存成功')
}

const handleChangePwd = async () => {
  try {
    await pwdFormRef.value.validate()
    if (pwdData.oldPwd !== '123456') {
      return ElMessage.error('原密码错误')
    }
    ElMessage.success('密码修改成功')
    pwdData.oldPwd = ''
    pwdData.newPwd = ''
    pwdData.confirmPwd = ''
  } catch (e) {
    // 验证失败
  }
}
</script>

<style scoped>
.page-container { padding: 10px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 24px; color: #333; margin: 0 0 8px 0; }
.page-header p { color: #999; margin: 0; font-size: 14px; }

.settings-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.settings-card { background: #fff; padding: 28px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; }
.card-icon { font-size: 28px; }
.card-header h3 { margin: 0; font-size: 18px; color: #333; }
.settings-form { max-width: 400px; }

:deep(.el-button--primary) { background: #52c41a; border-color: #52c41a; }
:deep(.el-button--primary:hover) { background: #73d13d; border-color: #73d13d; }
:deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px #52c41a inset; }
:deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #52c41a inset; }
</style>
