<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Close } from '@element-plus/icons-vue'
import { useTabs } from '@/composables/useTabs'

const router = useRouter()
const route = useRoute()
const { tabs, activeKey, closeTab } = useTabs()

const activeTabName = computed({
  get: () => activeKey.value,
  set: (val: string) => {
    router.push(val)
  }
})

function handleTabClick(tabName: string) {
  router.push(tabName)
}

function handleTabRemove(targetKey: string) {
  const newActiveKey = closeTab(targetKey)
  if (newActiveKey && newActiveKey !== route.path) {
    router.push(newActiveKey)
  }
}

function isClosable(key: string): boolean {
  const tab = tabs.value.find(t => t.key === key)
  return tab?.closable !== false
}
</script>

<template>
  <div class="app-tabs">
    <el-tabs
      v-model="activeTabName"
      type="card"
      class="tabs-container"
      @tab-click="(pane: any) => handleTabClick(pane.paneName as string)"
      @tab-remove="handleTabRemove"
    >
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.key"
        :label="tab.title"
        :name="tab.key"
      >
        <template #label>
          <span class="tab-label">
            {{ tab.title }}
            <el-icon
              v-if="isClosable(tab.key)"
              class="close-icon"
              @click.stop="handleTabRemove(tab.key)"
            >
              <Close />
            </el-icon>
          </span>
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.app-tabs {
  background-color: var(--brand-bg-white);
  border-bottom: 1px solid var(--brand-border-light);
  height: var(--brand-tabs-height);
  display: flex;
  align-items: flex-end;
  padding: 0 var(--brand-spacing-4);
}
.tabs-container {
  width: 100%;
}
:deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none;
}
:deep(.el-tabs__nav) {
  border: none;
}
:deep(.el-tabs__nav-wrap::after) {
  display: none;
}
:deep(.el-tabs__item) {
  height: 36px;
  line-height: 36px;
  border: 1px solid var(--brand-border-light);
  border-radius: var(--brand-radius-md) var(--brand-radius-md) 0 0;
  margin-right: var(--brand-spacing-1);
  background-color: var(--brand-bg-page);
  color: var(--brand-text-secondary);
  padding: 0 var(--brand-spacing-4);
  transition: all 0.2s ease;
}
:deep(.el-tabs__item:hover) {
  color: var(--brand-color-primary);
}
:deep(.el-tabs__item.is-active) {
  background-color: var(--brand-bg-white);
  color: var(--brand-color-primary);
  border-bottom-color: var(--brand-bg-white);
}
:deep(.el-tabs__item .is-icon-close) {
  margin-left: var(--brand-spacing-2);
}
.tab-label {
  display: flex;
  align-items: center;
  gap: var(--brand-spacing-2);
}
.close-icon {
  font-size: 12px;
  opacity: 0.6;
  transition: opacity 0.2s ease;
}
.close-icon:hover {
  opacity: 1;
}
</style>