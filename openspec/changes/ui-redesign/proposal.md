## Why

当前盲板管理系统使用 Element Plus 默认主题，缺乏品牌标识和一致的视觉语言。页面布局简单（仅有顶部标题栏 + 路由内容），随着功能模块增多（盲板管理、位置、操作、巡检、报表），需要更高效的系统布局（侧边菜单 + 多页签）来提升用户体验。本次 UI 重设计同时建立设计系统和实现新布局，为后续功能迭代打好基础。

## What Changes

### 设计系统引入
- **From**: 无统一设计 token，直接使用 Element Plus 默认变量
- **To**: 定义完整的 CSS 自定义属性设计 token 体系（品牌色、字体、间距、圆角、阴影），覆盖 Element Plus 主题变量
- **Reason**: 建立品牌一致性，提高 UI 开发效率
- **Impact**: non-breaking（新增样式文件，不影响现有功能）

### 系统布局重构
- **From**: 简单 `AppHeader + router-view` 结构，无侧边菜单
- **To**: 标题栏（右上角显示消息、我的菜单）+ 可折叠左侧菜单栏（折叠为垂直工具栏/展开为树形菜单）+ 右侧多页签内容区
- **Reason**: 支持多模块并行操作，提高导航效率
- **Impact**: breaking（路由结构变化，需调整路由配置和页面组件）

### Dashboard 页面
- **From**: `/reports` 路径下的简单统计卡片
- **To**: 登录后默认 `/dashboard` 路径，显示在系统布局内，包含统计概览
- **Reason**: Dashboard 作为系统入口，需要展示在布局中而非独立页面
- **Impact**: non-breaking（新增路由，不影响现有功能）

### 页面组件视觉更新
- **From**: 各页面使用 Element Plus 默认样式，无统一视觉风格
- **To**: 所有页面基于设计系统重新设计颜色、字体、间距、圆角
- **Reason**: 统一品牌视觉，提升用户体验
- **Impact**: non-breaking（仅样式变化，功能不变）

## Capabilities

### New Capabilities
- `system-layout`: 标题栏 + 可折叠左侧菜单 + 多页签内容区的系统布局框架
- `design-tokens`: 完整的 CSS 自定义属性设计 token 体系
- `dashboard-page`: 登录后的仪表盘概览页面

### Modified Capabilities
- `user-auth`: 登录页面视觉重设计，登录后跳转到 `/dashboard` 而非 `/blindplates`
- `blindplate-management`: 列表页面视觉重设计，在系统布局中显示
- `location-management`: 位置页面视觉重设计，在系统布局中显示
- `operation-management`: 操作页面视觉重设计，在系统布局中显示
- `inspection-management`: 巡检页面视觉重设计，在系统布局中显示

## Impact

- **Affected files**: 所有 `.vue` 文件（样式更新）、`App.vue`（布局重构）、`router/index.ts`（路由结构调整）、新增 `src/styles/` 目录（设计 token）
- **Dependencies**: 无新增依赖，现有 Element Plus 2.6.3 已支持 CSS 变量覆盖
- **Migration**: 路由结构从扁平变为嵌套（layout route），需确保导航守卫正确