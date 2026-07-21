## ADDED Requirements

### Requirement: Dashboard SHALL be the default page after login
用户登录后 SHALL 自动跳转到 Dashboard 页面，Dashboard SHALL 是登录后默认显示的页面。

#### Scenario: Redirect to dashboard after login
- **WHEN** 用户登录成功
- **THEN** 系统 SHALL 跳转到 `/dashboard` 路径

#### Scenario: Dashboard is rendered in system layout
- **WHEN** 用户访问 `/dashboard`
- **THEN** Dashboard 内容 SHALL 显示在系统布局的内容区（带页签）

### Requirement: Dashboard SHALL display statistical overview cards
Dashboard SHALL 显示统计概览卡片，包括盲板总数、工单总数、巡检计划数等核心指标。

#### Scenario: Statistics cards are displayed
- **WHEN** Dashboard 页面加载
- **THEN** 页面 SHALL 显示至少 3 个统计卡片（盲板总数、工单总数、巡检计划数）

#### Scenario: Statistics data is fetched from API
- **WHEN** Dashboard 页面加载
- **THEN** 系统 SHALL 调用 `/api/v1/reports/statistics` 获取统计数据

### Requirement: Dashboard SHALL display data in loading state while fetching
Dashboard 页面在数据加载过程中 SHALL 显示加载状态。

#### Scenario: Loading skeleton is shown
- **WHEN** Dashboard 页面正在加载数据
- **THEN** 统计卡片 SHALL 显示骨架屏或加载动画

#### Scenario: Error state is handled
- **WHEN** 数据请求失败
- **THEN** Dashboard SHALL 显示错误提示，而不是空白页面

---

## MODIFIED Requirements

<!-- No existing specs to modify -->

---

## REMOVED Requirements

<!-- No requirements to remove -->

---

## RENAMED Requirements

<!-- No requirements to rename -->