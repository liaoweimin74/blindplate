# Blind Spot Status Ledger - Design

## 1. Purpose
The blind spot status ledger (点位通盲状态台账) provides a plant-wide read-only overview of every isolation point's current pass/blind (通/盲) status. Status is computed in real time from operation order history - no persistent status table is introduced. The ledger supports filtering, abnormal-state highlighting, and per-point status change timeline tracing.

## 2. Routes
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/blind-spot-status` | List all isolation points with computed status (supports filters) |
| GET | `/api/v1/blind-spot-status/{locationId}/history` | Status change timeline for a specific isolation point |

## 3. Data Model (Computed DTO - no persistent table)

### BlindSpotStatusDTO
```java
public class BlindSpotStatusDTO {
    private Long locationId;           // 隔离点ID (FK -> Location)
    private String locationName;       // 隔离点名称
    private String locationType;       // 位置类型 (装置/单元/隔离点)
    private String parentPath;         // 装置/区域路径 (e.g. "一装置>裂解单元>点A")
    private String currentStatus;      // 当前通盲状态: 通/盲/盲板已拆除/未知
    private Long currentBlindPlateId;  // 当前盲板ID (FK -> BlindPlate)
    private String currentBlindPlateCode;  // 当前盲板编号
    private String currentBlindPlateModel; // 当前盲板型号
    private Boolean removable;         // 当前盲板是否可移除
    private Long relatedOperationOrderId;  // 关联作业票ID (最近一次操作)
    private String relatedOrderNo;     // 关联作业票编号
    private LocalDateTime lastOperationTime;  // 最近操作时间
    private Double statusDurationHours;   // 状态持续时长(小时)
    private Boolean abnormal;          // 异常状态标识
    private String abnormalDescription;    // 异常描述
}
```

### StatusHistoryDTO
```java
public class StatusHistoryDTO {
    private Long operationOrderId;
    private String orderNo;
    private String operationType;      // INSTALL / REMOVE / INSPECT
    private Long blindPlateId;
    private String blindPlateCode;
    private String blindPlateModel;
    private LocalDateTime operationTime;
    private String resultingStatus;    // 该操作导致的状态: 通/盲/未知
    private String operator;           // 操作人 (from order, if available)
}
```

## 4. Status Derivation Logic

### Current Status Derivation (per location)
```
1. Query OperationOrder WHERE locationId = X AND status = 'completed' AND type IN ('INSTALL','REMOVE')
   ORDER BY actualDate DESC
2. If no records -> status = "未知"
3. If latest = INSTALL -> status = "盲" (blind, plate blocking flow)
4. If latest = REMOVE -> status = "通" (pass, plate removed)
5. currentBlindPlateId = the blind plate from the latest INSTALL operation
   (if latest is REMOVE, currentBlindPlateId = the plate that was removed, marked as removed)
6. statusDurationHours = (now - latest.actualDate) in hours
```

### Abnormal Detection Rules
| Rule | Condition | Abnormal Description |
|------|-----------|---------------------|
| 长期挂盲板未拆除 | status=盲 AND statusDurationHours > 720 (30 days) | "盲板已挂载超过30天未拆除" |
| 状态冲突 | Two consecutive INSTALL operations without intervening REMOVE | "存在连续安装操作无拆除记录，状态冲突" |
| 无操作记录 | status=未知 AND location is leaf-level isolation point | (not flagged abnormal, just unknown) |

Abnormal threshold constant: `ABNORMAL_BLIND_DURATION_HOURS = 720` (30 days) - defined in service, configurable in future P1.

## 5. API Endpoints Detail

### GET /api/v1/blind-spot-status
**Query Parameters:**
| Param | Type | Description |
|-------|------|-------------|
| locationId | Long | Filter by specific location (includes subtree) |
| status | String | Filter by status: 通/盲/未知 |
| abnormalOnly | Boolean | If true, return only abnormal rows |

**Response:** `Result<List<BlindSpotStatusDTO>>`

### GET /api/v1/blind-spot-status/{locationId}/history
**Path Parameter:** locationId (Long)
**Response:** `Result<List<StatusHistoryDTO>>`

Returns all INSTALL/REMOVE/INSPECT operations on this location ordered by actualDate DESC, each annotated with the resulting status.

## 6. Backend File Structure
```
com.mangban.blindspotstatus/
├── controller/
│   └── BlindSpotStatusController.java
├── service/
│   └── BlindSpotStatusService.java
├── dto/
│   ├── BlindSpotStatusDTO.java
│   └── StatusHistoryDTO.java
└── (no entity, no repository - uses LocationRepository + OperationOrderRepository)
```

### Key Dependencies (existing, injected)
- `LocationRepository` - to list isolation points and traverse hierarchy
- `OperationOrderRepository` - to query operation history (NEW method: `findByLocationIdAndStatusAndTypeInOrderByActualDateDesc`)
- `BlindPlateRepository` - to resolve blind plate code/model from blindPlateId

### OperationOrderRepository Addition
```java
List<OperationOrder> findByLocationIdAndStatusAndTypeInOrderByActualDateDesc(
    Long locationId, String status, List<String> types);
```

## 7. Frontend File Structure
```
src/
├── api/
│   └── blindspotstatus.ts          # API module
├── types/
│   └── index.ts                    # Add BlindSpotStatus, StatusHistoryItem, BlindSpotStatusFilter
├── views/
│   └── blindspotstatus/
│       └── BlindSpotStatusList.vue # Main ledger page
├── router/
│   └── index.ts                    # Add route
├── components/
│   └── AppSidebar.vue              # Add menu item
└── locales/
    ├── zh-CN.json                  # Add menu.page.table keys
    └── en.json                     # Add menu.page.table keys
```

## 8. Frontend Page Design (BlindSpotStatusList.vue)

### Layout
```
[Page Header: 通盲状态台账 / subtitle]
[Card]
  [Header: Filter row]
    [Device/Area tree-select] [Status select] [Abnormal-only switch] [Refresh button]
  [Table]
    | 隔离点 | 位置路径 | 当前状态 | 当前盲板 | 关联作业票 | 最近操作时间 | 持续时长 | 异常 | 操作 |
    | (abnormal rows highlighted with warning row-class-name)
  [Row click -> Status History Dialog]
```

### Status History Dialog
- el-dialog showing el-timeline of StatusHistoryDTO items
- Each timeline node: operation time, type (安装/拆除/检验), blind plate code, resulting status

### Status Display
| Status | Tag Color |
|--------|-----------|
| 通 | success (green) |
| 盲 | danger (red) |
| 盲板已拆除 | warning (orange) |
| 未知 | info (gray) |

### Abnormal Highlighting
- `row-class-name` callback: if `row.abnormal` -> `abnormal-row` (CSS: background `var(--el-color-warning-light-9)`)

## 9. Route & Menu
- Route: `/blind-spot-status` -> `BlindSpotStatusList.vue`, child of AppLayout
- Meta: `{ requiresAuth: true, titleKey: 'menu.blindSpotStatus', closable: true }`
- Sidebar menu item: "通盲状态台账" with icon `View`, placed after 位置管理 (locations)

## 10. i18n Keys (zh-CN.json additions)
```json
{
  "menu": {
    "blindSpotStatus": "通盲状态台账"
  },
  "page": {
    "blindSpotStatus": "点位通盲状态台账",
    "blindSpotStatusSubtitle": "全厂隔离点通盲状态实时总览"
  },
  "table": {
    "location": "隔离点",
    "locationPath": "位置路径",
    "currentStatus": "当前状态",
    "currentBlindPlate": "当前盲板",
    "relatedOrder": "关联作业票",
    "lastOperationTime": "最近操作时间",
    "duration": "持续时长",
    "abnormal": "异常",
    "operation": "操作"
  },
  "filter": {
    "deviceArea": "装置/区域",
    "status": "状态",
    "abnormalOnly": "仅看异常"
  },
  "status": {
    "pass": "通",
    "blind": "盲",
    "removed": "盲板已拆除",
    "unknown": "未知"
  }
}
```

## 11. Tech Stack
- Backend: Spring Boot 3 + JPA/Hibernate + Lombok (existing)
- Frontend: Vue 3 + Element Plus + Pinia + TypeScript + vue-i18n (existing)
- No new dependencies required
