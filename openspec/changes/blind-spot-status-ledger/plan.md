# Blind Spot Status Ledger - Implementation Plan

## Phase 1: Backend (Computed Status API)

### Step 1.1: DTOs
- Create `BlindSpotStatusDTO.java` and `StatusHistoryDTO.java` in `com.mangban.blindspotstatus.dto`
- Pure Lombok `@Data` classes, no JPA annotations
- Fields per design.md §3
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/dto/BlindSpotStatusDTO.java`
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/dto/StatusHistoryDTO.java`

### Step 1.2: Repository query methods
- Add to `OperationOrderRepository.java`:
  - `findByLocationIdAndStatusAndTypeInOrderByActualDateDesc(Long, String, List<String>)`
  - `findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(List<Long>, String, List<String>)`
- These enable single + batch location queries to avoid N+1
- File: `blindplate-server/src/main/java/com/mangban/operation/repository/OperationOrderRepository.java`

### Step 1.3: BlindSpotStatusService
- Create `BlindSpotStatusService.java` in `com.mangban.blindspotstatus.service`
- Inject `LocationRepository`, `OperationOrderRepository`, `BlindPlateRepository`
- Implement `getStatusList(Long locationId, String status, Boolean abnormalOnly)`:
  1. Get target locations (all or subtree of locationId via LocationRepository)
  2. Batch-query completed INSTALL/REMOVE operations: `findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(locationIds, "completed", ["INSTALL","REMOVE"])`
  3. Group operations by locationId, take latest per location
  4. Derive status: INSTALL->盲, REMOVE->通, none->未知
  5. Resolve blind plate code/model via BlindPlateRepository (batch `findAllById`)
  6. Compute statusDurationHours: `Duration.between(latest.actualDate, now).toHours()`
  7. Apply abnormal rules: blind>720h, consecutive INSTALLs
  8. Filter by status param and abnormalOnly param
- Implement `getHistory(Long locationId)`:
  1. Query all operations for location (INSTALL/REMOVE/INSPECT), status=completed, ordered DESC
  2. Resolve blind plate info per operation
  3. Annotate resultingStatus per operation type
- Constant: `private static final long ABNORMAL_BLIND_DURATION_HOURS = 720;`
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/service/BlindSpotStatusService.java`

### Step 1.4: BlindSpotStatusController
- Create `BlindSpotStatusController.java` in `com.mangban.blindspotstatus.controller`
- `@RestController @RequestMapping("/api/v1/blind-spot-status") @RequiredArgsConstructor`
- `GET ""` with `@RequestParam(required=false)` locationId, status, abnormalOnly -> `Result<List<BlindSpotStatusDTO>>`
- `GET "/{locationId}/history"` -> `Result<List<StatusHistoryDTO>>`
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/controller/BlindSpotStatusController.java`

### Step 1.5: Backend verification
- Run `cd blindplate-server && mvn compile` (or `./mvnw compile`) - exit code 0
- Confirm no new DB table (JPA ddl-auto unchanged, no entity class added)

## Phase 2: Frontend - Foundation

### Step 2.1: TypeScript types
- Add `BlindSpotStatus`, `StatusHistoryItem`, `BlindSpotStatusFilter` interfaces to `types/index.ts`
- Fields match DTO definitions in design.md §3

### Step 2.2: API module
- Create `api/blindspotstatus.ts`:
  ```typescript
  import request from './request'
  import type { BlindSpotStatus, StatusHistoryItem, BlindSpotStatusFilter } from '@/types'

  export function getBlindSpotStatusList(filter?: BlindSpotStatusFilter) {
    return request.get<any, { code: number; data: BlindSpotStatus[] }>('/blind-spot-status', { params: filter })
  }
  export function getBlindSpotStatusHistory(locationId: number) {
    return request.get<any, { code: number; data: StatusHistoryItem[] }>(`/blind-spot-status/${locationId}/history`)
  }
  ```

### Step 2.3: i18n keys
- Add keys to `zh-CN.json` and `en.json` per design.md §10

### Step 2.4: Route + sidebar menu
- Add route to `router/index.ts`:
  ```typescript
  {
    path: 'blind-spot-status',
    name: 'BlindSpotStatusList',
    component: () => import('@/views/blindspotstatus/BlindSpotStatusList.vue'),
    meta: { requiresAuth: true, titleKey: 'menu.blindSpotStatus', closable: true }
  }
  ```
- Add menu item to `AppSidebar.vue` after locations item:
  ```typescript
  { key: '/blind-spot-status', title: t('menu.blindSpotStatus'), icon: View, path: '/blind-spot-status' }
  ```

## Phase 3: Frontend - Ledger Page

### Step 3.1: BlindSpotStatusList.vue
- Page header (title + subtitle via `$t('page.blindSpotStatus')`)
- el-card with:
  - Filter row: el-tree-select (locations), el-select (status: 通/盲/未知), el-switch (abnormalOnly), el-button (refresh)
  - el-table with columns per design.md §8
  - Status column: el-tag with type by status (success/danger/info)
  - Abnormal column: el-tag warning if abnormal
  - `row-class-name`: return `abnormal-row` if `row.abnormal`
  - Operations column: el-button text "查看历史" -> opens dialog
- `onMounted` -> call `getBlindSpotStatusList()` -> populate table
- Watch filters -> re-fetch

### Step 3.2: Status History Dialog (inline)
- el-dialog with `v-model` visibility
- el-timeline with entries from `getBlindSpotStatusHistory(locationId)`
- Each timeline item: timestamp, el-tag for operation type (安装/拆除/检验), blind plate code, resulting status el-tag
- Empty state: el-empty with "暂无操作记录"

### Step 3.3: Abnormal row CSS
- Add scoped style:
  ```css
  :deep(.abnormal-row) {
    background-color: var(--el-color-warning-light-9);
  }
  ```

## Phase 4: Verification

### Step 4.1: Build verification
- `cd blindplate-web && npm run build` - exit code 0
- `cd blindplate-server && mvn compile` - exit code 0

### Step 4.2: Functional verification
- Run verify.md checklist items

## Parallel Execution Opportunities
- Phase 1 (backend) and Phase 2.1-2.3 (frontend types/api/i18n) are independent -> parallel
- Phase 2.4 (route/menu) depends on F3 (i18n keys) for titleKey
- Phase 3 (list page) depends on Phase 2 complete
- B1+B2 can be done in parallel; B3 depends on B1+B2; B4 depends on B3

## Estimation
| Phase | Est |
|-------|-----|
| Phase 1 (Backend) | 3.5h |
| Phase 2 (Frontend Foundation) | 2h |
| Phase 3 (Ledger Page) | 3h |
| Phase 4 (Verification) | 0.5h |
| **Total** | **9h** |
