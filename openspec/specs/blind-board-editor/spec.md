# blind-board-editor Specification

## Purpose
盲板组态编辑器允许用户通过拖拽 SVG 符号到画布上，可视化设计盲板拓扑图。基于 maotu-webtopo 组件库构建，嵌入现有页签系统，并增加图层面板功能。

## Requirements

### Requirement: Editor accessed via sidebar menu
系统左侧菜单栏 SHALL 包含"盲板组态"菜单项，点击后在内容区页签中打开编辑器页面。

#### Scenario: Click menu opens editor tab
- **WHEN** 用户点击左侧菜单栏的"盲板组态"按钮
- **THEN** 系统在内容区打开一个新的页签
- **THEN** 页签标题为"盲板组态"
- **THEN** 页签内显示盲板组态编辑器

#### Scenario: Editor tab is closable
- **WHEN** 用户点击编辑器页签的关闭按钮
- **THEN** 对应的编辑器组件 SHALL 被销毁

### Requirement: Editor uses MtEdit component
编辑器 SHALL 基于 maotu-webtopo 的 MtEdit 组件构建，提供完整的组态编辑功能。

#### Scenario: MtEdit renders with full features
- **WHEN** 编辑器页面加载
- **THEN** MtEdit 组件 SHALL 渲染，包含左侧元件库、画布区域
- **THEN** 用户 SHALL 能拖拽 SVG 符号到画布上
- **THEN** 用户 SHALL 能缩放、平移画布

#### Scenario: SVG symbols are registered
- **WHEN** 编辑器初始化
- **THEN** 系统 SHALL 注册盲板相关的 SVG 符号到 MtEdit 的元件库

### Requirement: Custom toolbar above editor
编辑器顶部 SHALL 显示自定义工具栏，包含保存、预览、图层面板切换等操作按钮。

#### Scenario: Toolbar renders above MtEdit
- **WHEN** 编辑器页面加载
- **THEN** 顶部工具栏 SHALL 显示保存、预览、图层切换按钮
- **THEN** 工具栏 SHALL 在 MtEdit 组件外部、编辑器内容区顶部

#### Scenario: Save button triggers export
- **WHEN** 用户点击保存按钮
- **THEN** MtEdit SHALL 导出当前画布 JSON
- **THEN** 系统 SHALL 将 JSON 保存到后端

#### Scenario: Preview button opens preview tab
- **WHEN** 用户点击预览按钮
- **THEN** 系统 SHALL 打开预览页签（或新路由）
- **THEN** 预览页签 SHALL 使用 MtPreview 组件只读显示当前画布

### Requirement: Layer panel shows canvas elements
编辑器 SHALL 提供图层面板，显示画布中所有 SVG 元素，支持可见性和锁定控制。

#### Scenario: Layer toggle opens drawer
- **WHEN** 用户点击工具栏的图层按钮
- **THEN** 右侧 SHALL 滑出 el-drawer 图层面板
- **THEN** 图层面板 SHALL 显示画布中所有 SVG 元素列表

#### Scenario: Toggle element visibility
- **WHEN** 用户在图层中点击元素的可见性图标
- **THEN** 对应元素在画布上 SHALL 隐藏或显示

#### Scenario: Toggle element lock
- **WHEN** 用户在图层中点击元素的锁定图标
- **THEN** 对应元素在画布上 SHALL 被锁定或解锁（锁定后不可拖拽）

#### Scenario: Click layer item selects element
- **WHEN** 用户在图层中点击元素名称
- **THEN** 画布上对应元素 SHALL 被选中并高亮

### Requirement: Preview page renders read-only
预览页面 SHALL 使用 MtPreview 组件只读显示已保存的拓扑图。

#### Scenario: Preview loads saved project
- **WHEN** 用户访问预览路由
- **THEN** 系统 SHALL 从后端加载对应项目的 JSON 数据
- **THEN** MtPreview 组件 SHALL 只读渲染拓扑图

#### Scenario: Preview has no editing controls
- **WHEN** 预览页面显示
- **THEN** 画布上 SHALL 无编辑控制点、拖拽、添加功能

### Requirement: Board projects CRUD via API
系统 SHALL 提供后端 API 支持盲板组态项目的增删改查。

#### Scenario: Create project
- **WHEN** 用户保存新的组态图
- **THEN** 系统 POST 到 `/api/blindboard/projects`
- **THEN** 后端 SHALL 创建项目记录并返回 ID

#### Scenario: List projects
- **WHEN** 用户请求项目列表
- **THEN** 系统 GET `/api/blindboard/projects`
- **THEN** 后端 SHALL 返回所有项目摘要（不含完整 JSON）

#### Scenario: Get single project
- **WHEN** 用户打开或预览特定项目
- **THEN** 系统 GET `/api/blindboard/projects/:id`
- **THEN** 后端 SHALL 返回项目完整 JSON 数据

#### Scenario: Update project
- **WHEN** 用户保存已有项目的修改
- **THEN** 系统 PUT `/api/blindboard/projects/:id`
- **THEN** 后端 SHALL 更新项目记录

#### Scenario: Delete project
- **WHEN** 用户删除项目
- **THEN** 系统 DELETE `/api/blindboard/projects/:id`
- **THEN** 后端 SHALL 删除项目记录