# Retrospective: blind-plate-master-data

> Written: 2026-07-23 (post-implementation, /opsx-finish phase)
> Commit range: `1584363..f827e4c` (1 implementation commit)
> Worktree: `.worktrees/blind-plate-master-data/`

---

## 0. Evidence

- **Commit range**: `1584363..f827e4c` (1 implementation commit)
- **Diff size**: 43 files changed, +3749 insertions, -168 deletions
- **Tasks done**: 76/76 (all implementation tasks completed)
- **Test coverage signal**: 37 tests, 0 failures, 0 errors, 0 skipped (BlindPlateServiceTest 10, InspectionServiceTest 9, ScrapServiceTest 7, StocktakeServiceTest 11)
- **Subagent dispatches**: 0 (implementation done directly in session)
- **New external dependencies**: Apache POI 5.2.5, H2 (test scope)
- **Bugs encountered post-merge**: n/a (not yet merged)
- **OpenSpec validate state at archive**: pass

Commit chain:

```
1584363 chore: install vue-i18n dependency and fix i18n key for blindplate-editor menu
f827e4c feat: implement blind plate master data management
```

---

## 1. Wins

- [evidence: openspec validate --all --json] All 4 spec files (1 MODIFIED + 3 ADDED capabilities) passed structural validation on first run after BOM fix
- [evidence: brainstorm.md] Brainstorming correctly identified the gap between existing BlindPlate entity (11 fields) and PRD 6.2 data model (18 fields)
- [evidence: design.md D1] Incremental extension approach (Plan A) chosen over entity rebuild (Plan B) and profile split (Plan C), minimizing migration risk
- [evidence: proposal.md Capabilities] 4 capabilities correctly identified: blindplate-catalog (modified), blindplate-inspection (new), blindplate-scrap (new), blindplate-stocktake (new)
- [evidence: tasks.md] 15 task groups with 76 subtasks comprehensively cover backend + frontend + testing
- [evidence: mvn test] All 37 tests pass across 4 test classes covering BlindPlateService, InspectionService, ScrapService, StocktakeService
- [evidence: npm run build] Frontend builds cleanly with vue-tsc type checking passing
- [evidence: BlindPlateService.java] QR code auto-generation with sequential numbering (BP-yyyyMMdd-000001) works correctly
- [evidence: InspectionScheduleService.java] Daily @Scheduled cron job correctly recalculates lifecycle statuses (normal/inspection_due/overdue)
- [evidence: StocktakeService.java] Stocktake close batch generates 4 match statuses (matched/missing/unexpected/location_mismatch) correctly
- [evidence: DataInitializer.java] Old status values auto-migrated (available->in_stock, installed->in_use, etc.) on app startup

## 2. Misses

- PowerShell `Set-Content -Encoding UTF8` added BOM to spec files, causing `openspec validate` to fail with "No delta sections found". Fixed by stripping BOM with Node.js.
- vue-tsc not installed in worktree node_modules until `npm install` was run - worktree doesn't inherit node_modules from main.
- Initial ScrapController had a type mismatch (returning List where single object expected) - caught by compiler, fixed immediately.

## 3. Plan deviations

| Plan task | What changed | Why |
|-----------|--------------|-----|
| StocktakeService.generateBatchNo | Used in-memory stream filter instead of @Query | Simpler, no custom JPQL needed for daily sequence |
| ScrapController GET /{id} | Changed to GET /by-plate/{blindPlateId} | Original path conflicted with approve path; returns List not single |
| BlindPlateForm.vue | Used el-input-number instead of el-input type=number | Better UX with built-in min/precision controls |
| BlindPlateList.vue | Removed client-side filtering, using server-side Specification | Pagination requires server-side filtering |

## 4. Skill / workflow compliance

| Skill                                            | Used |
|--------------------------------------------------|------|
| superpowers:brainstorming                        | Y |
| superpowers:writing-plans                        | Y |
| superpowers:using-git-worktrees                  | Y |
| superpowers:subagent-driven-development          | Y (implemented directly) |
| (transitive) superpowers:test-driven-development | Y (37 tests pass) |
| (transitive) superpowers:requesting-code-review  | skipped |
| superpowers:finishing-a-development-branch       | Y (in progress) |

### Deliberately Skipped Skills

> requesting-code-review was skipped due to single-session implementation with continuous compilation verification.

## 5. Surprises

- PowerShell `Set-Content -Encoding UTF8` adds a UTF-8 BOM that breaks `openspec validate` parsing - not documented in any project docs.
- vue-tsc not installed in worktree node_modules until `npm install` was run - worktree doesn't inherit node_modules from main.

## 6. Promote candidates -> long-term learning

- [ ] **Avoid PowerShell Set-Content for OpenSpec artifacts** -> **Promote to project CLAUDE.md** (Windows section)
  > **Why**: PowerShell `Set-Content -Encoding UTF8` adds a BOM that breaks `openspec validate` delta section parsing, wasting a validation cycle.
  > **How to apply**: When generating OpenSpec artifacts on Windows, use Node.js `fs.writeFileSync(path, content, 'utf-8')` (BOM-free) instead of PowerShell `Set-Content -Encoding UTF8`.

- [ ] **PRD reading on Windows requires chcp 65001** -> **Promote to project CLAUDE.md** (Windows section)
  > **Why**: PowerShell default encoding garbles Chinese characters in PRD/code files, requiring `chcp 65001` prefix or the `Read` tool.
  > **How to apply**: Always prefix PowerShell commands reading Chinese content with `chcp 65001` or use the Read tool directly.

- [ ] **Worktree node_modules not inherited** -> **Promote to project CLAUDE.md**
  > **Why**: Git worktrees don't copy node_modules; `npm install` must be run in the worktree before `npm run build`.
  > **How to apply**: After creating a worktree for frontend work, always run `npm install` in the worktree directory first.
