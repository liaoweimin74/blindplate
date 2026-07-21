## Context

盲板管理系统（Blind Plate Management System）是一个基于 Vue 3 + Element Plus + TypeScript 的企业级管理后台。当前 UI 使用 Element Plus 默认主题，缺乏品牌一致性。本次变更旨在建立完整的设计系统，并基于此重设计所有页面。

## Goals / Non-Goals

**Goals:**
- 定义品牌色板（主色、辅助色、中性色、功能色）
- 定义字体层级与排版规范
- 定义间距体系（4px 基准网格）
- 定义阴影与圆角规范
- 基于设计 token 重写全局样式
- 实现标题栏 + 可折叠菜单栏 + 多页签内容区的系统布局
- 重写所有现有页面组件，使其符合设计系统
- 实现 Dashboard 作为登录后默认页面

**Non-Goals:**
- 不修改后端 API
- 不改变业务逻辑
- 不添加新的业务功能
- 不替换 Element Plus 组件库

## Decisions

### 1. 设计 Token 架构

采用 CSS 自定义属性（`--brand-*`）作为设计 token 的运行时层，SCSS 变量作为编译时层。

**Token 分类：**
- `--brand-color-*`：品牌色
- `--brand-text-*`：文字色
- `--brand-bg-*`：背景色
- `--brand-border-*`：边框色
- `--brand-font-*`：字体
- `--brand-spacing-*`：间距
- `--brand-radius-*`：圆角
- `--brand-shadow-*`：阴影

### 2. 品牌色板

| Token | 值 | 用途 |
|---|---|---|
| `--brand-color-primary` | `#1a73e8` | 主色、按钮、链接 |
| `--brand-color-primary-light` | `#4a90d9` | hover 状态 |
| `--brand-color-primary-dark` | `#1557b0` | active 状态 |
| `--brand-color-success` | `#34a853` | 成功/通过 |
| `--brand-color-warning` | `#fbbc04` | 警告 |
| `--brand-color-danger` | `#ea4335` | 危险/错误 |
| `--brand-color-info` | `#5f6368` | 信息/次要 |

### 3. 中性色

| Token | 值 | 用途 |
|---|---|---|
| `--brand-text-primary` | `#202124` | 主要文字 |
| `--brand-text-secondary` | `#5f6368` | 次要文字 |
| `--brand-text-disabled` | `#9aa0a6` | 禁用文字 |
| `--brand-bg-page` | `#f8f9fa` | 页面背景 |
| `--brand-bg-white` | `#ffffff` | 卡片/容器背景 |
| `--brand-bg-hover` | `#f1f3f4` | hover 背景 |
| `--brand-border` | `#dadce0` | 边框 |
| `--brand-border-light` | `#e8eaed` | 浅边框 |

### 4. 字体

| Token | 值 | 用途 |
|---|---|---|
| `--brand-font-family` | `'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif` | 正文字体 |
| `--brand-font-mono` | `'JetBrains Mono', 'Fira Code', monospace` | 等宽字体 |
| `--brand-font-size-xs` | `12px` | 辅助文字 |
| `--brand-font-size-sm` | `13px` | 小号文字 |
| `--brand-font-size-base` | `14px` | 正文 |
| `--brand-font-size-lg` | `16px` | 大号文字 |
| `--brand-font-size-xl` | `18px` | 标题级别 4 |
| `--brand-font-size-2xl` | `20px` | 标题级别 3 |
| `--brand-font-size-3xl` | `24px` | 标题级别 2 |
| `--brand-font-size-4xl` | `28px` | 标题级别 1 |
| `--brand-font-weight-normal` | `400` | |
| `--brand-font-weight-medium` | `500` | |
| `--brand-font-weight-semibold` | `600` | |
| `--brand-font-weight-bold` | `700` | |
| `--brand-line-height` | `1.5` | 行高 |

### 5. 间距体系（4px 基准）

| Token | 值 | 用途 |
|---|---|---|
| `--brand-spacing-1` | `4px` | 微间距 |
| `--brand-spacing-2` | `8px` | 紧凑间距 |
| `--brand-spacing-3` | `12px` | 小间距 |
| `--brand-spacing-4` | `16px` | 标准间距 |
| `--brand-spacing-5` | `20px` | 中间距 |
| `--brand-spacing-6` | `24px` | 大间距 |
| `--brand-spacing-8` | `32px` | 超大间距 |
| `--brand-spacing-10` | `40px` | 节间距 |
| `--brand-spacing-12` | `48px` | 章节间距 |

### 6. 圆角与阴影

| Token | 值 | 用途 |
|---|---|---|
| `--brand-radius-sm` | `4px` | 小圆角 |
| `--brand-radius-md` | `6px` | 标准圆角 |
| `--brand-radius-lg` | `8px` | 大圆角 |
| `--brand-radius-xl` | `12px` | 超大圆角 |
| `--brand-shadow-sm` | `0 1px 2px rgba(0,0,0,0.06)` | 浅阴影 |
| `--brand-shadow-md` | `0 2px 8px rgba(0,0,0,0.08)` | 标准阴影 |
| `--brand-shadow-lg` | `0 4px 16px rgba(0,0,0,0.10)` | 深阴影 |

### 7. 布局结构

```
+-------------------------------------------------------+
| Header (56px)                                         |
| Logo ··· 消息 | 我的(账号信息/帮助/关于)               |
+--------+----------------------------------------------+
|        |  Tab Bar                                      |
| Menu   |  [Dashboard] [盲板管理] [位置] ...  x          |
| (可折叠)| +----------------------------------------------+|
|        | |                                              ||
|        | |  Content Area                                ||
| 垂直   | |  (多页签内容)                                 ||
| 工具   | |                                              ||
| 栏/    | |                                              ||
| 树形   | |                                              ||
| 菜单   | +----------------------------------------------+|
+--------+----------------------------------------------+
```

- Header 高度：56px
- 菜单展开宽度：220px
- 菜单折叠宽度：64px
- 页签栏高度：40px
- 内容区 padding：16px

### 8. 组件重设计范围

每个现有页面组件的视觉更新：
- **Login**：居中卡片式设计，使用品牌色
- **BlindPlateList**：标准列表页，搜索栏 + 表格
- **LocationTree**：树形结构 + 右侧内容
- **OperationList**：操作列表 + 筛选
- **InspectionList**：巡检列表 + 状态标签
- **Dashboard**：统计卡片 + 图表概览

## Risks / Trade-offs

1. **Element Plus 版本兼容性**：CSS 变量覆盖依赖 Element Plus 版本。当前使用 2.6.3，CSS 变量方案成熟。
2. **主题切换**：当前设计系统只定义一套品牌色，未考虑暗色模式。如需暗色模式，需增加 `--brand-*-dark` 系列 token。
3. **多页签内存**：打开多个页签时可能占用较多内存，关闭时需确保组件销毁。
4. **旧样式迁移**：现有页面可能有个别内联样式，需要在重设计过程中逐一检查和迁移。