## Design Summary

UI 重设计（ui-redesign）变更的目标是为盲板管理系统建立完整的设计系统（品牌色、字体、spacing），然后基于这套设计系统重设计所有页面组件，并实现新的系统布局框架。

### 核心变更

1. **设计系统建立**：定义品牌色板、字体层级、间距规范、阴影等设计 token
2. **系统布局重构**：实现标题栏（顶部）+ 菜单栏（左侧，可折叠）+ 内容区（右侧，多页签）的三栏布局
3. **页面组件重设计**：基于设计系统重写所有现有页面（Login、BlindPlateList、LocationTree、OperationList、InspectionList、Dashboard）
4. **Dashboard 页面**：登录后的默认页面，显示在系统布局内

## Alternatives Considered

### 方案 A：基于 Element Plus 主题定制（推荐）
- **做法**：利用 Element Plus 的 CSS 变量机制覆盖主题色、字体等，结合自定义 SCSS 变量构建设计系统；布局使用 Element Plus 的 Container/Layout 组件
- **优点**：与现有 Element Plus 深度集成，改动最小；主题变量覆盖方便后续维护；布局组件开箱即用
- **缺点**：受限于 Element Plus 的主题变量范围；复杂布局需要额外样式
- **为何未采用**：推荐采用

### 方案 B：完全自定义 CSS 设计系统
- **做法**：完全不依赖 Element Plus 主题，使用 CSS 自定义属性（var）建立完整的设计 token 体系，自己实现所有组件
- **优点**：完全控制设计表现；无框架样式限制
- **缺点**：工作量大；与 Element Plus 组件样式冲突风险；失去 Element Plus 主题一致性
- **为何未采用**：项目已深度使用 Element Plus，完全重写所有组件成本过高且无必要

### 方案 C：引入 Tailwind CSS + 设计系统
- **做法**：引入 Tailwind CSS 作为样式基础设施，配置品牌色 token，结合 Element Plus 使用
- **优点**：设计 token 表达力强；开发效率高
- **缺点**：引入新的构建依赖；与 Element Plus 的样式集成需要额外适配层；学习成本
- **为何未采用**：项目规模不大，引入 Tailwind 带来的收益不足以抵消额外复杂度

## Agreed Approach

采用方案 A（Element Plus 主题定制 + 自定义设计 token）。具体做法：
- 使用 CSS 自定义属性（`--brand-*`）定义设计 token
- 覆盖 Element Plus 的 CSS 变量以保持一致性
- 布局使用 Element Plus 的 `el-container` / `el-aside` / `el-header` / `el-main` 组件组合
- 菜单使用 `el-menu`（支持折叠）
- 页签使用自定义组件或 `el-tabs`

## Key Decisions

1. **设计系统文件位置**：`src/styles/design-tokens.css`（CSS 自定义属性）+ `src/styles/variables.scss`（SCSS 变量，在 Element Plus 变量覆盖前 import）
2. **布局组件结构**：`App.vue` 使用 `el-container` 垂直布局，内部嵌套水平 `el-container`（aside + main）
3. **菜单折叠机制**：使用 `el-menu` 的 `collapse` 属性，左侧宽度在折叠/展开时切换
4. **多页签实现**：使用 `el-tabs` + `keep-alive` 结合动态组件实现，关闭页签时销毁对应组件实例
5. **Dashboard 路径**：登录后默认路由改为 `/dashboard`，显示在布局内
6. **路由重构**：需要登录的页面使用布局路由包裹（`layout` route with children）

## Open Questions

1. 菜单数据结构是否从后端 API 获取，还是前端静态配置？
2. 页签关闭后是否需要保留滚动位置或其他状态？
3. 设计系统品牌色是否有现成的品牌指南参考？