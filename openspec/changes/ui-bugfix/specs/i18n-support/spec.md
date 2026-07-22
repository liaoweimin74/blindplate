## ADDED Requirements

### Requirement: UI text SHALL be managed via vue-i18n
所有前端界面文本 SHALL 通过 vue-i18n 的 `$t()` 函数管理，不得硬编码中英文混杂文本。

#### Scenario: Page titles use i18n
- **WHEN** 渲染任何页面标题
- **THEN** 标题文本 SHALL 通过 `$t('page.title')` 获取

#### Scenario: Button labels use i18n
- **WHEN** 渲染任何按钮
- **THEN** 按钮文本 SHALL 通过 `$t('button.label')` 获取

#### Scenario: Table headers use i18n
- **WHEN** 渲染表格列头
- **THEN** 列头文本 SHALL 通过 `$t('table.header')` 获取

#### Scenario: Confirm dialogs use i18n
- **WHEN** 显示确认对话框
- **THEN** 对话框标题和内容 SHALL 通过 `$t()` 获取

### Requirement: System SHALL support Chinese and English
系统 SHALL 支持中文（zh-CN）和英文（en）两种语言，默认语言为中文。

#### Scenario: Default language is Chinese
- **WHEN** 首次加载应用
- **THEN** 界面语言 SHALL 为中文

#### Scenario: Language can be switched from Profile menu
- **WHEN** 用户点击 Profile 菜单中的语言选项
- **THEN** 界面语言 SHALL 立即切换

#### Scenario: Language preference is persisted
- **WHEN** 用户切换语言后刷新页面
- **THEN** 界面语言 SHALL 保持为用户选择的语言

### Requirement: Language preference SHALL be stored in localStorage
用户的语言偏好 SHALL 存储在 localStorage 中，key 为 `language`。

#### Scenario: Language is loaded from localStorage on app start
- **WHEN** 应用启动
- **THEN** SHALL 从 localStorage 读取语言偏好

### Requirement: Collapsed sidebar SHALL reduce layout width
左侧菜单折叠后，布局中 sidebar 占据的宽度 SHALL 从 220px 减少到 64px。

#### Scenario: Sidebar width changes on collapse
- **WHEN** 用户点击折叠按钮
- **THEN** sidebar 宽度 SHALL 从 220px 变为 64px

#### Scenario: Content area expands when sidebar collapses
- **WHEN** 侧边栏折叠
- **THEN** 内容区宽度 SHALL 自动扩展填充释放的空间

### Requirement: User management page layout SHALL be consistent with other pages
用户管理页面 SHALL 使用与其他页面相同的布局结构（page-container + page-header + content-card）。

#### Scenario: UserList uses same structure as BlindPlateList
- **WHEN** 渲染用户管理页面
- **THEN** 页面结构 SHALL 与 BlindPlateList 一致

### Requirement: Add User button SHALL be right-aligned with search bar
用户管理页面的 Add User 按钮 SHALL 与搜索框在同一行，靠右对齐。

#### Scenario: Button and search are in same row
- **WHEN** 渲染用户管理页面
- **THEN** Add User 按钮 SHALL 在搜索框同一行的右侧