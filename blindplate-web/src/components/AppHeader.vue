<template>
  <div class="app-header">
    <div class="header-left">
      <span class="logo">盲板管理系统</span>
    </div>
    <div class="header-right">
      <span class="username">{{ authStore.userInfo?.name || authStore.userInfo?.username }}</span>
      <el-button type="text" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 50px;
  background: #409eff;
  color: white;
}
.header-left .logo {
  font-size: 16px;
  font-weight: bold;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  font-size: 14px;
}
</style>
