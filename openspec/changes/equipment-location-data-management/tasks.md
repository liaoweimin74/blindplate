## 1. Backend Data Model

- [ ] 1.1 Refactor Location entity: add `code` (unique, nullable for non-isolation-point), `level` (int) fields; replace type enum values with FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT
- [ ] 1.2 Create IsolationPointDetail entity (`bp_isolation_point_detail`): id, location_id FK, pid_diagram_ref, medium, pressure, temperature, hazard_level(A/B/C/D), isolation_type(BLIND_PLATE/DOUBLE_BLOCK/VALVE/OTHER), coord_x, coord_y, coord_z, diagram_id FK to BoardProject, timestamps; @OneToOne cascade ALL with Location
- [ ] 1.3 Create LocationChangeRecord entity (`bp_location_change_record`): id, location_id FK, change_type(CREATE/UPDATE/DELETE/MOVE), field_name, old_value, new_value, status(PENDING/APPROVED/REJECTED), applicant_id, approver_id, approval_comment, created_at, approved_at
- [ ] 1.4 Update LocationRepository with new query methods (findByCode, existsByCode, findByType)

## 2. Backend Isolation Point Detail CRUD

- [ ] 2.1 Create IsolationPointDetailRepository (JpaRepository)
- [ ] 2.2 Create IsolationPointDetailService with CRUD methods + validation (hazard level enum, isolation type enum, non-isolation-point reject)
- [ ] 2.3 Create IsolationPointDetailController: GET /api/v1/isolation-points/{locationId}, PUT /api/v1/isolation-points/{locationId}
- [ ] 2.4 Hook IsolationPointDetail auto-creation inside LocationService.create() when type=ISOLATION_POINT (cascade)

## 3. Backend Change Management

- [ ] 3.1 Create LocationChangeRecordRepository with filter methods (findByLocationId, findByStatus, findByApplicantId, findByTimeRange)
- [ ] 3.2 Create LocationChangeRecordService: createChangeRecord, approveChange, rejectChange, queryHistory; enforce ADMIN role on approve/reject
- [ ] 3.3 Create LocationChangeRecordController: GET /api/v1/location-changes (list with filters), POST /api/v1/location-changes/{id}/approve, POST /api/v1/location-changes/{id}/reject
- [ ] 3.4 Hook change record generation into LocationService.create/update/delete/move: CREATE auto-approved, others PENDING

## 4. Backend Location Service Updates

- [ ] 4.1 Add hierarchy level validation in LocationService.create/update: reject invalid parent type (EQUIPMENT must have FACTORY parent, UNIT must have EQUIPMENT parent, ISOLATION_POINT must have UNIT parent)
- [ ] 4.2 Add code uniqueness validation in LocationService.create/update
- [ ] 4.3 Restrict BlindPlate assignment to ISOLATION_POINT only (modify BlindPlateService.assignLocation)
- [ ] 4.4 Update DataInitializer to populate sample data with new type enum (FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT)

## 5. Backend Excel Batch Import

- [ ] 5.1 Add EasyExcel (com.alibaba:easyexcel) or Apache POI dependency to blindplate-server/pom.xml
- [ ] 5.2 Create IsolationPointImportService: getTemplate (generate .xlsx template with headers), importFile (parse + validate + persist + error report)
- [ ] 5.3 Create IsolationPointImportController: GET /api/v1/isolation-points/import/template, POST /api/v1/isolation-points/import (multipart upload)
- [ ] 5.4 Implement per-row validation: check code uniqueness within file + DB, check parent_code exists and type is UNIT, check hazard_level/isolation_type enum values
- [ ] 5.5 Implement partial success: persist valid rows, collect invalid rows with row number + error message, return ImportResult DTO

## 6. Frontend Location Form Update

- [ ] 6.1 Update LocationForm.vue type select options: FACTORY(工厂), EQUIPMENT(装置), UNIT(单元), ISOLATION_POINT(隔离点)
- [ ] 6.2 Add conditional form section for ISOLATION_POINT type: isolation point detail fields (pid_diagram_ref, medium, pressure, temperature, hazard_level, isolation_type, coord_x/y/z, diagram_id)
- [ ] 6.3 Add code field (required for ISOLATION_POINT, optional otherwise)
- [ ] 6.4 Add error display for duplicate code and invalid hierarchy

## 7. Frontend Isolation Point Import Component

- [ ] 7.1 Create IsolationPointImport.vue: download template button, upload area (el-upload), error result table
- [ ] 7.2 Add methods in api/location.ts: getImportTemplate(), importIsolationPoints(file)
- [ ] 7.3 Add route menu entry and import button accessible from location management page

## 8. Frontend Change Approval Page

- [ ] 8.1 Create ChangeApproval.vue: change record list with filters (status, change_type, location, time range)
- [ ] 8.2 Add pending list view, approve button (with comment dialog), reject button (with reason dialog)
- [ ] 8.3 Add methods in api/location.ts: getLocationChanges(), approveChange(id, comment), rejectChange(id, reason)
- [ ] 8.4 Add permission directive v-permission="['ADMIN']" on approve/reject actions

## 9. Frontend Router, Sidebar, i18n

- [ ] 9.1 Add routes: /change-approval (ChangeApproval.vue), link from sidebar
- [ ] 9.2 Add sidebar menu group "基础数据" with location-tree, isolation-point-import, change-approval children
- [ ] 9.3 Add i18n locale entries (zh-CN and en) for new type options, new page titles, button labels, error messages

## 10. Verification

- [ ] 10.1 Backend: mvn clean compile — no errors, no warnings on new entities
- [ ] 10.2 Backend: run application, verify Hibernate DDL generates bp_isolation_point_detail and bp_location_change_record tables
- [ ] 10.3 Frontend: pnpm install + pnpm run build — no type errors, no build failures
- [ ] 10.4 Smoke test: create factory → equipment → unit → isolation point; verify detail auto-create; verify change record pending; admin approve; Excel import
- [ ] 10.5 Run openspec validate --changes equipment-location-data-management — all artifacts valid
