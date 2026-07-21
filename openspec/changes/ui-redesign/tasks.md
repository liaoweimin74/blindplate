## 1. 设计系统实现

- [ ] 1.1 创建 `src/styles/design-tokens.css` — 定义所有 CSS 自定义属性（品牌色、字体、间距、圆角、阴影）
- [ ] 1.2 创建 `src/styles/element-overrides.css` — 覆盖 Element Plus CSS 变量以匹配设计系统
- [ ] 1.3 在 `main.ts` 中导入设计系统样式文件
- [ ] 1.4 创建 `src/styles/global.scss` — 全局基础样式（body 字体、滚动条、选中颜色等）

## 2. 系统布局实现

- [ ] 2.1 创建 `src/components/AppLayout.vue` — 系统主布局组件（Header + Sidebar + Content）
- [ ] 2.2 创建 `src/components/AppSidebar.vue` — 可折叠左侧菜单栏（支持展开/折叠、树形菜单）
- [ ] 2.3 创建 `src/components/AppHeader.vue` — 重写顶部标题栏（Logo、消息、用户菜单）
- [ ] 2.4 创建 `src/components/AppTabs.vue` — 多页签组件（打开、切换、关闭、销毁）
- [ ] 2.5 创建 `src/composables/useTabs.ts` — 页签状态管理 composable

## 3. 路由重构

- [ ] 3.1 重写 `src/router/index.ts` — 使用布局路由包裹需要登录的页面
- [ ] 3.2 添加 Dashboard 路由（`/dashboard`）作为默认登录后页面
- [ ] 3.3 更新导航守卫 — 登录后重定向到 `/dashboard`

## 4. App.vue 重构

- [ ] 4.1 重写 `src/App.vue` — 使用 `AppLayout` 组件替代原有简单布局
- [ ] 4.2 登录页面保持独立，不应用系统布局

## 5. 页面组件视觉重设计

- [ ] 5.1 重设计 `src/views/auth/Login.vue` — 使用设计系统品牌色
- [ ] 5.2 重设计 `src/views/report/Dashboard.vue` — 统计卡片 + 加载状态
- [ ] 5.3 重设计 `src/views/blindplate/BlindPlateList.vue`
- [ ] 5.4 重设计 `src/views/location/LocationTree.vue`
- [ ] 5.5 重设计 `src/views/operation/OperationList.vue`
- [ ] 5.6 重设计 `src/views/inspection/InspectionList.vue`

## 6. 验证与清理

- [ ] 6.1 运行 TypeScript 编译检查
- [ ] 6.2 运行项目构建验证
- [ ] 6.3 检查所有页面路由与导航正确性
- [ ] 6.4 清理旧样式文件