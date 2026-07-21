<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
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

const router = useRouter()
const route = useRoute()

const isCollapsed = ref(false)

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
}

const menuItems = [
  { key: '/dashboard', title: '首页', icon: HomeFilled, path: '/dashboard' },
  { key: '/blindplates', title: '盲板管理', icon: Document, path: '/blindplates' },
  { key: '/locations', title: '位置管理', icon: Location, path: '/locations' },
  { key: '/operations', title: '操作记录', icon: List, path: '/operations' },
  { key: '/inspections', title: '巡检记录', icon: DataAnalysis, path: '/inspections' },
  { key: '/users', title: '用户管理', icon: User, path: '/users' },
  { key: '/settings', title: '系统设置', icon: Setting, path: '/settings' }
]

const activeMenu = computed(() => route.path)

function navigate(path: string) {
  router.push(path)
}
</script>

<template>
  <el-scrollbar class="app-sidebar">
    <div class="sidebar-header">
      <span v-if="!isCollapsed" class="logo-text">盲板系统</span>
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
        <template #title>{{ item.title }}</template>
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
