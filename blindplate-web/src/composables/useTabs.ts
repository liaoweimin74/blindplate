import { ref, computed, type Ref, type Component } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'

/**
 * 页签结构定义
 */
export interface Tab {
  /** 唯一标识（通常为路由路径） */
  key: string
  /** 显示名称 */
  title: string
  /** Vue 组件 */
  component?: Component
  /** 路由全路径 */
  fullPath: string
  /** 图标名称（可选） */
  icon?: string
  /** 是否可关闭 */
  closable?: boolean
}

/**
 * useTabs composable
 * 
 * 管理已打开页签列表、当前激活页签、添加/关闭/切换页签方法。
 * 用于 AppTabs 组件的状态管理。
 */
export function useTabs() {
  // ========== 状态 ==========
  
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
    // 检查是否已存在
    const existingIndex = tabs.value.findIndex(t => t.key === tab.key)
    
    if (existingIndex === -1) {
      // 不存在则添加
      tabs.value.push({
        ...tab,
        closable: tab.closable !== false // 默认可关闭
      })
    }
    
    // 激活页签
    if (activate) {
      activeKey.value = tab.key
    }
  }

  /**
   * 从路由添加页签
   * @param route 路由对象
   */
  function addTabFromRoute(route: RouteLocationNormalized): void {
    const tab: Tab = {
      key: route.path,
      title: (route.meta?.title as string) || route.name?.toString() || route.path,
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
    
    if (index === -1) {
      return null
    }

    // 不允许关闭不可关闭的页签
    if (tabs.value[index].closable === false) {
      return null
    }

    // 记录关闭前是否激活
    const wasActive = activeKey.value === key
    
    // 移除页签
    tabs.value.splice(index, 1)

    // 如果关闭的是当前激活页签，需要切换到其他页签
    if (wasActive && tabs.value.length > 0) {
      // 优先切换到右侧页签，否则切换到左侧
      const newActiveIndex = Math.min(index, tabs.value.length - 1)
      activeKey.value = tabs.value[newActiveIndex].key
      return activeKey.value
    }

    return null
  }

  /**
   * 关闭其他页签
   * @param key 保留的页签 key
   */
  function closeOtherTabs(key: string): void {
    tabs.value = tabs.value.filter(t => t.key === key || t.closable === false)
    
    // 如果当前激活的页签被关闭，切换到保留的页签
    if (!tabs.value.find(t => t.key === activeKey.value)) {
      activeKey.value = key
    }
  }

  /**
   * 关闭右侧所有页签
   * @param key 起始页签 key
   */
  function closeRightTabs(key: string): void {
    const index = tabs.value.findIndex(t => t.key === key)
    if (index === -1) return

    // 保留当前页签及左侧的页签
    tabs.value = tabs.value.filter((t, i) => i <= index || t.closable === false)
    
    // 如果当前激活的页签被关闭，切换到起始页签
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
   * @param key 页签 key
   */
  function switchTab(key: string): void {
    if (tabs.value.find(t => t.key === key)) {
      activeKey.value = key
    }
  }

  /**
   * 检查页签是否存在
   * @param key 页签 key
   */
  function hasTab(key: string): boolean {
    return tabs.value.some(t => t.key === key)
  }

  /**
   * 获取页签
   * @param key 页签 key
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

  return {
    // 状态
    tabs,
    activeKey,
    
    // 计算属性
    activeTab,
    tabCount,
    hasMultipleTabs,
    
    // 方法
    addTab,
    addTabFromRoute,
    closeTab,
    closeOtherTabs,
    closeRightTabs,
    closeAllTabs,
    switchTab,
    hasTab,
    getTab,
    clearTabs
  }
}

// 导出单例实例（可选）
export const tabsManager = useTabs()