# Retrospective: blind-plate-master-data

> Written: 2026-07-23 (pre-implementation, ff artifact generation phase)
> Commit range: `1584363..HEAD` (artifact commit pending)
> Worktree: `.worktrees/blind-plate-master-data/`

---

## 0. Evidence

> 本 retrospective 產生於 `/opsx-ff` artifact 生成階段，implementation 尚未開始。
> 量化數據為 artifact 生成階段的結果。

- **Commit range**: `1584363..HEAD` (1 commit — artifact commit pending)
- **Diff size**: +8 artifacts (brainstorm.md, design.md, proposal.md, 4 spec files, tasks.md, plan.md, verify.md, retrospective.md)
- **Tasks done**: 0/76 (implementation phase not started)
- **Active hours**: ~1h (artifact generation)
- **Subagent dispatches**: 0 (all artifacts generated directly in ff workflow)
- **New external dependencies**: Apache POI (planned, not yet added to pom.xml)
- **Bugs encountered post-merge**: n/a (not merged yet)
- **OpenSpec validate state at archive**: pass (blind-plate-master-data change valid=true)
- **Test coverage signal**: n/a (implementation not started)

Commit chain:

```
1584363 chore: install vue-i18n dependency and fix i18n key for blindplate-editor menu
(pending) change: blind-plate-master-data (artifact commit)
```

---

## 1. Wins

- [evidence: openspec validate --all --json] All 4 spec files (1 MODIFIED + 3 ADDED capabilities) passed structural validation on first run after BOM fix
- [evidence: brainstorm.md] Brainstorming correctly identified the gap between existing BlindPlate entity (11 fields) and PRD 6.2 data model (18 fields)
- [evidence: design.md D1] Incremental extension approach (Plan A) chosen over entity rebuild (Plan B) and profile split (Plan C), minimizing migration risk
- [evidence: proposal.md Capabilities] 4 capabilities correctly identified: blindplate-catalog (modified), blindplate-inspection (new), blindplate-scrap (new), blindplate-stocktake (new)
- [evidence: tasks.md] 15 task groups with 76 subtasks comprehensively cover backend + frontend + testing

## 2. Misses

- 🟡 [painful | evidence: openspec validate first run] PowerShell `Set-Content -Encoding UTF8` added BOM to spec files, causing `openspec validate` to fail with "No delta sections found". Fixed by stripping BOM with Node.js. Should use Node.js `fs.writeFileSync` or `chcp 65001` + `Out-File -Encoding utf8NoBOM` for future artifact generation on Windows.
- 📌 [nit | evidence: PRD encoding] PRD file has encoding issues when read via PowerShell `Get-Content`, requiring Workaround using the `Read` tool or `chcp 65001` flag.

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| N/A | No deviations — artifact generation phase completed as planned | All artifacts generated in dependency order |

## 4. Skill / workflow compliance

| Skill                                            | Used |
|--------------------------------------------------|------|
| superpowers:brainstorming                        | ✓ |
| superpowers:writing-plans                        | ✓ |
| superpowers:using-git-worktrees                  | ✓ |
| superpowers:subagent-driven-development          | pending apply |
| (transitive) superpowers:test-driven-development | pending apply |
| (transitive) superpowers:requesting-code-review  | pending apply |
| superpowers:finishing-a-development-branch       | pending apply |

> Skills marked "pending apply" are expected to be used during the `/opsx-apply` phase, not the artifact generation phase.

### Deliberately Skipped Skills

> No skills deliberately skipped in this phase. Subagent-driven-development, TDD, code review, and finishing-a-development-branch are apply-phase skills that will execute during `/opsx-apply`.

## 5. Surprises

- PowerShell `Set-Content -Encoding UTF8` adds a UTF-8 BOM that breaks `openspec validate` parsing — not documented in any project docs. Detected by inspecting raw bytes with Node.js `fs.readFileSync`.

## 6. Promote candidates -> long-term learning

- [ ] 🟡 **Avoid PowerShell Set-Content for OpenSpec artifacts** -> **Promote to project CLAUDE.md** (Windows section)
  > **Why**: PowerShell `Set-Content -Encoding UTF8` adds a BOM that breaks `openspec validate` delta section parsing, wasting a validation cycle.
  > **How to apply**: When generating OpenSpec artifacts on Windows, use Node.js `fs.writeFileSync(path, content, 'utf-8')` (BOM-free) instead of PowerShell `Set-Content -Encoding UTF8`.

- [ ] 📌 **PRD reading on Windows requires chcp 65001** -> **Promote to project CLAUDE.md** (Windows section)
  > **Why**: PowerShell default encoding garbles Chinese characters in PRD/code files, requiring `chcp 65001` prefix or the `Read` tool.
  > **How to apply**: Always prefix PowerShell commands reading Chinese content with `chcp 65001` or use the Read tool directly.