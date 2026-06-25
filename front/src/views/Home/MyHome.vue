<template>
  <div class="home-container">
    <el-container>
      <el-aside width="200px">
        <div class="logo">🛒 校园二手</div>
        <el-menu 
          :default-active="$route.path" 
          router 
          background-color="#fff" 
          text-color="#666" 
          active-text-color="#52c41a"
        >
          <el-menu-item index="/home/page1">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/home/page2">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/home/page3">
            <el-icon><Grid /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/home/page4">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/home/page5">
            <el-icon><ChatDotRound /></el-icon>
            <span>消息中心</span>
          </el-menu-item>
          <el-menu-item index="/home/page6">
            <el-icon><User /></el-icon>
            <span>个人设置</span>
          </el-menu-item>
          <el-menu-item index="/">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { DataAnalysis, Goods, Grid, Document, ChatDotRound, User, SwitchButton } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background: #fff;
  height: 100%;
  border-right: 1px solid #f0f0f0;
}

.el-menu {
  border-right: none;
}

.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #52c41a;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid #f0f0f0;
}

.el-main {
  background: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
}

:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
}

:deep(.el-menu-item:hover) {
  background-color: #f6ffed !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #f6ffed !important;
  color: #52c41a;
}
</style>
