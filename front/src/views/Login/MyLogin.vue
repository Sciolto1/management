<template>
  <div class="login-container">
    <div class="login-form">
      <div class="logo-icon">
        <el-icon :size="48" color="#409EFF"><User /></el-icon>
      </div>
      <h2>人事管理系统</h2>
      <p class="subtitle">HR Management System</p>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="formData.username" placeholder="请输入账号" size="large">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" size="large" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="formData.role" class="role-group">
            <el-radio value="user">
              <el-icon><UserFilled /></el-icon> 员工登录
            </el-radio>
            <el-radio value="admin">
              <el-icon><Setting /></el-icon> 管理员登录
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin">
            <el-icon><Right /></el-icon> 登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Setting, Right } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const formData = reactive({ username: '', password: '', role: 'user' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  const success = await authStore.login(formData.username, formData.password, formData.role)
  if (success) {
    ElMessage.success('登录成功')
    // 根据用户选择的登录方式跳转（dept_admin两种方式都能登录）
    router.push(formData.role === 'admin' ? '/admin' : '/user')
  } else {
    ElMessage.error('账号、密码或角色不匹配')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-form {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(64, 158, 255, 0.15);
}

.logo-icon {
  text-align: center;
  margin-bottom: 10px;
}

h2 {
  text-align: center;
  color: #303133;
  margin-bottom: 8px;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  text-align: center;
  color: #409EFF;
  font-size: 14px;
  margin-bottom: 30px;
}

.role-group {
  width: 100%;
  display: flex;
  justify-content: space-around;
}

.login-btn {
  width: 100%;
  background: #409EFF;
  border: none;
  font-size: 16px;
  height: 44px;
  border-radius: 8px;
}

.login-btn:hover {
  background: #66b1ff;
}


:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

:deep(.el-radio) {
  margin-right: 20px;
}

</style>
