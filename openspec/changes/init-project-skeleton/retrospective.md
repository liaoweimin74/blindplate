# Retrospective: init-project-skeleton

> Written: 2026-07-20 (planning-phase placeholder, to be updated after apply)
> Commit range: `<pending — to be filled after apply>`
> Worktree: `.worktrees/init-project-skeleton/`

---

> ⚠️ **Planning phase**: This retrospective is a template created during `/opsx-ff`
> artifact generation. It will be populated with actual results after `/opsx-apply`
> completes the implementation.

---

## 0. Evidence

> 量化前置數據 — 後續 Wins / Misses bullets 直接引用,避免每行重複 [evidence: ...]。

- **Commit range**: `<pending>`
- **Diff size**: `<pending>`
- **Tasks done**: `0/46` (planning phase — no tasks started)
- **Active hours**: `<pending>`
- **Subagent dispatches**: `<pending>`
- **New external dependencies**: `<pending>`
- **Bugs encountered post-merge**: `<pending>`
- **OpenSpec validate state at archive**: `<pending>`
- **Test coverage signal**: `<pending>`

Commit chain (時序):

```
<pending — to be filled after apply>
```

---

## 1. Wins

- (none observed — planning phase only)

## 2. Misses

- (none observed — planning phase only)

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| — | — | — |

## 4. Skill / workflow compliance

| Skill                                            | Used |
|--------------------------------------------------|------|
| superpowers:brainstorming                        | ✓    |
| superpowers:writing-plans                        | ✓    |
| superpowers:using-git-worktrees                  | ✓    |
| superpowers:subagent-driven-development          | —    |
| (transitive) superpowers:test-driven-development | —    |
| (transitive) superpowers:requesting-code-review  | —    |
| superpowers:finishing-a-development-branch       | —    |

> Subagent-driven-development, TDD, requesting-code-review, and finishing-a-development-branch
> will be used during `/opsx-apply` phase.

### Deliberately Skipped Skills

> 整節空白（全綠）是預期狀態。apply 階段的 skill 尚未運行。

## 5. Surprises

- (none observed — planning phase only)

## 6. Promote candidates → long-term learning

每條 candidate 用 `- [ ]` checklist:

- [ ] 📌 **PowerShell openspec CLI Join-Path warning** → **One-off** (記錄即可,不 promote)
  > **Why**: PowerShell 5.1 on Windows with Chinese locale causes `Join-Path` positional parameter warning
  > on every `openspec` CLI call. Commands still execute successfully — the warning is cosmetic only.
  > **How to apply**: When running `openspec` commands on Windows, ignore the Join-Path warning
  > if the JSON output follows it.

---

> **Carry-forward 機制**:下個 cycle 寫 retro 時,可
> `grep -A 5 '^- \[ \]' openspec/changes/archive/*/retrospective.md` 取出
> 既往 unchecked candidates,逐筆判斷要 carry-forward 到本 cycle §6、就地
> promote、或標 stale 不再追蹤。
