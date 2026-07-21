<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Bell, User, QuestionFilled, InfoFilled, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const userName = computed(() => authStore.userInfo?.name || authStore.userInfo?.username || 'User')

function handleUserCommand(command: string) {
  switch (command) {
    case 'profile':
      ElMessage.info('Profile coming soon')
      break
    case 'help':
      ElMessage.info('Help center coming soon')
      break
    case 'about':
      ElMessageBox.alert('Blind Plate Management System v1.0.0', 'About')
      break
    case 'logout':
      handleLogout()
      break
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('Are you sure you want to logout?', 'Confirm', {
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      type: 'warning'
    })
    authStore.logout()
    router.push('/login')
  } catch {
    // cancelled
  }
}
</script>

<template>
  <el-header class="app-header" height="var(--brand-header-height)">
    <div class="header-left">
      <span class="logo">Blind Plate Management</span>
    </div>
    <div class="header-right">
      <el-badge :value="0" :hidden="true" class="message-badge">
        <el-button :icon="Bell" text circle />
      </el-badge>
      <el-dropdown trigger="click" @command="handleUserCommand">
        <div class="user-info">
          <el-icon><User /></el-icon>
          <span class="user-name">{{ userName }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile" :icon="User">Profile</el-dropdown-item>
            <el-dropdown-item command="help" :icon="QuestionFilled">Help</el-dropdown-item>
            <el-dropdown-item command="about" :icon="InfoFilled">About</el-dropdown-item>
            <el-dropdown-item divided command="logout" :icon="SwitchButton">Logout</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--brand-spacing-6);
  background-color: var(--brand-bg-white);
  border-bottom: 1px solid var(--brand-border-light);
  box-shadow: var(--brand-shadow-sm);
}
.header-left {
  display: flex;
  align-items: center;
}
.logo {
  font-size: var(--brand-font-size-xl);
  font-weight: var(--brand-font-weight-semibold);
  color: var(--brand-color-primary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: var(--brand-spacing-4);
}
.message-badge {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: var(--brand-spacing-2);
  cursor: pointer;
  padding: var(--brand-spacing-2) var(--brand-spacing-3);
  border-radius: var(--brand-radius-md);
  transition: background-color 0.2s ease;
}
.user-info:hover {
  background-color: var(--brand-bg-hover);
}
.user-name {
  font-size: var(--brand-font-size-base);
  color: var(--brand-text-primary);
}
</style>
