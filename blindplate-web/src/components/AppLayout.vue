<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import AppTabs from './AppTabs.vue'
import { useTabs } from '@/composables/useTabs'

const router = useRouter()
const route = useRoute()
const { tabs, addTabFromRoute } = useTabs()

const sidebarVisible = ref(true)
const SIDEBAR_WIDTH = 220

function toggleSidebar() {
  sidebarVisible.value = !sidebarVisible.value
}

const sidebarWidth = computed(() => sidebarVisible.value ? SIDEBAR_WIDTH : 0)

/** keep-alive 缓存列表，关闭标签页时自动移除缓存 */
const cachedTabs = computed(() => tabs.value.map(t => t.key))

router.afterEach((to) => {
  if (to.path !== '/login') {
    addTabFromRoute(to)
  }
})
</script>

<template>
  <div class="app-layout">
    <AppHeader :sidebar-visible="sidebarVisible" @toggle-sidebar="toggleSidebar" />
    <div class="layout-body">
      <el-aside :width="sidebarWidth + 'px'" class="layout-aside">
        <AppSidebar />
      </el-aside>
      <div class="layout-main">
        <AppTabs />
        <el-main class="layout-content">
          <router-view v-slot="{ Component }">
            <keep-alive :include="cachedTabs">
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </router-view>
        </el-main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--brand-bg-page);
}
.layout-body {
  display: flex;
  flex: 1;
  min-height: 0;
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
  flex: 1;
  min-width: 0;
  min-height: 0;
}
.layout-content {
  flex: 1;
  padding: var(--brand-spacing-4);
  background-color: var(--brand-bg-page);
  overflow: auto;
}
</style>
