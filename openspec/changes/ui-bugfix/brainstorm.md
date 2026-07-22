## Design Summary

UI 修复变更（ui-bugfix）修复 4 个 UI 问题：
1. 界面中英文混杂 → 引入 i18n 支持，默认中文
2. 折叠菜单布局占位 → 折叠时 sidebar 宽度动态变化
3. UserList 按钮对齐 → 按钮与搜索框同行靠右
4. 系统页面布局不一致 → 统一使用 page-container 布局

## Alternatives Considered

### 方案 A：vue-i18n 完整方案（推荐）
- **做法**：安装 vue-i18n，创建中英文 locale 文件，所有文本通过 $t() 调用
- **优点**：标准化方案，扩展性好，支持未来添加更多语言
- **缺点**：需要修改所有模板中的文本

### 方案 B：简单语言切换变量
- **做法**：定义一个全局 language ref，所有文本通过条件判断切换
- **优点**：无需额外依赖
- **缺点**：扩展性差，维护成本高

### 为何采用方案 A
vue-i18n 是 Vue 生态标准方案，后续添加语言只需新增 locale 文件，不改代码。

## Agreed Approach

采用 vue-i18n + 前端语言切换。不涉及后端。
- 安装 vue-i18n 依赖
- 创建 `src/locales/zh-CN.json` 和 `src/locales/en.json`
- 创建 `src/i18n/index.ts` 初始化
- 在 Profile 下拉菜单中增加语言切换选项
- 语言偏好存入 localStorage

## Key Decisions

1. i18n 使用 vue-i18n v9 (Vue 3 兼容)
2. 语言偏好存储在 localStorage 中，key 为 `language`
3. 默认语言为中文（zh-CN）
4. 语言切换不刷新页面，实时生效
5. 折叠菜单宽度通过 AppSidebar  emit 事件通知 AppLayout

## Open Questions

- 是否需要后端也支持多语言？（当前仅前端）