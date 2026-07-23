# Blind Spot Status Ledger - Tasks

## Backend Tasks

### B1: OperationOrderRepository - Add query method
- [ ] Add method `findByLocationIdAndStatusAndTypeInOrderByActualDateDesc(Long locationId, String status, List<String> types)` to `OperationOrderRepository`
- [ ] Add method `findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(List<Long> locationIds, String status, List<String> types)` for batch query (avoid N+1)
- File: `blindplate-server/src/main/java/com/mangban/operation/repository/OperationOrderRepository.java`

### B2: BlindSpotStatusDTO + StatusHistoryDTO
- [ ] Create `BlindSpotStatusDTO` class with fields: locationId, locationName, locationType, parentPath, currentStatus, currentBlindPlateId, currentBlindPlateCode, currentBlindPlateModel, removable, relatedOperationOrderId, relatedOrderNo, lastOperationTime, statusDurationHours, abnormal, abnormalDescription
- [ ] Create `StatusHistoryDTO` class with fields: operationOrderId, orderNo, operationType, blindPlateId, blindPlateCode, blindPlateModel, operationTime, resultingStatus, operator
- [ ] Use Lombok `@Data`, no JPA annotations (pure DTO)
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/dto/`

### B3: BlindSpotStatusService - Status derivation logic
- [ ] Create `BlindSpotStatusService` with `@Service @RequiredArgsConstructor`
- [ ] Inject `LocationRepository`, `OperationOrderRepository`, `BlindPlateRepository`
- [ ] Implement `List<BlindSpotStatusDTO> getStatusList(Long locationId, String status, Boolean abnormalOnly)`:
  - Get target locations (all, or subtree of locationId)
  - Batch-query completed INSTALL/REMOVE operations for all target locations (avoid N+1)
  - For each location, derive current status from latest operation
  - Compute statusDurationHours from latest operation's actualDate
  - Apply abnormal detection rules (long-term blind >720h, status conflict)
  - Filter by status and abnormalOnly if provided
- [ ] Implement `List<StatusHistoryDTO> getHistory(Long locationId)`:
  - Query all INSTALL/REMOVE/INSPECT operations for location, ordered by actualDate DESC
  - Resolve blind plate code/model for each
  - Annotate resulting status per operation
- [ ] Define constant `ABNORMAL_BLIND_DURATION_HOURS = 720`
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/service/BlindSpotStatusService.java`

### B4: BlindSpotStatusController - REST endpoints
- [ ] Create `BlindSpotStatusController` with `@RestController @RequestMapping("/api/v1/blind-spot-status") @RequiredArgsConstructor`
- [ ] `GET ""` -> `Result<List<BlindSpotStatusDTO>>` with `@RequestParam` locationId, status, abnormalOnly
- [ ] `GET "/{locationId}/history"` -> `Result<List<StatusHistoryDTO>>`
- [ ] All endpoints require auth (existing SecurityConfig covers `/api/v1/**`)
- File: `blindplate-server/src/main/java/com/mangban/blindspotstatus/controller/BlindSpotStatusController.java`

## Frontend Tasks

### F1: TypeScript types
- [ ] Add `BlindSpotStatus` interface (matches DTO fields)
- [ ] Add `StatusHistoryItem` interface (matches history DTO fields)
- [ ] Add `BlindSpotStatusFilter` interface { locationId?, status?, abnormalOnly? }
- File: `blindplate-web/src/types/index.ts`

### F2: API module
- [ ] Create `api/blindspotstatus.ts` with `getBlindSpotStatusList(filter)` and `getBlindSpotStatusHistory(locationId)`
- [ ] Use existing `request` axios instance (baseURL `/api/v1`)
- File: `blindplate-web/src/api/blindspotstatus.ts`

### F3: i18n keys
- [ ] Add `menu.blindSpotStatus`, `page.blindSpotStatus`, `page.blindSpotStatusSubtitle`
- [ ] Add `table.*` keys (location, locationPath, currentStatus, currentBlindPlate, relatedOrder, lastOperationTime, duration, abnormal, operation)
- [ ] Add `filter.*` keys (deviceArea, status, abnormalOnly)
- [ ] Add `status.*` keys (pass, blind, removed, unknown)
- [ ] Add to both `zh-CN.json` and `en.json`
- File: `blindplate-web/src/locales/zh-CN.json`, `blindplate-web/src/locales/en.json`

### F4: Route + sidebar menu
- [ ] Add route `/blind-spot-status` -> `BlindSpotStatusList.vue` as child of AppLayout
- [ ] Meta: `{ requiresAuth: true, titleKey: 'menu.blindSpotStatus', closable: true }`
- [ ] Add sidebar menu item "通盲状态台账" with `View` icon, placed after 位置管理 (locations)
- File: `blindplate-web/src/router/index.ts`, `blindplate-web/src/components/AppSidebar.vue`

### F5: BlindSpotStatusList.vue - Main ledger page
- [ ] Page header with title + subtitle (PageHeader pattern)
- [ ] el-card with filter row: location tree-select (el-tree-select), status el-select, abnormal-only el-switch, refresh button
- [ ] el-table with columns: 隔离点, 位置路径, 当前状态 (el-tag colored), 当前盲板, 关联作业票, 最近操作时间, 持续时长, 异常 (el-tag), 操作 (查看历史)
- [ ] `row-class-name` callback -> `abnormal-row` class for highlighted rows
- [ ] Loading state with `v-loading`
- [ ] On mount: call API, populate table
- [ ] Filter changes: re-fetch or client-side filter
- File: `blindplate-web/src/views/blindspotstatus/BlindSpotStatusList.vue`

### F6: Status History Dialog
- [ ] el-dialog with el-timeline showing StatusHistoryItem entries
- [ ] Each timeline node: operation time, type tag (安装/拆除/检验), blind plate code, resulting status tag
- [ ] Empty state when no history
- [ ] Can be inline in BlindSpotStatusList.vue or separate component
- File: `blindplate-web/src/views/blindspotstatus/BlindSpotStatusList.vue` (inline dialog)

## Task Dependency Graph
| Task | Est | Depends On |
|------|-----|------------|
| B1: Repository method | 0.5h | - |
| B2: DTOs | 0.5h | - |
| B3: Service | 2h | B1, B2 |
| B4: Controller | 0.5h | B3 |
| F1: Types | 0.5h | - |
| F2: API module | 0.5h | F1 |
| F3: i18n keys | 0.5h | - |
| F4: Route + menu | 0.5h | F3 |
| F5: List page | 2h | F2, F3, F4 |
| F6: History dialog | 1h | F5 |
| **Total** | **8h** | |
