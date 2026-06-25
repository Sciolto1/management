<template>
  <div class="user-hall">
    <!-- 顶部导航栏 -->
    <el-header class="hall-header">
      <div class="header-left">
        <el-icon :size="28" color="#409EFF"><UserFilled /></el-icon>
        <span class="title">员工中心</span>
      </div>
      <div class="header-right">
        <span class="welcome">欢迎，{{ employeeInfo?.name || authStore.currentUser?.username }}</span>
        <el-button v-if="isDeptAdmin" type="primary" text @click="router.push('/admin')">
          <el-icon><Setting /></el-icon> 返回管理
        </el-button>
        <el-button type="danger" text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出登录
        </el-button>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="hall-main">
      <!-- 大厅首页 - 功能卡片 -->
      <div v-if="isHallHome" class="hall-cards">
        <el-row :gutter="24">
          <el-col :xs="12" :sm="8" :md="6" v-for="item in menuItems" :key="item.path">
            <el-card 
              class="function-card" 
              shadow="hover" 
              @click="router.push(item.path)"
            >
              <div class="card-content">
                <el-icon :size="48" :color="item.color">
                  <component :is="item.icon" />
                </el-icon>
                <span class="card-title">{{ item.title }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 子页面内容 -->
      <div v-else class="sub-page">
        <div class="back-nav">
          <el-button type="primary" text @click="router.push('/user')">
            <el-icon><ArrowLeft /></el-icon> 返回大厅
          </el-button>
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        <router-view />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useEmployeeStore } from '../../stores/employee'
import { 
  User, UserFilled, Bell, Document, Clock, SwitchButton, 
  Money, TrendCharts, ArrowLeft, Setting, Reading, Wallet 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const employeeStore = useEmployeeStore()

const employeeInfo = ref(null)
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')

// 功能菜单配置
const menuItems = [
  { path: '/user/profile', title: '个人信息', icon: User, color: '#409EFF' },
  { path: '/user/notice', title: '公司通知', icon: Bell, color: '#E6A23C' },
  { path: '/user/leave', title: '请假申请', icon: Document, color: '#67C23A' },
  { path: '/user/attendance', title: '考勤打卡', icon: Clock, color: '#F56C6C' },
  { path: '/user/salary', title: '我的工资', icon: Money, color: '#909399' },
  { path: '/user/fund', title: '我的公积金', icon: Wallet, color: '#8E44AD' },
  { path: '/user/performance', title: '我的绩效', icon: TrendCharts, color: '#3498DB' },
  { path: '/user/training', title: '培训报名', icon: Reading, color: '#10B981' }
]

// 判断是否在大厅首页
const isHallHome = computed(() => route.path === '/user' || route.path === '/user/')

// 当前页面标题
const currentPageTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item?.title || ''
})

onMounted(async () => {
  if (authStore.currentUser?.employeeId) {
    try {
      employeeInfo.value = await employeeStore.getEmployeeById(authStore.currentUser.employeeId)
    } catch (e) {
      // 忽略错误
    }
  }
})

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.user-hall {
  min-height: 100vh;
  background: #fff;
}

.hall-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: 70px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome {
  color: #606266;
  font-size: 14px;
}

.hall-main {
  padding: 40px;
}

.hall-cards {
  max-width: 1200px;
  margin: 0 auto;
}

.function-card {
  margin-bottom: 24px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.function-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  gap: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.sub-page {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.back-nav {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

:deep(.el-card__body) {
  padding: 0;
}
</style>
