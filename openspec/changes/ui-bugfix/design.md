## Context

盲板管理系统经过 UI 重设计后，界面存在中英文混杂问题（菜单中文、按钮英文、提示英文），且有两个布局 Bug（折叠菜单占位、系统页面布局不一致）。需要统一语言并修复布局问题。

## Goals / Non-Goals

**Goals:**
- 引入 vue-i18n 实现前端国际化
- 创建中文和英文语言包
- 所有界面文本统一通过 i18n 管理
- 用户可通过 Profile 下拉菜单切换语言
- 默认语言为中文
- 修复折叠菜单折叠后仍占 220px 宽度的问题
- 修复 UserList 和 Settings 页面布局与其他页面不一致的问题
- UserList 页面 Add User 按钮与搜索框同行靠右对齐

**Non-Goals:**
- 不修改后端多语言支持
- 不涉及后端 API 变更
- 不添加新功能

## Decisions

### 1. i18n 架构
- 使用 vue-i18n v9
- 语言包文件：`src/locales/zh-CN.json`、`src/locales/en.json`
- 初始化：`src/i18n/index.ts`
- 挂载到 Vue app 实例

### 2. 语言切换
- 在 AppHeader 的 Profile 下拉菜单中增加"语言/Language"选项
- 切换后立即生效，不刷新页面
- 语言偏好保存到 localStorage

### 3. 折叠菜单 Bug 修复
- AppSidebar 通过 emit 向 AppLayout 通知当前折叠状态
- 折叠时 sidebarWidth = 64px，展开时 = 220px

### 4. 页面布局统一
- UserList.vue 和 Settings.vue 移除 `@import '@/styles/global.scss'`（已全局引入）
- 确认页面结构与其他页面一致（page-container > page-header + content-card）

## Risks / Trade-offs
1. vue-i18n 需要遍历所有 .vue 文件替换硬编码文本，工作量大但机械
2. 部分按钮文本在 Element Plus 组件内部，需通过 `el-config-provider` 覆盖