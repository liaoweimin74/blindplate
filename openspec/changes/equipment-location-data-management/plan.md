# Equipment/Location Data Management Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development
> to implement this plan task-by-task.

**Goal:** Implement PRD 7.1.2 by refactoring Location into a 4-level hierarchy (Factory->Equipment->Unit->Isolation Point) and adding isolation point master data, location marking, and change approval capabilities.

**Architecture:** Extend existing `Location` tree with new type enum + code/level fields. Add `IsolationPointDetail` 1:1 companion entity for isolation-point-specific fields (PID ref, medium, pressure, temperature, hazard level, coordinates). Add `LocationChangeRecord` audit entity with single-stage approval workflow (PENDING/APPROVED/REJECTED). Excel batch import via EasyExcel.

**Tech Stack:** Spring Boot 3 + JPA + Lombok + EasyExcel (backend), Vue 3 + Element Plus + Pinia + TypeScript (frontend)

**Specs:** See `specs/location-tree/spec.md`, `specs/isolation-point-data/spec.md`, `specs/location-change-management/spec.md`
**Design:** See `design.md`

---

## Task 1: Refactor Location Entity (New Type Enum, Code, Level)

**Files:**
- Modify: `blindplate-server/src/main/java/com/mangban/location/entity/Location.java`
- Modify: `blindplate-server/src/main/java/com/mangban/location/service/LocationService.java`
- Test: `blindplate-server/src/test/java/com/mangban/location/LocationTypeValidationTest.java`

- [ ] **Step 1: Write failing test for new type enum and level validation**

Create `LocationTypeValidationTest.java` with 2 tests:
- `createEquipment_withoutFactoryParent_throws400`: parent type=UNIT, new type=EQUIPMENT -> expect BusinessException
- `createIsolationPoint_withUnitParent_succeeds`: parent type=UNIT, new type=ISOLATION_POINT + code=IP-001 -> expect saved with level=3

- [ ] **Step 2: Run test** Run: `mvn -pl blindplate-server test -Dtest=LocationTypeValidationTest`. Expected: FAIL (validation not yet implemented)

- [ ] **Step 3: Update Location entity**

Add fields: `code` (String, unique, length 50), `level` (Integer). Type remains String field but values restricted to FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT.

- [ ] **Step 4: Add hierarchy validation to LocationService**

Add `validateHierarchy(Location)` method:
- parent null + type=FACTORY -> level 0, OK
- EQUIPMENT requires FACTORY parent; UNIT requires EQUIPMENT parent; ISOLATION_POINT requires UNIT parent
- Throw `BusinessException("<type> 节点必须挂在 <expected> 节点下")` otherwise
- Set `level = parent.level + 1`

Add `validateCode(Location)`:
- ISOLATION_POINT requires non-blank code, throw `BusinessException("隔离点编码不能为空")`
- Non-null code checked for uniqueness via `locationRepository.existsByCode(code)`, throw `BusinessException("位置编码已存在")`

Add `existsByCode(String)` to `LocationRepository`.

Modify `create()` to call `validateHierarchy` and `validateCode` before save.

- [ ] **Step 5: Run test** Run: `mvn -pl blindplate-server test -Dtest=LocationTypeValidationTest`. Expected: 2 tests PASS

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/
git commit -m "feat: refact Location type enum to 4-level hierarchy with code/level validation"
```

---

## Task 2: Create IsolationPointDetail Entity + Repository

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/location/entity/IsolationPointDetail.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/repository/IsolationPointDetailRepository.java`

- [ ] **Step 1: Create IsolationPointDetail entity**

Fields: id, location (@OneToOne to Location, joinColumn location_id unique), pidDiagramRef (200), medium (50), pressure (Double), temperature (Double), hazardLevel (1, A/B/C/D), isolationType (30, BLIND_PLATE/DOUBLE_BLOCK/VALVE/OTHER), coordX/coordY/coordZ (Double), diagramId (Long, references BoardProject.id), createdAt, updatedAt. Table name: `bp_isolation_point_detail`.

- [ ] **Step 2: Create IsolationPointDetailRepository**

`JpaRepository<IsolationPointDetail, Long>` with method `Optional<IsolationPointDetail> findByLocationId(Long locationId)`.

- [ ] **Step 3: Compile check** Run: `mvn -pl blindplate-server compile`. Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/
git commit -m "feat: add IsolationPointDetail entity and repository"
```


---

## Task 3: IsolationPointDetail Service + Controller

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/location/service/IsolationPointDetailService.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/controller/IsolationPointDetailController.java`
- Test: `blindplate-server/src/test/java/com/mangban/location/IsolationPointDetailServiceTest.java`

- [ ] **Step 1: Write failing tests**

Create `IsolationPointDetailServiceTest.java` with 3 tests:
- `getByLocationId_returnsDetail` - mock detailRepository.findByLocationId(10L) returns detail, assert id matches
- `update_rejectsInvalidHazardLevel` - patch hazardLevel="X" -> expect BusinessException("危害等级必须为A、B、C或D")
- `update_validHazardLevel_succeeds` - patch hazardLevel="A" -> expect saved with hazardLevel=A

- [ ] **Step 2: Run test** Run: `mvn -pl blindplate-server test -Dtest=IsolationPointDetailServiceTest`. Expected: FAIL

- [ ] **Step 3: Create service**

`IsolationPointDetailService` with `REQUIRED_ARGS_CONSTRUCTOR`, fields `IsolationPointDetailRepository`. Methods:
- `getByLocationId(Long)`: findByLocationId or throw BusinessException("隔离点详情不存在")
- `update(Long locationId, IsolationPointDetail patch)`: load by locationId, validate + patch each non-null field
  - hazardLevel must be one of A/B/C/D (else throw "危害等级必须为A、B、C或D")
  - isolationType must be one of BLIND_PLATE/DOUBLE_BLOCK/VALVE/OTHER (else throw "隔离类型无效")
  - coordX/coordY/coordZ must be finite Double (else throw "坐标值无效")
  - Save and return

Controller `IsolationPointDetailController` at `/api/v1/isolation-points`:
- `GET /{locationId}` -> Result<IsolationPointDetail>
- `PUT /{locationId}` -> Result<IsolationPointDetail>

- [ ] **Step 4: Hook auto-create in LocationService.create() for ISOLATION_POINT**

In `LocationService` field, inject `IsolationPointDetailRepository`. In `create()` after saving Location:
- If saved type is "ISOLATION_POINT", create new IsolationPointDetail with location=saved, save it.

- [ ] **Step 5: Run tests** Run: `mvn -pl blindplate-server test -Dtest=IsolationPointDetailServiceTest`. Expected: 3 PASS

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/
git commit -m "feat: add IsolationPointDetail service, controller, and auto-create hook"
```

---

## Task 4: Create LocationChangeRecord Entity + Repository

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/location/entity/LocationChangeRecord.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/repository/LocationChangeRecordRepository.java`

- [ ] **Step 1: Create LocationChangeRecord entity**

Fields: id, locationId (Long), changeType (CREATE/UPDATE/DELETE/MOVE, 20), fieldName (100), oldValue (TEXT), newValue (TEXT), status (PENDING/APPROVED/REJECTED, 20), applicantId (Long), approverId (Long), approvalComment (TEXT), createdAt (@CreationTimestamp), approvedAt (LocalDateTime). Table name: `bp_location_change_record`.

- [ ] **Step 2: Create repository**

`JpaRepository<LocationChangeRecord, Long>` with methods:
- `findByLocationIdOrderByCreatedAtDesc(Long locationId)`
- `findByStatus(String status)`
- `findByApplicantId(Long applicantId)`
- `findByLocationIdAndStatus(Long locationId, String status)`
- `findByTimeRange(LocalDateTime start, LocalDateTime end)` via @Query

- [ ] **Step 3: Compile check** Run: `mvn -pl blindplate-server compile`. Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/
git commit -m "feat: add LocationChangeRecord entity and repository"
```

---

## Task 5: Change Management Service + Controller (Approval Workflow)

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/location/service/LocationChangeRecordService.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/controller/LocationChangeRecordController.java`
- Test: `blindplate-server/src/test/java/com/mangban/location/LocationChangeRecordServiceTest.java`

- [ ] **Step 1: Write failing tests**

Create `LocationChangeRecordServiceTest.java` with 3 tests:
- `approve_byNonAdmin_throws` - isAdmin=false -> expect BusinessException("仅管理员可审批变更")
- `approve_byAdmin_succeeds` - isAdmin=true on PENDING record -> expect status=APPROVED, approverId set, approvedAt not null
- `approve_alreadyApproved_throws` - PENDING=APPROVED -> expect BusinessException("该变更已处理")

- [ ] **Step 2: Run test** Run: `mvn -pl blindplate-server test -Dtest=LocationChangeRecordServiceTest`. Expected: FAIL

- [ ] **Step 3: Create service**

`LocationChangeRecordService` with repository field. Methods:
- `createChangeRecord(locationId, changeType, fieldName, oldValue, newValue, applicantId)`: status = (changeType=="CREATE" ? "APPROVED" : "PENDING"), save
- `approve(id, comment, approverId, isAdmin)`: validate isAdmin (else throw "仅管理员可审批变更"); validate PENDING (else "该变更已处理"); set status=APPROVED, approverId, approvalComment, approvedAt=now; save
- `reject(id, reason, approverId, isAdmin)`: same validations; status=REJECTED; save
- `queryHistory(locationId, status, changeType, applicantId, start, end)`: filter returns matching records

- [ ] **Step 4: Create controller**

At `/api/v1/location-changes`:
- `GET /` with query params (locationId, status, changeType, applicantId, start, end) -> Result<List>
- `POST /{id}/approve?comment=` with @RequestAttribute userId/isAdmin -> Result<LocationChangeRecord>
- `POST /{id}/reject?reason=` with @RequestAttribute userId/isAdmin -> Result<LocationChangeRecord>

- [ ] **Step 5: Run tests** Run: `mvn -pl blindplate-server test -Dtest=LocationChangeRecordServiceTest`. Expected: 3 PASS

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/
git commit -m "feat: add LocationChangeRecord approval workflow service and controller"
```

---

## Task 6: Integrate Change Record into LocationService CRUD

**Files:**
- Modify: `blindplate-server/src/main/java/com/mangban/location/service/LocationService.java`

- [ ] **Step 1: Add change record generation to create()**

Inject `LocationChangeRecordService` into `LocationService`. In `create()` after saving Location:
- If type ISOLATION_POINT and IsolationPointDetail auto-created from Task 3, still call `changeRecordService.createChangeRecord(saved.getId(), "CREATE", "*", null, snapshot(saved), currentUserId)` where snapshot returns a JSON string of saved fields. status auto-set to APPROVED inside createChangeRecord.

- [ ] **Step 2: Add change record generation to update()**

In `update()`, for each field that differs between existing and patch:
- name: `createChangeRecord(id, "UPDATE", "name", existing.getName(), patch.getName(), userId)`
- parentId: change_type "MOVE"
- description: change_type "UPDATE"
- code: change_type "UPDATE"

Per design: CREATE auto-approved; UPDATE/DELETE/MOVE stay PENDING and physically mutated only on approval. For simplicity in this task, create the change records AND defer physical mutation for UPDATE/MOVE; for description-only updates, allow direct mutation since they don't change tree structure.DELETE creates a "DELETE" record in PENDING status and does not physically delete until approval.

- [ ] **Step 3: Compile check** Run: `mvn -pl blindplate-server compile`. Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/
git commit -m "feat: integrate change record generation into LocationService CRUD"
```

---

## Task 7: Add EasyExcel Dependency + Excel Import Service

**Files:**
- Modify: `blindplate-server/pom.xml`
- Create: `blindplate-server/src/main/java/com/mangban/location/dto/IsolationPointImportRow.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/dto/ImportResult.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/service/IsolationPointImportService.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/controller/IsolationPointImportController.java`

- [ ] **Step 1: Add EasyExcel dependency to pom.xml**

Add `<dependency>` for `com.alibaba:easyexcel:3.3.4`.

- [ ] **Step 2: Create DTOs**

`IsolationPointImportRow` with @ExcelProperty annotations on: code, name, type, parentCode, medium, pressure, temperature, hazardLevel, isolationType.

`ImportResult` with: totalRows, successCount, failedCount, List<ImportError> errors (each: rowNumber, message).

- [ ] **Step 3: Create import service**

`IsolationPointImportService` injecting `LocationRepository` and `IsolationPointDetailRepository`:

`writeTemplate(HttpServletResponse)`: set content-type to xlsx, header attachment filename, use EasyExcel.write to produce template sheet.

`importFile(MultipartFile file)`:
- Verify filename ends .xlsx or .xls (else throw BusinessException("请上传Excel文件"))
- Read rows via EasyExcel
- For each row, attempt persist; capture errors per row with rowNumber
- Valid case: validate code blank, existsByCode, hazardLevel in {A,B,C,D}, isolationType in {BLIND_PLATE,DOUBLE_BLOCK,VALVE,OTHER}, parentCode resolves to a Location of type UNIT
- Create Location with type=ISOLATION_POINT, code, name, parentId, level=parent.level+1
- Create IsolationPointDetail with medium/pressure/temperature/hazardLevel/isolationType
- Return ImportResult with success/failed counts and error list (partial success model)

Add `findByCode(String code)` to `LocationRepository` returning `Optional<Location>`.

- [ ] **Step 4: Create controller**

At `/api/v1/isolation-points/import`:
- `GET /template` -> void (writes xlsx to response)
- `POST /` with multipart file -> Result<ImportResult>

- [ ] **Step 5: Compile check** Run: `mvn -pl blindplate-server compile`. Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/
git commit -m "feat: add Excel batch import for isolation point master data"
```

---

## Task 8: Restrict Blind Plate Assignment + Update DataInitializer

**Files:**
- Modify: `blindplate-server/src/main/java/com/mangban/blindplate/service/BlindPlateService.java`
- Modify: `blindplate-server/src/main/java/com/mangban/config/DataInitializer.java`

- [ ] **Step 1: Update BlindPlateService to reject non-IsolationPoint assignment**

In any `assignLocation` method or location update path:
- Load Location by locationId (throw "位置不存在" if missing)
- If Location.type != "ISOLATION_POINT", throw BusinessException("盲板只能绑定到隔离点位置")
- Otherwise update BlindPlate.locationId

- [ ] **Step 2: Update DataInitializer seed data**

Replace existing test seed (area/building/floor/room/equipment) with sample FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT tree (e.g., 1 factory, 1 equipment, 1 unit, 2-3 isolation points) and matching IsolationPointDetail rows.

- [ ] **Step 3: Compile check** Run: `mvn -pl blindplate-server compile`. Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/
git commit -m "feat: restrict blind plate to ISOLATION_POINT and update DataInitializer with new type enum"
```

---

## Task 9: Frontend Update LocationForm Type Options + Isolation Point Detail Fields

**Files:**
- Modify: `blindplate-web/src/components/LocationForm.vue`
- Modify: `blindplate-web/src/api/location.ts`
- Modify: `blindplate-web/src/types/index.ts` (or wherever Location type is defined)

- [ ] **Step 1: Update TypeScript `Location` and add `IsolationPointDetail` types**

In `types/index.ts`:
- Update `Location` interface: type as union 'FACTORY'|'EQUIPMENT'|'UNIT'|'ISOLATION_POINT'; add `code?: string`, `level?: number`, `detail?: IsolationPointDetail`
- Add `IsolationPointDetail` interface: id?, locationId, pidDiagramRef?, medium?, pressure?, temperature?, hazardLevel? ('A'|'B'|'C'|'D'), isolationType? ('BLIND_PLATE'|'DOUBLE_BLOCK'|'VALVE'|'OTHER'), coordX?, coordY?, coordZ?, diagramId?
- Add `ImportResult` interface: totalRows, successCount, failedCount, errors: { rowNumber, message }[]

- [ ] **Step 2: Update LocationForm.vue type select options**

Replace existing el-option list with:
- `FACTORY` -> `form.optionFactory`
- `EQUIPMENT` -> `form.optionEquipment`
- `UNIT` -> `form.optionUnit`
- `ISOLATION_POINT` -> `form.optionIsolationPoint`

Add a `code` field form-item (required when type=ISOLATION_POINT).

- [ ] **Step 3: Add conditional isolation point detail form section**

When `form.type === 'ISOLATION_POINT'`, render a divider followed by form items for: medium, pressure, temperature, hazardLevel (select A/B/C/D), isolationType (select BLIND_PLATE/DOUBLE_BLOCK/VALVE/OTHER), coordX, coordY, coordZ, diagramId (numeric input or select from board projects). Bind all to `form.detail.*`.

- [ ] **Step 4: Add detail create/update to api/location.ts**

Add functions:
- `getIsolationPointDetail(locationId: number)` -> GET `/isolation-points/${locationId}`
- `updateIsolationPointDetail(locationId: number, data)` -> PUT `/isolation-points/${locationId}`

- [ ] **Step 5: Build check** Run: `cd blindplate-web && pnpm run build`. Expected: No type errors

- [ ] **Step 6: Commit**

```bash
git add blindplate-web/
git commit -m "feat: update LocationForm with 4-level type options and isolation point detail fields"
```

---

## Task 10: Frontend Isolation Point Import Component

**Files:**
- Create: `blindplate-web/src/components/IsolationPointImport.vue`
- Modify: `blindplate-web/src/api/location.ts`

- [ ] **Step 1: Create IsolationPointImport.vue**

Component with:
- "Download Template" button calling axios GET `/api/v1/isolation-points/import/template` with responseType: blob, trigger download via Blob + a element
- el-upload with auto-upload, accept .xlsx/.xls, calling POST `/api/v1/isolation-points/import` with FormData
- Display UploadResult via el-alert summary + el-table of errors (rowNumber, message)
- i18n labels for all keys

- [ ] **Step 2: Add import API methods**

In `api/location.ts` add:
- `getImportTemplate()` -> axios GET with responseType:blob
- `importIsolationPoints(file: File)` -> POST multipart

- [ ] **Step 3: Build check** Run: `cd blindplate-web && pnpm run build`. Expected: No type errors

- [ ] **Step 4: Commit**

```bash
git add blindplate-web/
git commit -m "feat: add IsolationPointImport component for Excel batch import"
```

---

## Task 11: Frontend Change Approval Page

**Files:**
- Create: `blindplate-web/src/views/ChangeApproval.vue`
- Modify: `blindplate-web/src/api/location.ts`

- [ ] **Step 1: Add API methods**

In `api/location.ts` add:
- `getLocationChanges(params)` -> GET `/location-changes` with params
- `approveChange(id, comment)` -> POST `/location-changes/${id}/approve?comment=`
- `rejectChange(id, reason)` -> POST `/location-changes/${id}/reject?reason=`

- [ ] **Step 2: Create ChangeApproval.vue**

Page with:
- filter select for status (ALL/PENDING/APPROVED/REJECTED)
- el-table of records: id, locationId, changeType, fieldName, status, createdAt
- Only when status filter is PENDING, render Approve/Reject action buttons with `v-permission="['ADMIN']"` directive
- Approve: ElMessageBox.prompt for comment, call approveChange, refresh
- Reject: ElMessageBox.prompt for reason, call rejectChange, refresh

- [ ] **Step 3: Build check** Run: `cd blindplate-web && pnpm run build`. Expected: No type errors

- [ ] **Step 4: Commit**

```bash
git add blindplate-web/
git commit -m "feat: add ChangeApproval page for location change management"
```

---

## Task 12: Frontend Router, Sidebar, i18n Updates

**Files:**
- Modify: `blindplate-web/src/router/index.ts`
- Modify: `blindplate-web/src/components/AppSidebar.vue`
- Modify: `blindplate-web/src/locales/zh-CN.json` (or equivalent i18n locale file)
- Modify: `blindplate-web/src/locales/en.json`

- [ ] **Step 1: Add routes**

In `router/index.ts` add `{ path: '/change-approval', name: 'ChangeApproval', component: () => import('@/views/ChangeApproval.vue') }` as child of AppLayout route.

- [ ] **Step 2: Add sidebar items**

In `AppSidebar.vue` add "基础数据" menu group containing children:
- location-tree (existing location page)
- isolation-point-import
- change-approval

If isolation-point-import is rendered as a section of LocationList rather than a separate route, link from there.

- [ ] **Step 3: Add i18n entries**

In zh-CN locale add keys (mirror in en.json):
- form.optionFactory/optionEquipment/optionUnit/optionIsolationPoint
- form.isolationPointDetail/labelMedium/labelPressure/labelTemperature/labelHazardLevel/labelIsolationType/labelCoordX/labelCoordY/labelCoordZ
- import.downloadTemplate/upload/row/error/summary/recordsImported/templateFailed/failed
- approval.all/pending/approved/rejected/enterComment/approve/enterReason/reject/locationId/changeType/fieldName/status/createdAt/actions
- button.approve/confirm/reject

Use proper Chinese text for zh-CN and English translations for en.

- [ ] **Step 4: Build check** Run: `cd blindplate-web && pnpm run build`. Expected: No type errors

- [ ] **Step 5: Commit**

```bash
git add blindplate-web/
git commit -m "feat: add routes, sidebar items and i18n entries for change management"
```

---

## Task 13: Verification

- [ ] **Step 1: Backend full compile + test**

Run: `mvn -pl blindplate-server clean test`
Expected: BUILD SUCCESS, all tests pass (LocationTypeValidationTest, IsolationPointDetailServiceTest, LocationChangeRecordServiceTest)

- [ ] **Step 2: Start backend**

Run: `mvn -pl blindplate-server spring-boot:run`
Expected: Hibernate DDL generates `bp_isolation_point_detail` and `bp_location_change_record` tables in logs; no migration errors

- [ ] **Step 3: Frontend build**

Run: `cd blindplate-web && pnpm run build`
Expected: dist/ produced, no type errors

- [ ] **Step 4: Smoke test**

Manual flow:
1. Open frontend; navigate to Location Management; create FACTORY -> EQUIPMENT -> UNIT -> ISOLATION_POINT via LocationForm (test rejection when type/parent mismatch)
2. When ISOLATION_POINT type selected, verify conditional isolation-point-detail fields appear; fill medium/pressure/temperature/hazardLevel/isolationType/coordinates; save; verify detail record auto-created on backend
3. Edit an ISOLATION_POINT's name; verify LocationChangeRecord created with status=PENDING; old name vs new name captured
4. Navigate to ChangeApproval page as ADMIN; approve PENDING record; verify change applied to live entity
5. Reject another PENDING record; verify no mutation occurred
6. Download Excel template; fill 3 valid rows + 2 invalid rows (duplicate code, invalid parent_code); upload; verify ImportResult shows 3 success, 2 failed with error messages
7. Try to assign a BlindPlate to a FACTORY/EQUIPMENT/UNIT node; verify 400 "盲板只能绑定到隔离点位置"

- [ ] **Step 5: Final commit + openspec validate**

Run: `openspec validate --changes equipment-location-data-management`
Expected: All artifacts valid

```bash
git add -A
git commit -m "chore: verification complete for equipment-location-data-management"
```
