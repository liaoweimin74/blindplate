<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import {
  Document,
  Location,
  List,
  DataAnalysis,
  HomeFilled,
  User,
  Setting,
  Edit,
  Box,
  Upload,
  Stamp,
  View
} from '@element-plus/icons-vue'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const menuItems = [
{ key: '/dashboard', title: 'menu.dashboard', icon: HomeFilled, path: '/dashboard' },
  { key: '/blindplates', title: 'menu.blindplates', icon: Document, path: '/blindplates' },
  { key: '/stocktakes', title: 'menu.stocktakes', icon: Box, path: '/stocktakes' },
  { key: '/blindplate-editor', title: 'menu.blindplateEditor', icon: Edit, path: '/blindplate-editor' },
  { key: '/locations', title: 'menu.locations', icon: Location, path: '/locations' },
  { key: '/isolation-point-import', title: 'menu.isolationPointImport', icon: Upload, path: '/isolation-point-import' },
  { key: '/change-approval', title: 'menu.changeApproval', icon: Stamp, path: '/change-approval' },
  { key: '/blind-spot-status', title: 'menu.blindSpotStatus', icon: View, path: '/blind-spot-status' },
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
    <el-menu
      :default-active="activeMenu"
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
}
.sidebar-menu {
  border-right: none;
  width: 100%;
}
</style>
