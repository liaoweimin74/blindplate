## ADDED Requirements

### Requirement: Design tokens SHALL be defined as CSS custom properties
系统 SHALL 使用 CSS 自定义属性（`--brand-*`）定义所有设计 token，包括品牌色、字体、间距、圆角、阴影。

#### Scenario: Design tokens are accessible at runtime
- **WHEN** 页面加载完成
- **THEN** `document.documentElement` 上 SHALL 包含所有 `--brand-*` CSS 自定义属性

#### Scenario: Element Plus theme is overridden by design tokens
- **WHEN** 应用启动
- **THEN** Element Plus 的 CSS 变量（`--el-color-primary` 等）SHALL 被设计 token 覆盖

### Requirement: Brand color palette SHALL include primary, success, warning, danger, info colors
系统 SHALL 定义至少 5 个品牌色：主色（`--brand-color-primary`）、成功色（`--brand-color-success`）、警告色（`--brand-color-warning`）、危险色（`--brand-color-danger`）、信息色（`--brand-color-info`）。

#### Scenario: Brand colors are applied to UI components
- **WHEN** 渲染按钮、标签、链接等组件
- **THEN** 组件颜色 SHALL 使用对应的品牌色 token

### Requirement: Typography scale SHALL define at least 6 font sizes
系统 SHALL 定义字体大小层级：xs（12px）、sm（13px）、base（14px）、lg（16px）、xl（18px）、2xl（20px）及以上。

#### Scenario: Text uses correct font size
- **WHEN** 渲染不同层级的文本元素
- **THEN** 文本 SHALL 使用对应层级的 `--brand-font-size-*` token

### Requirement: Spacing system SHALL use 4px base unit
系统 SHALL 以 4px 为基准单位定义间距 token：`--brand-spacing-1`（4px）到 `--brand-spacing-12`（48px）。

#### Scenario: Layout elements use spacing tokens
- **WHEN** 设置元素间距、内边距或外边距
- **THEN** 间距值 SHALL 使用 `--brand-spacing-*` token

### Requirement: Border radius and shadow tokens SHALL be defined
系统 SHALL 定义圆角 token（`--brand-radius-sm/md/lg/xl`）和阴影 token（`--brand-shadow-sm/md/lg`）。

#### Scenario: Cards use standard border radius and shadow
- **WHEN** 渲染卡片或容器组件
- **THEN** 卡片 SHALL 使用 `--brand-radius-md` 和 `--brand-shadow-md`

---

## MODIFIED Requirements

<!-- No existing specs to modify, all requirements are new -->

---

## REMOVED Requirements

<!-- No requirements to remove -->

---

## RENAMED Requirements

<!-- No requirements to rename -->