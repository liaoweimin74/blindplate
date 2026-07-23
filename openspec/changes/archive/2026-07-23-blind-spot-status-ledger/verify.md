# Blind Spot Status Ledger - Verification Plan

## Build Verification
- [ ] `cd blindplate-server && mvn compile` exits with code 0
- [ ] `cd blindplate-web && npm run build` exits with code 0
- [ ] No TypeScript errors (vue-tsc passes)
- [ ] No lint errors

## Backend Verification
- [ ] GET `/api/v1/blind-spot-status` returns `Result<List<BlindSpotStatusDTO>>` with code 200
- [ ] Response includes all isolation points with computed status field
- [ ] Isolation point with latest INSTALL operation -> status = "盲"
- [ ] Isolation point with latest REMOVE operation -> status = "通"
- [ ] Isolation point with no completed operations -> status = "未知"
- [ ] statusDurationHours is a non-negative number when status != 未知
- [ ] GET `/api/v1/blind-spot-status?locationId=1` returns only locations in location 1's subtree
- [ ] GET `/api/v1/blind-spot-status?status=盲` returns only 盲 status rows
- [ ] GET `/api/v1/blind-spot-status?abnormalOnly=true` returns only abnormal-flagged rows
- [ ] Abnormal flag set when blind duration > 720 hours
- [ ] Abnormal flag set when consecutive INSTALL operations without REMOVE (status conflict)
- [ ] Normal blind (< 720h, no conflict) -> abnormal = false
- [ ] GET `/api/v1/blind-spot-status/{locationId}/history` returns operation timeline ordered DESC
- [ ] History entries include operationType, blindPlateCode, resultingStatus
- [ ] All endpoints return 401 without valid JWT token
- [ ] No new database table created (no new @Entity class, JPA ddl-auto unchanged)

## Frontend Verification
- [ ] "通盲状态台账" menu item visible in sidebar after "位置管理"
- [ ] Click menu opens ledger tab with title "通盲状态台账"
- [ ] Ledger tab is closable
- [ ] Table renders with columns: 隔离点, 位置路径, 当前状态, 当前盲板, 关联作业票, 最近操作时间, 持续时长, 异常, 操作
- [ ] Status column shows colored el-tag (green=通, red=盲, gray=未知)
- [ ] Device/area tree-select filter narrows results to selected subtree
- [ ] Status filter (通/盲/未知) narrows results
- [ ] Abnormal-only switch shows only abnormal rows
- [ ] Refresh button re-fetches data
- [ ] Abnormal rows highlighted with warning background color
- [ ] Click "查看历史" button -> status history dialog opens
- [ ] History dialog shows el-timeline with operation entries
- [ ] Each timeline entry shows time, type tag, blind plate code, resulting status
- [ ] History dialog with no records shows empty state
- [ ] Loading spinner shows during API calls
- [ ] Network error shows error toast message

## Edge Cases
- [ ] Location with zero operations -> status 未知, abnormal=false, no history entries
- [ ] Location with only INSPECT operations (no INSTALL/REMOVE) -> status 未知
- [ ] Operation with status != "completed" -> ignored in status derivation
- [ ] Multiple INSTALL without REMOVE -> status conflict, abnormal=true
- [ ] Blind plate from INSTALL operation no longer exists (deleted) -> currentBlindPlateCode shows null/empty gracefully
- [ ] Large result set (100+ locations) -> table renders without lag
- [ ] Filter combinations (locationId + status + abnormalOnly) work together correctly
- [ ] i18n: all labels display in Chinese (zh-CN) and English (en) correctly
