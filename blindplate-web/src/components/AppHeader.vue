<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Bell, User, QuestionFilled, InfoFilled, SwitchButton, EditPen, Fold, Expand } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { setLocale } from '@/i18n'

const { t, locale } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

defineProps<{
  sidebarVisible: boolean
}>()

const emit = defineEmits<{
  (e: 'toggle-sidebar'): void
}>()
const userName = computed(() => authStore.userInfo?.name || authStore.userInfo?.username || 'User')

function handleUserCommand(command: string) {
  switch (command) {
    case 'profile':
      ElMessage.info(t('message.profileComing'))
      break
    case 'help':
      ElMessage.info(t('message.helpComing'))
      break
    case 'about':
      ElMessageBox.alert(t('confirm.aboutContent'), t('confirm.aboutTitle'))
      break
    case 'language-zh':
      setLocale('zh-CN')
      break
    case 'language-en':
      setLocale('en')
      break
    case 'logout':
      handleLogout()
      break
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm(t('confirm.logoutContent'), t('confirm.logoutTitle'), {
      confirmButtonText: t('button.yes'),
      cancelButtonText: t('button.no'),
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
      <el-button :icon="sidebarVisible ? Fold : Expand" text circle @click="emit('toggle-sidebar')" class="sidebar-toggle" />
      <span class="logo">{{ t('app.title') }}</span>
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
            <el-dropdown-item command="profile" :icon="User">{{ t('header.accountInfo') }}</el-dropdown-item>
            <el-dropdown-item command="help" :icon="QuestionFilled">{{ t('header.help') }}</el-dropdown-item>
            <el-dropdown-item command="about" :icon="InfoFilled">{{ t('header.about') }}</el-dropdown-item>
            <el-dropdown-item divided :icon="EditPen" command="language-zh" v-if="locale !== 'zh-CN'">{{ t('header.chinese') }}</el-dropdown-item>
            <el-dropdown-item :icon="EditPen" command="language-en" v-if="locale !== 'en'">{{ t('header.english') }}</el-dropdown-item>
            <el-dropdown-item divided command="logout" :icon="SwitchButton">{{ t('header.logout') }}</el-dropdown-item>
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
  gap: var(--brand-spacing-3);
}
.sidebar-toggle {
  font-size: 20px;
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
.language-trigger {
  display: block;
  width: 100%;
}
</style>