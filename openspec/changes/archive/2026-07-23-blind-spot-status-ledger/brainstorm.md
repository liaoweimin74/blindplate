# Blind Spot Status Ledger - Brainstorm

## Project Context
- Spring Boot 3 + JPA/Hibernate + Lombok backend (`blindplate-server`, package `com.mangban.*`)
- Vue 3 + Element Plus + Pinia + vue-i18n + TypeScript frontend (`blindplate-web`)
- Tab-based layout (AppLayout/AppSidebar/AppTabs), menu items in AppSidebar.vue
- Existing modules: auth, blindplate (BlindPlate entity), blindboard (BoardProject), inspection, location (Location tree entity), operation (OperationOrder), report
- Backend conventions: `@Data @Entity @Table(name="bp_*")`, `Result<T>` wrapper, `BusinessException(code,msg)`, JPA ddl-auto (no Flyway), `@RestController @RequestMapping("/api/v1/*")`
- Frontend conventions: `views/<module>/<Page>.vue`, `api/<module>.ts` (axios baseURL `/api/v1`), `stores/<module>.ts`, router children of AppLayout, i18n `menu.*` keys in zh-CN.json/en.json
- PRD source: §6.5 点位通盲状态 (data model), §7.1.3 点位通盲状态台账 (functional requirements), Goal G4, Priority P0

## User Needs
1. A plant-wide overview table showing every isolation point (隔离点) and its current pass/blind (通/盲) status in real time
2. Filter by device/area, medium type, hazard level, risk grade
3. Auto-highlight abnormal states (e.g., long-term blind without removal, status conflicts)
4. Trace status change timeline for any isolation point

## Data Source Analysis
- Isolation point = `Location` entity (tree: factory->device->unit->isolation point). Currently has: id, parentId, name, description, type.
- Blind plate = `BlindPlate` entity (code, name, spec, material, status, etc.)
- Operation history = `OperationOrder` entity (orderNo, type [INSTALL/REMOVE/INSPECT], blindplateId, locationId, status, actualDate)
- The 通盲 status of a location is derivable: the latest completed INSTALL/REMOVE operation on that location determines whether it's 通 (pass) or 盲 (blind).

## Design Decisions (Approved)

### Approach: Option A - Computed Read-Only Ledger (NO new persistent table)

**Why A (computed read-only)?**
- Status is derived data - storing a snapshot duplicates the source of truth (OperationOrder)
- Aligns with PRD principle "数据一次录入、多处复用" (single data entry, multi-use)
- No schema migration needed (JPA ddl-auto stays clean)
- Abnormal detection is pure business logic computed at query time
- Simplest to deliver as P0; lowest maintenance burden

**Why not B (persistent snapshot table `bp_blind_spot_status`)?**
- Introduces data sync burden: snapshot must be refreshed when operations complete
- Risk of stale data if refresh fails or is forgotten
- Redundant storage of derivable information
- Adds a migration + entity + sync logic for P0 - over-engineering

**Why not C (snapshot + dedicated history log table)?**
- History timeline already exists in OperationOrder records (INSTALL/REMOVE events)
- A second history table duplicates operation semantics
- Maximum maintenance burden for no additional capability

### Status Derivation Logic
For each Location (isolation point), query OperationOrder where `locationId = X` AND `status = 'completed'` AND `type IN ('INSTALL','REMOVE')`, ordered by `actualDate DESC`:
- Latest = INSTALL → status = 盲 (blind - plate is blocking flow)
- Latest = REMOVE → status = 通 (pass - plate removed, flow open)
- No history → status = 未知 (unknown)
- (Future: if plate installed then physically removed without order → 盲板已拆除, handled when拆除 operation type exists)

### Abnormal Detection Rules (computed at query time)
1. **长期挂盲板未拆除**: status=盲 AND (now - lastOperationTime) > configurable threshold (default 30 days)
2. **状态冲突**: detected when multiple consecutive INSTALL operations without intervening REMOVE (data integrity issue)
3. **状态持续异常长**: any status persisted beyond expected duration threshold

### API Design
- `GET /api/v1/blind-spot-status` - list all isolation points with computed status + filters (locationId/deviceId, status, abnormalOnly)
- `GET /api/v1/blind-spot-status/{locationId}/history` - timeline of status changes from operation orders

### Frontend Design
- New view: `views/blindspotstatus/BlindSpotStatusList.vue` (table + filters + abnormal highlighting)
- Route: `/blind-spot-status` under AppLayout
- Menu item: "通盲状态台账" under sidebar (after 位置管理)
- Status history dialog: click a row → el-dialog with timeline
- New API module: `api/blindspotstatus.ts`
- New types in `types/index.ts`: `BlindSpotStatus`, `BlindSpotStatusFilter`, `StatusHistoryItem`

### Scope Boundary
- Read-only ledger: NO create/update/delete of status records (status is derived)
- Filtering uses Location hierarchy (device/area) - medium type & hazard level filtering noted as dependent on isolation-point master data enrichment (§7.1.2); current Location entity supports type/description filtering
- Abnormal threshold is a constant (30 days) in v1; rule engine configuration is P1

## Open Questions (Resolved)
- **Q: Should we persist abnormal flags for reporting?** A: No - compute at query time for v1. P1 can add a scheduled job + persistence if performance requires.
- **Q: Medium type / hazard level filter?** A: Filter by available Location fields (type, hierarchy) for now. Full medium/hazard filtering lands when §7.1.2 enriches isolation-point master data.

## Verification Checklist (Frontend)
- [ ] "通盲状态台账" menu item visible in sidebar
- [ ] Click menu opens ledger tab with title "通盲状态台账"
- [ ] Ledger tab is closable
- [ ] Table shows all isolation points with computed 通/盲/未知 status
- [ ] Filter by device/area narrows results
- [ ] Filter by status (通/盲/未知) narrows results
- [ ] Abnormal rows are highlighted (warning color)
- [ ] Abnormal-only filter checkbox shows only abnormal rows
- [ ] Click row → status history dialog opens with timeline
- [ ] History timeline shows operation date, type, blind plate code, status change

## Verification Checklist (Backend)
- [ ] GET `/api/v1/blind-spot-status` returns list of BlindSpotStatusDTO
- [ ] Filter by locationId returns only that subtree
- [ ] Filter by status returns matching rows
- [ ] Filter by abnormalOnly=true returns only flagged rows
- [ ] GET `/api/v1/blind-spot-status/{locationId}/history` returns operation timeline
- [ ] All endpoints return 401 without valid token
- [ ] No new DB table created (computed from existing entities)

## Edge Cases
- [ ] Location with no operations → status 未知, no abnormal flag
- [ ] Location with only INSPECT operations (no INSTALL/REMOVE) → status 未知
- [ ] Operation with status != completed → ignored in derivation
- [ ] Multiple INSTALL without REMOVE → status conflict flagged abnormal
- [ ] Large plant (1000+ locations) → response time acceptable (index on locationId)
