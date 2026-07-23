# Blind Spot Status Ledger - Retrospective

## What Went Well
- Computed read-only approach (Option A) avoids schema migration and data sync complexity entirely - status is always accurate by derivation
- Existing OperationOrder + Location + BlindPlate entities provide all data needed; no new persistent table required
- Clear PRD source (§6.5 data model + §7.1.3 functional requirements) made requirements unambiguous
- Backend conventions (Result wrapper, BusinessException, JPA, package structure) are well-established and easy to follow
- Frontend conventions (tab layout, i18n, API module pattern) are consistent across existing modules
- Parallelizable: backend (B1-B4) and frontend foundation (F1-F3) can proceed simultaneously

## What Could Be Improved
- Location entity currently lacks medium type (介质类型) and hazard level (危害等级) fields - full filtering per PRD §7.1.3 depends on §7.1.2 isolation-point master data enrichment which is in a separate unmerged change
- OperationOrder.type uses free-form strings (not an enum) - status derivation must handle case-insensitive type matching and validate against known types
- "盲板已拆除" (plate removed) status from PRD §6.5 cannot be detected without a dedicated拆除 operation type - v1 uses 通/盲/未知 only; this is a known gap
- N+1 query risk: batch querying operations for all locations requires careful repository method design (findByLocationIdIn...) to avoid per-location queries
- Abnormal threshold (720h/30 days) is hardcoded in v1; rule engine configuration (§7.10.1) is P1

## Action Items
- [ ] Verify Location subtree traversal logic works correctly for filtering (parent->children recursive query)
- [ ] Confirm OperationOrder.type values in existing data match "INSTALL"/"REMOVE"/"INSPECT" (case-sensitive check)
- [ ] Add DB index on bp_operation_order(location_id, status, type, actual_date) if query performance is insufficient
- [ ] Track dependency: full medium type / hazard level filtering blocked on §7.1.2 isolation-point master data enrichment
- [ ] Track gap: "盲板已拆除" status detection blocked on introducing a拆除 operation type
- [ ] Consider pagination if plant exceeds 500 isolation points (currently out of scope)
- [ ] P1: Extract abnormal threshold to configurable rule engine (§7.10.1)

## Open Risks
- If OperationOrder data is sparse (few completed operations), most locations will show 未知 status - verify with seed data during implementation
- If Location hierarchy is deep, parentPath string construction may be expensive - consider caching or limiting depth
- Frontend el-tree-select for location filtering needs the full location tree from existing Location API - verify the endpoint returns tree structure
