# UI Bugfix Implementation Plan

**Goal:** 修复 4 个 UI 问题：中英文混杂、折叠菜单占位、按钮对齐、页面布局不一致

**Architecture:** 安装 vue-i18n → 创建中英文语言包 → 替换所有硬编码文本 → 修复 AppLayout 侧边栏宽度响应 → 统一页面布局结构

**Tech Stack:** Vue 3 + TypeScript + Element Plus + vue-i18n v9

---

## Task 1: i18n 基础设施

- [ ] **Step 1:** 安装 vue-i18n: `npm install vue-i18n@9`
- [ ] **Step 2:** 创建 `src/locales/zh-CN.json` 和 `src/locales/en.json`
- [ ] **Step 3:** 创建 `src/i18n/index.ts` 初始化 i18n
- [ ] **Step 4:** 在 `main.ts` 中 `app.use(i18n)`

## Task 2: 语言切换

- [ ] **Step 1:** 在 AppHeader Profile 下拉菜单添加中英文切换选项
- [ ] **Step 2:** 切换时更新 locale 并保存到 localStorage

## Task 3: 折叠菜单宽度修复

- [ ] **Step 1:** AppSidebar 添加 `update:collapsed` emit
- [ ] **Step 2:** AppLayout 监听折叠状态，动态调整 sidebarWidth

## Task 4: 页面布局统一

- [ ] **Step 1:** 从 UserList.vue 移除 `@import '@/styles/global.scss'`
- [ ] **Step 2:** 从 Settings.vue 移除 `@import '@/styles/global.scss'`

## Task 5: 文本替换（所有 .vue 文件）

- [ ] **Step 1-12:** 逐个替换所有页面和组件的硬编码文本为 `$t()`

## Task 6: 验证

- [ ] **Step 1:** `npm run build` 构建验证