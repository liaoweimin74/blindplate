# Retrospective: ui-redesign

> Written: 2026-07-21 (after apply completed)
> Commit range: `305d2ec..6479b44`
> Worktree: `.worktrees/ui-redesign/`

---

## 0. Evidence

- **Commit range**: `305d2ec..6479b44` (2 commits)
- **Diff size**: +3510 / -195 lines across 39 files
- **Tasks done**: 24/24
- **Active hours**: ~2
- **Subagent dispatches**: 3 (failed due to credits, implemented directly)
- **New external dependencies**: sass (devDependency)
- **Bugs encountered post-merge**: none
- **OpenSpec validate state at archive**: pass

---

## 1. Wins

- [x] 設計系統 token 結構完整，覆蓋品牌色、字體、間距、圓角、陰影
- [x] 布局方案與 Element Plus 深度集成，利用現有組件減少自定義工作
- [x] 多頁簽方案明確（關閉即銷毀），避免記憶體泄漏
- [x] 所有頁面完成重設計，統一視覺風格
- [x] 表單對話框組件完整，覆蓋 CRUD 操作

---

## 2. Misses

- [ ] 未考慮暗色模式支援 — 如需暗色模式需增加 `--brand-*-dark` token
- [ ] 菜單數據來源為靜態配置，未從 API 獲取
- [ ] 權限指令雖已創建但未在頁面中廣泛使用

---

## 3. Surprises

- [x] PowerShell 的模板字符串（`${}`）在 Vue 模板中引起 TypeScript 錯誤 — 需改用函數調用
- [x] `Set-Content` 和 `Out-File` 在 PowerShell 中寫入 `@` 開頭的 heredoc 時有空文件問題

---

## 4. What to Improve

- [x] 下次變更應在 artifacts 生成前先確認菜單數據結構
- [ ] 複雜的 Vue 組件應使用 `write` 工具而非 `bash` heredoc 寫入

---

## 5. Knowledge Candidates

- PowerSell 的 heredoc (`@""@`) 在 Vue template 有 `${}` 時會導致模板字符串被 PowerShell 提前解析，應改用 `@''@` 或直接使用 `write` 工具