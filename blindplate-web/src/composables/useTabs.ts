import { ref, computed, type Ref, type Component } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'
import i18n from '@/i18n'

/**
 * 页签结构定义
 */
export interface Tab {
  /** 唯一标识（通常为路由路径） */
  key: string
  /** 显示名称 */
  title: string
  /** i18n key（用于语言切换时动态更新标题） */
  titleKey?: string
  /** Vue 组件 */
  component?: Component
  /** 路由全路径 */
  fullPath: string
  /** 图标名称（可选） */
  icon?: string
  /** 是否可关闭 */
  closable?: boolean
}

// ========== 单例状态（模块级，所有调用共享同一实例） ==========

/** 已打开的页签列表 */
const tabs: Ref<Tab[]> = ref([])

/** 当前激活的页签 key */
const activeKey: Ref<string> = ref('')

// ========== 计算属性 ==========

/** 当前激活的页签对象 */
const activeTab = computed(() => {
  return tabs.value.find(tab => tab.key === activeKey.value)
})

/** 页签数量 */
const tabCount = computed(() => tabs.value.length)

/** 是否有多个页签 */
const hasMultipleTabs = computed(() => tabs.value.length > 1)

// ========== 方法 ==========

/**
 * 添加页签
 * @param tab 页签信息
 * @param activate 是否激活（默认 true）
 */
function addTab(tab: Tab, activate = true): void {
  const existingIndex = tabs.value.findIndex(t => t.key === tab.key)

  if (existingIndex === -1) {
    tabs.value.push({
      ...tab,
      closable: tab.closable !== false
    })
  }

  if (activate) {
    activeKey.value = tab.key
  }
}

/**
 * 从路由添加页签
 * @param route 路由对象
 */
function addTabFromRoute(route: RouteLocationNormalized): void {
  const titleKey = route.meta?.titleKey as string | undefined
  const title = titleKey
    ? i18n.global.t(titleKey)
    : (route.meta?.title as string) || route.name?.toString() || route.path

  const tab: Tab = {
    key: route.path,
    title,
    titleKey,
    fullPath: route.fullPath,
    icon: route.meta?.icon as string,
    closable: route.meta?.closable !== false
  }
  addTab(tab)
}

/**
 * 关闭页签
 * @param key 页签 key
 * @returns 关闭后应该激活的页签 key（如果有）
 */
function closeTab(key: string): string | null {
  const index = tabs.value.findIndex(t => t.key === key)

  if (index === -1) return null
  if (tabs.value[index].closable === false) return null

  const wasActive = activeKey.value === key

  tabs.value.splice(index, 1)

  if (wasActive && tabs.value.length > 0) {
    const newActiveIndex = Math.min(index, tabs.value.length - 1)
    activeKey.value = tabs.value[newActiveIndex].key
    return activeKey.value
  }

  return null
}

/**
 * 关闭其他页签
 */
function closeOtherTabs(key: string): void {
  tabs.value = tabs.value.filter(t => t.key === key || t.closable === false)
  if (!tabs.value.find(t => t.key === activeKey.value)) {
    activeKey.value = key
  }
}

/**
 * 关闭右侧所有页签
 */
function closeRightTabs(key: string): void {
  const index = tabs.value.findIndex(t => t.key === key)
  if (index === -1) return
  tabs.value = tabs.value.filter((t, i) => i <= index || t.closable === false)
  if (!tabs.value.find(t => t.key === activeKey.value)) {
    activeKey.value = key
  }
}

/**
 * 关闭所有可关闭的页签
 */
function closeAllTabs(): void {
  tabs.value = tabs.value.filter(t => t.closable === false)
  if (tabs.value.length > 0) {
    activeKey.value = tabs.value[0].key
  } else {
    activeKey.value = ''
  }
}

/**
 * 切换到指定页签
 */
function switchTab(key: string): void {
  if (tabs.value.find(t => t.key === key)) {
    activeKey.value = key
  }
}

/**
 * 检查页签是否存在
 */
function hasTab(key: string): boolean {
  return tabs.value.some(t => t.key === key)
}

/**
 * 获取页签
 */
function getTab(key: string): Tab | undefined {
  return tabs.value.find(t => t.key === key)
}

/**
 * 清空所有页签
 */
function clearTabs(): void {
  tabs.value = []
  activeKey.value = ''
}

/**
 * 刷新所有页签标题（语言切换时调用）
 */
function refreshTabTitles(): void {
  tabs.value.forEach(tab => {
    if (tab.titleKey) {
      tab.title = i18n.global.t(tab.titleKey)
    }
  })
}

/**
 * useTabs composable（单例模式）
 *
 * 所有组件共享同一份页签状态。
 */
export function useTabs() {
  return {
    tabs,
    activeKey,
    activeTab,
    tabCount,
    hasMultipleTabs,
    addTab,
    addTabFromRoute,
    closeTab,
    closeOtherTabs,
    closeRightTabs,
    closeAllTabs,
    switchTab,
    hasTab,
    getTab,
    clearTabs,
    refreshTabTitles,
  }
}
