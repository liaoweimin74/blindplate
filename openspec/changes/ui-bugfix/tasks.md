## 1. i18n 基础设施搭建

- [ ] 1.1 安装 vue-i18n 依赖
- [ ] 1.2 创建 `src/locales/zh-CN.json` — 中文语言包
- [ ] 1.3 创建 `src/locales/en.json` — 英文语言包
- [ ] 1.4 创建 `src/i18n/index.ts` — i18n 初始化和配置
- [ ] 1.5 在 `main.ts` 中挂载 i18n

## 2. 语言切换功能

- [ ] 2.1 在 AppHeader Profile 下拉菜单中添加语言切换选项
- [ ] 2.2 实现语言切换逻辑（更新 locale + 保存到 localStorage）

## 3. 折叠菜单宽度修复

- [ ] 3.1 AppSidebar 添加 emit 事件通知折叠状态变化
- [ ] 3.2 AppLayout 根据折叠状态动态调整 sidebarWidth

## 4. 页面布局统一

- [ ] 4.1 修复 UserList.vue 布局（移除多余 `@import`，确认结构一致）
- [ ] 4.2 修复 Settings.vue 布局（移除多余 `@import`，确认结构一致）

## 5. 文本替换

- [ ] 5.1 替换 AppSidebar.vue 菜单文本为 `$t()`
- [ ] 5.2 替换 AppHeader.vue 文本为 `$t()`
- [ ] 5.3 替换 AppTabs.vue 文本为 `$t()`
- [ ] 5.4 替换 Login.vue 文本为 `$t()`
- [ ] 5.5 替换 Dashboard.vue 文本为 `$t()`
- [ ] 5.6 替换 BlindPlateList.vue 文本为 `$t()`
- [ ] 5.7 替换 LocationTree.vue 文本为 `$t()`
- [ ] 5.8 替换 OperationList.vue 文本为 `$t()`
- [ ] 5.9 替换 InspectionList.vue 文本为 `$t()`
- [ ] 5.10 替换 UserList.vue 文本为 `$t()`
- [ ] 5.11 替换 Settings.vue 文本为 `$t()`
- [ ] 5.12 替换表单组件文本为 `$t()`

## 6. 验证

- [ ] 6.1 运行 TypeScript 编译检查
- [ ] 6.2 运行项目构建验证