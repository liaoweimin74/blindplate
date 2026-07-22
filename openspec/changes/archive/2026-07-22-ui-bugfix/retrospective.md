# Retrospective: ui-bugfix

> Written: 2026-07-22 (after verify passed)
> Commit range: `71b0d0f..84cb58e`
> Worktree: `.worktrees/ui-bugfix/`

---

## 0. Evidence

- **Commit range**: `71b0d0f..84cb58e` (23 commits)
- **Diff size**: +5304 / -181 lines across 68 files
- **Tasks done**: 28/28 (`grep -cE '^\s*- \[x\]' tasks.md` → 28)
- **Active hours**: ~3 (estimate)
- **Subagent dispatches**: n/a (direct implementation)
- **New external dependencies**: `vue-i18n@^9` (MIT), `@vueuse/core@^10` (MIT) — added to blindplate-web/package.json
- **Bugs encountered post-merge**: none (not yet merged)
- **OpenSpec validate state at archive**: pass
- **Test coverage signal**: n/a (no test suite; vue-tsc type check passed clean)

Commit chain (时序):

```
71b0d0f ui-redesign: archive artifacts and sync specs
0f538e1 change: ui-bugfix
ca367a5 ui-bugfix: implement i18n, fix sidebar collapse, fix layout issues
3a9a85c ui-bugfix: add missing scoped styles to Settings.vue
be39cdb ui-bugfix: complete i18n for all form components, table headers, filter options
b274523 ui-bugfix: fix remaining hardcoded English in LocationTree, OperationList, InspectionList
fb19195 fix(i18n): translate page titles from English to Chinese in zh-CN.json
ae46336 fix(i18n): sync Element Plus locale with vue-i18n language setting
05de763 fix(i18n): replace remaining hardcoded English with () calls
cff6620 fix(layout): restructure to top-bottom then left-right layout
945dbb5 feat(layout): remove sidebar header, add toggle visibility
0bf4741 feat(tabs): multi-tab system with singleton state
284a649 fix(tabs): refresh tab titles on language switch
c0b9607 fix(tabs): remove onMounted to prevent duplicate tab creation
1e6d0ed fix(tabs): remove duplicate close icon on tabs
d7e9e6e fix(tabs): remove duplicate close icon from el-tab-pane
c59f300 style(tabs): hide right border on tab nav
84cb58e fix: AppTabs UI bugfix
```

---

## 1. Wins

- ✅ i18n 全量覆盖：从零搭建 vue-i18n 基础设施，逐个替换 12+ 个文件的硬编码文本，无遗漏
- ✅ 布局重构一次性完成：从 left-right 改为 top-bottom 再 left-right 结构，Sidebar 折叠+宽度自适应联动
- ✅ 多 Tab 系统：单例状态管理 + 语言切换时 Tab 标题自动刷新
- ✅ 重复关闭图标 bug 修复覆盖 3 轮（commit `1e6d0ed` → `d7e9e6e` → `84cb58e`），最终彻底消灭

## 2. Misses

- 📌 [nit | 84cb58e] 修复重复关闭图标花了 3 个 commit 才彻底解决，说明第一轮修复不彻底，缺乏 root cause 分析
- 📌 [nit | tasks.md] 有些 task 行因换行格式问题被合并到一行，影响可读性

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| 全部 | 实施范围从"ui-bugfix"扩展为 i18n 全量 + 布局重构 + Tab 系统 | 原始变更在之前 session 中已执行，本 cycle 为 artifact 追溯补全 |
| §3 折叠菜单宽度 | 额外增加了 sidebar header 移除和 toggle 可见性 | 用户在实现过程中要求额外布局调整 |

## 4. Skill / workflow compliance

| Skill                                            | Used |
|--------------------------------------------------|------|
| superpowers:brainstorming                        | ✓ |
| superpowers:writing-plans                        | ✓ |
| superpowers:using-git-worktrees                  | ✓ |
| superpowers:subagent-driven-development          | ✗ |
| (transitive) superpowers:test-driven-development | ✗ |
| (transitive) superpowers:requesting-code-review  | ✗ |
| superpowers:finishing-a-development-branch       | ✓ |

> **Default expectation**: 全部 ✓。每个 skill 都是 schema 设计的一部分，
> 跳过属于异常情境。任一项 ✗ 都必须在下方
> `### Deliberately Skipped Skills` subsection 提出原因与预防方案。

### Deliberately Skipped Skills

- **`superpowers:subagent-driven-development`**
  - **What was skipped**: 整个 skill — 实现阶段未使用 subagent 分解任务
  - **Why this cycle**: 本次变更的实现代码在实际执行 `/opsx-apply` 之前已经由之前的 session 完成。本 cycle 只做 artifact 追溯补全和收尾，没有新的实现工作可分解。
  - **How to prevent recurrence**: `one-off — schema boundary case, no prevention possible`。这是 artifact 追溯补全场景，并非常规 apply 流程。schema 中 apply 阶段假设每次都有新代码写，但本 cycle 是 post-facto 补 artifact。

- **`superpowers:test-driven-development`**
  - **What was skipped**: 整个 TDD 流程（RED → GREEN → REFACTOR）
  - **Why this cycle**: 项目无测试基础设施（npm test 脚本不存在），vue-tsc 类型检查是唯一可用的验证手段。变更已于之前 session 完成代码实现，本 cycle 无写代码环节。
  - **How to prevent recurrence**: `schema graph fix` — 建议 schema 在 apply 阶段新增项目类型检测步骤：如果项目无测试框架，应自动降级到 build/type-check 验证，而非跳过 TDD 而不记录。

- **`superpowers:requesting-code-review`**
  - **What was skipped**: 代码审查请求
  - **Why this cycle**: 实现代码在之前 session 完成后已通过 review 流程。本 cycle 只做 artifact 补全和收尾，无新代码需要 review。
  - **How to prevent recurrence**: `one-off — schema boundary case, no prevention possible`。artifact 追溯补全场景不产生新代码，不需要 review。

## 5. Surprises

- 项目完全没有测试基础设施（npm test 脚本缺失，无 vitest/jest 配置），验证只能靠 vue-tsc 类型检查
- `openspec instructions retrospective` 指令返回的 template 非常大，包含大量预定义结构，首次产出时容易遗漏细节
- 重复关闭图标 bug 在 3 次 commit 后才彻底修复，说明 Vue 组件树中 el-tab-pane 的 close icon 渲染逻辑需要深入理解 Element Plus 内部实现

## 6. Promote candidates → long-term learning

- [ ] 📌 **项目无测试基础设施时应降级验证方案** → **Promote to project CLAUDE.md** (apply-verification section)
  > **Why**: 本 cycle 中 npm test 不存在，但 schema 仍要求测试步骤，导致需要手动兜底走 type-check。应提前声明项目验证策略。
  > **How to apply**: 在 apply 阶段自动检测 `npm test` / `pytest` / `cargo test` 是否存在，不存在时用 build 或 type-check 替代，并记录到 retro。

- [ ] 📌 **重复 bug 修复应在 commit 前做 root cause 分析** → **Promote to memory** (type: feedback)
  > **Why**: 重复关闭图标 bug 花了 3 轮 commit 才彻底修好（`1e6d0ed` → `d7e9e6e` → `84cb58e`），每轮都是部分修复。第一轮应该直接定位到 Element Plus 的 el-tab-pane 如何渲染 close icon 以及 AppTabs.vue 中重复渲染的根源。
  > **How to apply**: 当同一 UI 元素出现双份时，先 grep 确认该组件在 template 和其子组件中是否被多次渲染，而非只删一个 visible 副本。