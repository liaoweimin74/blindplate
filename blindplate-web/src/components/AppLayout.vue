<script setup lang="ts">
import { ref, onMounted, provide } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import AppTabs from './AppTabs.vue'
import { useTabs } from '@/composables/useTabs'

const router = useRouter()
const route = useRoute()
const { addTabFromRoute } = useTabs()

const sidebarWidth = ref(220)

provide('tabsManager', useTabs)

onMounted(() => {
  if (route.path !== '/login') {
    addTabFromRoute(route)
  }
})

router.afterEach((to) => {
  if (to.path !== '/login') {
    addTabFromRoute(to)
  }
})
</script>

<template>
  <el-container class="app-layout">
    <el-aside :width="sidebarWidth + 'px'" class="layout-aside">
      <AppSidebar />
    </el-aside>
    <el-container class="layout-main">
      <AppHeader />
      <AppTabs />
      <el-main class="layout-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-layout {
  height: 100vh;
  background-color: var(--brand-bg-page);
}
.layout-aside {
  background-color: var(--brand-bg-white);
  border-right: 1px solid var(--brand-border-light);
  transition: width 0.3s ease;
  overflow: hidden;
}
.layout-main {
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.layout-content {
  flex: 1;
  padding: var(--brand-spacing-4);
  background-color: var(--brand-bg-page);
  overflow: auto;
}
</style>
