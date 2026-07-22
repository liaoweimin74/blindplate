## Why

当前 UI 存在中英文混杂问题（菜单中文、按钮英文、提示英文），影响专业度。同时有两个布局 Bug：折叠菜单折叠后仍占位、系统管理页面布局与其他页面不一致。需统一语言并修复布局。

## What Changes

### 引入 vue-i18n 国际化
- **From**: 硬编码中英文混杂文本
- **To**: 统一通过 vue-i18n 管理，默认中文
- **Reason**: 统一界面语言，提升专业度
- **Impact**: non-breaking

### 折叠菜单宽度修复
- **From**: 折叠后 sidebar 仍占 220px
- **To**: 折叠时 sidebar 宽度变为 64px
- **Reason**: 折叠后布局应释放空间
- **Impact**: bug fix

### 系统页面布局统一
- **From**: UserList/Settings 使用 `@import '@/styles/global.scss'` 与其他页面不同
- **To**: 移除多余 import，统一布局结构
- **Reason**: 统一页面布局模式
- **Impact**: non-breaking

## Capabilities

### New Capabilities
- `i18n-support`: 前端国际化支持，中英文语言包

### Modified Capabilities
- `system-layout`: 折叠菜单宽度动态响应
- `user-management`: 页面布局统一，按钮对齐修复
- `system-settings`: 页面布局统一

## Impact
- 安装 vue-i18n 依赖
- 所有 .vue 文件中的硬编码文本需替换为 `$t()` 调用