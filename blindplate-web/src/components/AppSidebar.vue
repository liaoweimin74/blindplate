<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import {
  Document,
  Location,
  List,
  DataAnalysis,
  Expand,
  Fold,
  HomeFilled,
  User,
  Setting
} from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const isCollapsed = ref(false)
const emit = defineEmits<{
  (e: 'update:collapsed', value: boolean): void
}>()

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
  emit('update:collapsed', isCollapsed.value)
}

const menuItems = [
  { key: '/dashboard', title: 'menu.dashboard', icon: HomeFilled, path: '/dashboard' },
  { key: '/blindplates', title: 'menu.blindplates', icon: Document, path: '/blindplates' },
  { key: '/locations', title: 'menu.locations', icon: Location, path: '/locations' },
  { key: '/operations', title: 'menu.operations', icon: List, path: '/operations' },
  { key: '/inspections', title: 'menu.inspections', icon: DataAnalysis, path: '/inspections' },
  { key: '/users', title: 'menu.users', icon: User, path: '/users' },
  { key: '/settings', title: 'menu.settings', icon: Setting, path: '/settings' }
]

const activeMenu = computed(() => route.path)

function navigate(path: string) {
  router.push(path)
}
</script>

<template>
  <el-scrollbar class="app-sidebar">
    <div class="sidebar-header">
      <span v-if="!isCollapsed" class="logo-text">{{ t('app.shortTitle') }}</span>
      <el-button
        :icon="isCollapsed ? Expand : Fold"
        text
        @click="toggleCollapse"
        class="collapse-btn"
      />
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapsed"
      :collapse-transition="false"
      class="sidebar-menu"
      @select="navigate"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.key"
        :index="item.path"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>{{ t(item.title) }}</template>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<style scoped>
.app-sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--brand-border-light);
}
.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--brand-text-primary);
}
.collapse-btn {
  padding: 8px;
}
.sidebar-menu {
  border-right: none;
  flex: 1;
}
.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}
</style>
