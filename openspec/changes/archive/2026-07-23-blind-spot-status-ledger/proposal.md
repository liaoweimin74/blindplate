# Blind Spot Status Ledger - Proposal

## Summary
Add a read-only blind spot status ledger (点位通盲状态台账) to the blind plate management system. The ledger computes each isolation point's current pass/blind (通/盲) status in real time from existing operation order history - no new database table is introduced. Users access it via a new sidebar menu item, view a filterable table with abnormal-state highlighting, and can trace per-point status change timelines.

## Motivation
Plant operators need a single pane of glass to see the pass/blind status of every isolation point across the entire plant (PRD Goal G4, §7.1.3). Currently the system has blind plate inventory, location tree, and operation orders, but no aggregated status view. Manually checking each location's operation history to determine if it's blocked (盲) or open (通) is error-prone and time-consuming. This P0 feature delivers the core data visibility required before downstream modules (isolation schemes, visual monitoring) can be built.

## Approach: Computed Read-Only Ledger (Option A)
**Why not B (persistent snapshot table)?** - A snapshot table duplicates data already derivable from OperationOrder records. It introduces sync burden: the snapshot must be refreshed whenever an operation completes, risking stale data. For a P0 read-only ledger, this is over-engineering.

**Why not C (snapshot + dedicated history log)?** - The status change timeline is already captured in OperationOrder (INSTALL/REMOVE events). A second history table duplicates operation semantics and doubles maintenance.

**Why A (computed read-only)?** - Status is derived data. Computing it at query time from existing OperationOrder history ensures it is always accurate with zero sync risk. No schema migration needed. Simplest to deliver, lowest maintenance, aligns with PRD principle "数据一次录入、多处复用".

## Key Features
1. **Plant-Wide Status Overview** - Table listing all isolation points with computed 通/盲/未知 status
2. **Filtering** - By device/area (location hierarchy), status, abnormal-only toggle
3. **Abnormal Highlighting** - Long-term blind (>30 days), status conflicts auto-highlighted
4. **Status History Timeline** - Click any row to see the operation history timeline
5. **Backend Computation** - BlindSpotStatusService derives status from OperationOrder + Location + BlindPlate

## Scope
### In Scope
- `BlindSpotStatusController.java` - 2 GET endpoints
- `BlindSpotStatusService.java` - status derivation + abnormal detection logic
- `BlindSpotStatusDTO.java` / `StatusHistoryDTO.java` - computed DTOs
- `OperationOrderRepository` - new query method for location+status+type filtering
- `BlindSpotStatusList.vue` - main ledger page with table, filters, history dialog
- `api/blindspotstatus.ts` - frontend API module
- Route `/blind-spot-status` + sidebar menu item "通盲状态台账"
- i18n keys (zh-CN + en) for menu, page, table, filter, status labels
- TypeScript types in `types/index.ts`

### Out of Scope (v1)
- Persistent status snapshot table (P1 if performance requires)
- Configurable abnormal threshold rules (rule engine is §7.10.1, P1)
- Medium type / hazard level filtering (depends on §7.1.2 isolation-point master data enrichment)
- Real-time WebSocket push updates (P2 visual monitoring §7.7)
- Data export (Excel/PDF) - covered by §7.9 reporting
- Pagination (current plant scale <500 isolation points; add if needed)

## Risks
| Risk | Mitigation |
|------|-----------|
| Performance with 1000+ locations (N+1 queries) | Batch query operations by location IDs; add DB index on (location_id, status, type, actual_date) |
| Location entity lacks medium/hazard fields for full filtering | Filter by available fields (type, hierarchy); note dependency on §7.1.2 |
| Status derivation assumes INSTALL=盲, REMOVE=通 | Document assumption; PRD §6.5 confirms 通/盲/盲板已拆除/未知 semantics |
| No "盲板已拆除" detection without a拆除 operation type | v1 uses 通/盲/未知; 盲板已拆除 added when拆除 operation type is introduced |
| OperationOrder.type values not standardized (string) | Service handles type case-insensitively; validates against known types |
