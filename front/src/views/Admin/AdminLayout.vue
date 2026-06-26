<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <el-icon :size="24" color="#fff"><Setting /></el-icon>
        <span>{{ isDeptAdmin ? '部门管理' : '管理后台' }}</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item v-if="!isDeptAdmin" index="/admin/department">
          <el-icon><OfficeBuilding /></el-icon>
          <span>部门管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/employee">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notice">
          <el-icon><Bell /></el-icon>
          <span>通知管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/leave">
          <el-icon><Document /></el-icon>
          <span>请假审批</span>
        </el-menu-item>
        <el-menu-item index="/admin/salary">
          <el-icon><Money /></el-icon>
          <span>工资管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/attendance">
          <el-icon><Clock /></el-icon>
          <span>考勤记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/performance">
          <el-icon><TrendCharts /></el-icon>
          <span>绩效考核</span>
        </el-menu-item>
        <el-menu-item index="/admin/training">
          <el-icon><Reading /></el-icon>
          <span>培训管理</span>
        </el-menu-item>
        <el-menu-item v-if="!isDeptAdmin" index="/admin/fund">
          <el-icon><Wallet /></el-icon>
          <span>公积金管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/transfer">
          <el-icon><Switch /></el-icon>
          <span>人员调动</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="welcome">
          欢迎，{{ authStore.currentUser?.username }}
          <el-tag v-if="isDeptAdmin" type="warning" size="small" style="margin-left: 8px">{{ authStore.currentUser?.department }}</el-tag>
        </span>
        <div class="header-actions">
          <el-button v-if="isDeptAdmin" type="primary" text @click="router.push('/user')">
            <el-icon><UserFilled /></el-icon> 员工中心
          </el-button>
          <el-button type="danger" text @click="handleLogout">
            <el-icon><SwitchButton /></el-icon> 退出登录
          </el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { User, UserFilled, Bell, Document, Setting, SwitchButton, Money, Clock, TrendCharts, DataAnalysis, OfficeBuilding, Reading, Wallet, Switch } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const isDeptAdmin = computed(() => authStore.currentUser?.role === 'dept_admin')

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5e;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.welcome {
  color: #303133;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.main {
  background: #f5f7fa;
  padding: 20px;
}

:deep(.el-menu) {
  border-right: none;
}
</style>
