# system-layout Specification

## Purpose
TBD - created by archiving change ui-redesign. Update Purpose after archive.
## Requirements
### Requirement: System layout SHALL consist of header, sidebar, and content area with tabs
系统布局 SHALL 包含三个主要区域：顶部标题栏（Header）、左侧菜单栏（Sidebar）、右侧内容区（Content Area with Tabs）。

#### Scenario: Layout renders after login
- **WHEN** 用户登录成功
- **THEN** 系统 SHALL 显示完整的三栏布局

### Requirement: Header SHALL display logo on left and notifications/user menu on right
顶部标题栏左侧 SHALL 显示系统 Logo/名称，右侧 SHALL 显示消息图标和"我的"菜单（包含账号信息、帮助、关于）。

#### Scenario: Header renders correctly
- **WHEN** 布局渲染
- **THEN** 标题栏左侧 SHALL 显示"盲板管理系统"Logo
- **THEN** 标题栏右侧 SHALL 显示消息按钮和用户菜单

#### Scenario: User menu contains account info, help, about
- **WHEN** 用户点击"我的"菜单
- **THEN** 下拉菜单 SHALL 包含"账号信息"、"帮助"、"关于"选项

### Requirement: Sidebar SHALL be collapsible between expanded tree menu and compact toolbar
左侧菜单栏 SHALL 支持两种状态：展开状态（220px 宽，显示树形菜单）和折叠状态（64px 宽，显示垂直工具栏图标）。

#### Scenario: Sidebar expands to tree menu
- **WHEN** 用户点击展开按钮或菜单在展开状态
- **THEN** 菜单栏宽度 SHALL 为 220px，显示完整菜单文字和图标

#### Scenario: Sidebar collapses to toolbar
- **WHEN** 用户点击折叠按钮
- **THEN** 菜单栏宽度 SHALL 为 64px，仅显示一级菜单图标

### Requirement: Content area SHALL use multi-tab interface
右侧内容区 SHALL 使用多页签（Tab）界面，每个打开的菜单页面对应一个页签。

#### Scenario: Clicking menu item opens a new tab
- **WHEN** 用户点击左侧菜单项
- **THEN** 内容区 SHALL 打开对应页签并显示页面内容

#### Scenario: Tab can be closed
- **WHEN** 用户点击页签上的关闭按钮
- **THEN** 对应的页面组件 SHALL 被销毁

#### Scenario: Clicking existing tab activates it without recreation
- **WHEN** 用户点击已打开的页签
- **THEN** 系统 SHALL 切换到该页签而不重新创建组件

### Requirement: Sidebar SHALL support hierarchical menu items
左侧菜单栏 SHALL 支持多级菜单（树形结构），当展开时显示完整层级。

#### Scenario: Sub-menu items are visible in expanded state
- **WHEN** 菜单栏处于展开状态
- **THEN** 包含子菜单的父菜单项 SHALL 可展开显示子菜单

#### Scenario: Only first-level menu items shown in collapsed state
- **WHEN** 菜单栏处于折叠状态
- **THEN** 仅 SHALL 显示一级菜单图标

---

