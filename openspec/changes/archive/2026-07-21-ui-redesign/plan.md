# UI Redesign Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development
> to implement this plan task-by-task.

**Goal:** 为盲板管理系统建立完整设计系统，实现三栏布局框架（标题栏 + 可折叠菜单 + 多页签内容区），并基于设计系统重设计所有页面组件。

**Architecture:** 使用 CSS 自定义属性构建设计 token → 覆盖 Element Plus 主题变量 → 重构路由为嵌套布局路由 → 实现 `AppLayout`（Header + Sidebar + Tabs）→ 逐个重设计页面组件。设计系统层与布局层分离，布局层与页面层通过路由嵌套解耦。

**Tech Stack:** Vue 3 + TypeScript + Element Plus 2.6 + Pinia + Vue Router 4

---

## Task 1: 设计系统基础文件

- [ ] **Step 1:** 创建 `src/styles/design-tokens.css` — 定义完整的 `--brand-*` CSS 自定义属性（品牌色、中性色、字体、间距、圆角、阴影）
- [ ] **Step 2:** 创建 `src/styles/element-overrides.css` — 将 Element Plus 的 `--el-*` CSS 变量映射到设计 token
- [ ] **Step 3:** 创建 `src/styles/global.scss` — 全局基础样式（字体、滚动条样式、body 背景色）
- [ ] **Step 4:** 在 `main.ts` 中按顺序导入设计系统样式（tokens → element-overrides → global）

## Task 2: 页签状态管理（useTabs composable）

- [ ] **Step 1:** 创建 `src/composables/useTabs.ts` — 管理已打开页签列表、当前激活页签、添加/关闭/切换页签方法
- [ ] **Step 2:** 每个页签存储 `{ key, title, component, icon }` 结构
- [ ] **Step 3:** 关闭页签时从列表中移除并销毁对应组件实例

## Task 3: 系统布局组件

- [ ] **Step 1:** 创建 `src/components/AppSidebar.vue` — 使用 `el-menu` 实现可折叠侧边栏，折叠时显示一级菜单图标，展开时显示完整树形菜单
- [ ] **Step 2:** 重写 `src/components/AppHeader.vue` — 左侧 Logo，右侧消息图标 + 用户下拉菜单（账号信息/帮助/关于）
- [ ] **Step 3:** 创建 `src/components/AppTabs.vue` — 使用 `el-tabs` 实现多页签，关闭页签时触发组件销毁
- [ ] **Step 4:** 创建 `src/components/AppLayout.vue` — 组合 Header + Sidebar + Tabs + router-view

## Task 4: 路由重构

- [ ] **Step 1:** 重写 `src/router/index.ts` — 添加 layout 路由（path: '/'），将需要登录的页面作为其 children
- [ ] **Step 2:** 添加 Dashboard 路由（path: 'dashboard'）作为 layout 的子路由
- [ ] **Step 3:** 更新导航守卫：登录后跳转到 `/dashboard`

## Task 5: App.vue 重构

- [ ] **Step 1:** 重写 `src/App.vue` — 登录页使用独立布局，登录后页面使用 AppLayout

## Task 6: 登录页面重设计

- [ ] **Step 1:** 重写 `src/views/auth/Login.vue` — 居中卡片式设计，应用品牌色，美化表单样式

## Task 7: Dashboard 页面重设计

- [ ] **Step 1:** 重写 `src/views/report/Dashboard.vue` — 统计卡片 + 骨架屏加载状态 + 错误处理

## Task 8: 业务页面视觉更新

- [ ] **Step 1:** 更新 `src/views/blindplate/BlindPlateList.vue` — 应用设计系统样式
- [ ] **Step 2:** 更新 `src/views/location/LocationTree.vue` — 应用设计系统样式
- [ ] **Step 3:** 更新 `src/views/operation/OperationList.vue` — 应用设计系统样式
- [ ] **Step 4:** 更新 `src/views/inspection/InspectionList.vue` — 应用设计系统样式

## Task 9: 验证

- [ ] **Step 1:** 运行 TypeScript 编译检查（vue-tsc）
- [ ] **Step 2:** 运行项目构建（vite build）
- [ ] **Step 3:** 检查路由和布局功能完整性