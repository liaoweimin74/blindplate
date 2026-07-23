# 盲板主数据管理 Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development
> to implement this plan task-by-task.

**Goal:** 实现 PRD 7.1.1 盲板主数据管理，覆盖入库登记、档案查询、状态变更记录、检验管理、报废管理、盘点六大子功能。

**Architecture:** 在现有 Spring Boot 模块化单体架构上扩展 BlindPlate 实体，新增 4 个关联实体（状态历史、检验、报废、盘点），后端按模块分包（blindplate/inspection/scrap/stocktake），前端 Vue3 + Element Plus 增强列表页并新增详情抽屉、检验管理、报废审批、盘点管理界面。

**Tech Stack:** Spring Boot 3.x + Spring Data JPA + MySQL 8.0 + Apache POI (Excel) | Vue3 + TypeScript + Element Plus + Vite | JUnit 5 + Vitest

**Reference Artifacts:**
- specs/blindplate-catalog/spec.md — 盲板扩展、批量导入、状态历史、导出
- specs/blindplate-inspection/spec.md — 检验记录、到期提醒
- specs/blindplate-scrap/spec.md — 报废申请与审批
- specs/blindplate-stocktake/spec.md — 盘点批次与差异报告
- design.md — 技术设计决策
- brainstorm.md — 方案选择

---

## Task 1: 后端实体扩展与数据库迁移

- [ ] **Step 1:** 扩展 `BlindPlate.java` 实体，新增字段：`modelType`（枚举 String）、`thickness`（Double）、`factoryCode`（String）、`purchaseDate`（LocalDate）、`currentLocationId`（Long）、`installCount`（Integer，默认0）、`totalUsageDays`（Double，默认0）、`lifecycleStatus`（String，默认"normal"）、`nextInspectionDate`（LocalDate）、`rfidTag`（String）、`qrCode`（String）
- [ ] **Step 2:** 创建 `BlindPlateStatusHistory.java` 实体（id, blindPlateId, previousStatus, newStatus, changedAt, operator, reason），注解 `@Table(name = "bp_status_history")`
- [ ] **Step 3:** 创建 `BlindPlateInspection.java` 实体（id, blindPlateId, inspectionDate, result, nextInspectionDate, inspector, remark, createdAt），注解 `@Table(name = "bp_inspection")`
- [ ] **Step 4:** 创建 `BlindPlateScrapRecord.java` 实体（id, blindPlateId, applyTime, applicant, status, approver, approveTime, approveComment, reason），注解 `@Table(name = "bp_scrap_record")`
- [ ] **Step 5:** 创建 `BlindPlateStocktake.java`（id, batchNo, batchName, operator, status, createdAt, closedAt）和 `BlindPlateStocktakeItem.java`（id, batchId, blindPlateCode, scannedAt, matchStatus），注解 `@Table(name = "bp_stocktake", "bp_stocktake_item")`
- [ ] **Step 6:** 创建 Repository 接口：`BlindPlateStatusHistoryRepository`、`BlindPlateInspectionRepository`、`BlindPlateScrapRecordRepository`、`BlindPlateStocktakeRepository`、`BlindPlateStocktakeItemRepository`
- [ ] **Step 7:** 修改 `BlindPlateRepository`，新增方法：`findByModelTypeAndMaterialAndStatus(String modelType, String material, String status, Pageable pageable)`、`findByLifecycleStatus(String lifecycleStatus, Pageable pageable)` 等
- [ ] **Step 8:** 在 `DataInitializer.java` 中新增状态迁移逻辑：`available→in_stock`、`installed→in_use`、`removed→in_stock`、`maintenance→under_inspection`
- [ ] **Step 9:** 运行 `mvn compile` 验证编译通过
- [ ] **Commit:** `feat: extend BlindPlate entity and create related entities`

---

## Task 2: 后端 API - 盲板目录管理扩展

- [ ] **Step 1:** 修改 `BlindPlateController.java`，list 端点改为 `@GetMapping` 接收 `Pageable` + `@RequestParam` 过滤参数（keyword, modelType, material, status, lifecycleStatus）
- [ ] **Step 2:** 修改 `BlindPlateService.java`，新增 `findAllWithFilters(keyword, modelType, material, status, lifecycleStatus, pageable)` 方法，使用 `Specification` 动态查询
- [ ] **Step 3:** 新增 `POST /api/v1/blindplates/import` 端点，接收 `MultipartFile`，使用 Apache POI 解析 Excel，调用 `BlindPlateService.batchImport(List<BlindPlateImportDTO>)`
- [ ] **Step 4:** 在 `pom.xml` 中添加 Apache POI 依赖（`poi`、`poi-ooxml`）
- [ ] **Step 5:** 新增 `GET /api/v1/blindplates/template` 端点，使用 POI 生成模板 Excel 并返回
- [ ] **Step 6:** 新增 `GET /api/v1/blindplates/export` 端点，根据当前过滤条件查询后生成 Excel 返回
- [ ] **Step 7:** 修改 `BlindPlateService.create()`，调用 `generateQrCode()`（格式 `BP-yyyyMMdd-{6位序号}`）和 `generateRfidTag()`（UUID）
- [ ] **Step 8:** 修改 `BlindPlateService.update()`，检测 status 变更，写入 `BlindPlateStatusHistory` 记录
- [ ] **Step 9:** 新增 `GET /api/v1/blindplates/{id}/status-history` 端点
- [ ] **Step 10:** 创建 `ExcelImportService.java` 工具类，封装 POI 解析+导入逻辑，支持行级错误收集
- [ ] **Step 11:** 编写单元测试：`BlindPlateServiceTest`（QR/RFID 生成、状态历史写入、分页查询）
- [ ] **Commit:** `feat: extend blindplate catalog API with pagination, import, export`

---

## Task 3: 后端 API - 检验管理

- [ ] **Step 1:** 创建 `InspectionController.java`，提供 `POST/GET/PUT/DELETE /api/v1/inspections` CRUD 端点
- [ ] **Step 2:** 新增 `GET /api/v1/blindplates/{id}/inspections` 端点
- [ ] **Step 3:** 创建 `InspectionService.java`，`create()` 方法新增检验记录后更新盲板 `nextInspectionDate` 和 `lifecycleStatus`
- [ ] **Step 4:** 新增 `GET /api/v1/blindplates/inspection-alerts` 端点，返回 lifecycleStatus 为 inspection_due 或 overdue 的盲板列表
- [ ] **Step 5:** 创建 `InspectionScheduleService.java`，`@Scheduled(cron = "0 0 8 * * ?")` 每日08:00扫描，更新 lifecycleStatus
- [ ] **Step 6:** 在 `BlindPlateApplication.java` 添加 `@EnableScheduling`
- [ ] **Step 7:** 编写单元测试：`InspectionServiceTest`（检验记录创建、lifecycle 状态更新）
- [ ] **Commit:** `feat: add blindplate inspection management API`

---

## Task 4: 后端 API - 报废管理

- [ ] **Step 1:** 创建 `ScrapController.java`，提供 `POST /api/v1/blindplates/{id}/scrap`（发起报废申请）
- [ ] **Step 2:** 新增 `PUT /api/v1/blindplates/scrap/{scrapId}/approve`（审批报废申请）
- [ ] **Step 3:** 新增 `GET /api/v1/blindplates/scrap`（分页查询，支持 status/applicant/dateRange 过滤）
- [ ] **Step 4:** 新增 `GET /api/v1/blindplates/{id}/scrap`（查询指定盲板的报废历史）
- [ ] **Step 5:** 创建 `ScrapService.java`，`submitScrap()` 校验（已 scrapped 拒绝、已 pending 拒绝），`approveScrap()` 审批通过后更新盲板 status 和 lifecycleStatus 为 scrapped
- [ ] **Step 6:** 报废审批通过时写入 `BlindPlateStatusHistory` 记录
- [ ] **Step 7:** 编写单元测试：`ScrapServiceTest`（申请校验、审批状态变更）
- [ ] **Commit:** `feat: add blindplate scrap management API`

---

## Task 5: 后端 API - 盘点管理

- [ ] **Step 1:** 创建 `StocktakeController.java`，提供 `POST /api/v1/blindplates/stocktake`（创建盘点批次）
- [ ] **Step 2:** 新增 `POST /api/v1/blindplates/stocktake/{batchId}/scan`（提交扫描编码列表）
- [ ] **Step 3:** 新增 `PUT /api/v1/blindplates/stocktake/{batchId}/close`（关闭批次，生成差异报告）
- [ ] **Step 4:** 新增 `GET /api/v1/blindplates/stocktake`（分页查询批次列表）
- [ ] **Step 5:** 新增 `GET /api/v1/blindplates/stocktake/{batchId}`（详情及差异报告）
- [ ] **Step 6:** 创建 `StocktakeService.java`，`createBatch()` 自动生成批次号 `ST-yyyyMMdd-{4位序号}`
- [ ] **Step 7:** `closeBatch()` 方法实现差异比对逻辑：matched（在库且已扫描）、missing（在库但未扫描）、unexpected（扫描但不在库）、location_mismatch（在用但被扫描）
- [ ] **Step 8:** 编写单元测试：`StocktakeServiceTest`（差异比对逻辑）
- [ ] **Commit:** `feat: add blindplate stocktake management API`

---

## Task 6: 前端类型与 API 层

- [ ] **Step 1:** 修改 `types/index.ts`，扩展 `BlindPlate` 接口新增 11 个字段，新增 `BlindPlateStatusHistory`、`BlindPlateInspection`、`BlindPlateScrapRecord`、`BlindPlateStocktake`、`BlindPlateStocktakeItem`、`DifferenceReport` 接口
- [ ] **Step 2:** 扩展 `api/blindplate.ts`，新增所有 API 调用函数（importBlindPlates, exportBlindPlates, downloadTemplate, getStatusHistory, getInspections, createInspection, updateInspection, deleteInspection, getInspectionAlerts, submitScrap, approveScrap, getScrapRecords, getScrapHistory, createStocktake, scanStocktake, closeStocktake, getStocktakeBatches, getStocktakeDetail）
- [ ] **Step 3:** 运行 `npx tsc --noEmit` 验证类型检查通过
- [ ] **Commit:** `feat: extend frontend types and API layer for blindplate master data`

---

## Task 7: 前端 - 盲板列表页增强

- [ ] **Step 1:** 修改 `BlindPlateList.vue`，将 `getBlindPlates()` 改为分页调用 `getBlindPlates({ page, size, keyword, modelType, material, status, lifecycleStatus })`
- [ ] **Step 2:** 添加筛选栏组件（keyword input + modelType select + material select + status select + lifecycleStatus select）
- [ ] **Step 3:** 添加 el-pagination 组件，绑定 page/size 状态
- [ ] **Step 4:** 添加「批量导入」按钮 + el-upload 弹窗，调用 importBlindPlates API
- [ ] **Step 5:** 添加「导出Excel」按钮，调用 exportBlindPlates 下载文件
- [ ] **Step 6:** 添加「下载模板」按钮，调用 downloadTemplate 下载文件
- [ ] **Step 7:** 表格新增列：modelType, thickness, lifecycleStatus（Tag 颜色）, nextInspectionDate, qrCode
- [ ] **Step 8:** status 列改用 el-tag 颜色区分（in_stock=success, in_use=primary, under_inspection=warning, scrapped=danger, lost=info）
- [ ] **Step 9:** 修复 `handleFormSubmit` 实现，调用 createBlindPlate/updateBlindPlate
- [ ] **Commit:** `feat: enhance blindplate list page with filters, pagination, import/export`

---

## Task 8: 前端 - 盲板表单增强

- [ ] **Step 1:** 修改 `BlindPlateForm.vue`，新增 modelType（el-select 枚举）、thickness（el-input-number）、factoryCode（el-input）、purchaseDate（el-date-picker）、nextInspectionDate（el-date-picker）表单项
- [ ] **Step 2:** 更新 status el-select 选项为 in_stock/in_use/under_inspection/scrapped/lost
- [ ] **Step 3:** 添加 qrCode 和 rfidTag 字段：创建时隐藏，编辑时 el-input readonly 显示
- [ ] **Step 4:** 更新 form validation rules
- [ ] **Commit:** `feat: enhance blindplate form with new fields`

---

## Task 9: 前端 - 盲板详情抽屉

- [ ] **Step 1:** 创建 `BlindPlateDetailDrawer.vue` 组件，props: `visible`, `blindPlateId`
- [ ] **Step 2:** 使用 el-drawer + el-tabs（4 个 tab-pane）
- [ ] **Step 3:** Tab1 基本信息：el-descriptions 展示所有字段
- [ ] **Step 4:** Tab2 状态历史：el-table 展示 getStatusHistory 返回数据
- [ ] **Step 5:** Tab3 检验记录：el-table + 新增检验记录按钮（打开 InspectionForm）
- [ ] **Step 6:** Tab4 报废信息：el-descriptions 展示报废记录（如有）
- [ ] **Step 7:** 在 BlindPlateList.vue 中引入并使用详情抽屉
- [ ] **Commit:** `feat: add blindplate detail drawer with tabs`

---

## Task 10: 前端 - 检验管理

- [ ] **Step 1:** 创建 `InspectionForm.vue` 弹窗组件（检验日期、检验结果 select、下次检验日期、检验人、备注）
- [ ] **Step 2:** 在 BlindPlateDetailDrawer 检验记录 Tab 中嵌入 InspectionForm
- [ ] **Step 3:** 创建 `InspectionAlerts.vue` 组件，调用 getInspectionAlerts 展示到期/超期告警列表
- [ ] **Step 4:** 告警列表按 nextInspectionDate 升序，超期记录行高亮红色（el-table row-class-name）
- [ ] **Step 5:** 在盲板列表页或独立区域展示检验告警
- [ ] **Commit:** `feat: add inspection management UI`

---

## Task 11: 前端 - 报废管理

- [ ] **Step 1:** 创建 `ScrapForm.vue` 弹窗组件（报废原因 textarea + 申请人 input）
- [ ] **Step 2:** 创建 `ScrapApprovalDialog.vue` 弹窗组件（展示报废详情 + 通过/驳回按钮 + 审批意见）
- [ ] **Step 3:** 在 BlindPlateList 添加「发起报废」按钮（el-table 操作列，仅对非 scrapped 状态显示）
- [ ] **Step 4:** 实现报废列表弹窗或页面，支持按状态筛选，审批人可操作
- [ ] **Commit:** `feat: add scrap management UI`

---

## Task 12: 前端 - 盘点管理

- [ ] **Step 1:** 创建 `StocktakeList.vue` 页面，展示盘点批次列表（批次号/名称/操作人/状态/创建时间），el-pagination 分页
- [ ] **Step 2:** 创建 `StocktakeCreateDialog.vue` 弹窗（批次名称 + 操作人）
- [ ] **Step 3:** 创建 `StocktakeScanInput.vue` 组件（el-input textarea 批量输入编码 + 提交按钮）
- [ ] **Step 4:** 创建 `StocktakeDetail.vue` 页面，展示差异报告（matched/missing/unexpected/location_mismatch 四个 section）
- [ ] **Step 5:** 在 `router/index.ts` 添加盘点管理路由
- [ ] **Step 6:** 在 `AppSidebar.vue` 添加盘点管理菜单入口
- [ ] **Commit:** `feat: add stocktake management UI`

---

## Task 13: 前端 - i18n

- [ ] **Step 1:** 在 `locales/zh-CN.json` 新增所有新功能国际化条目（modelType 选项、lifecycleStatus 标签、status 新枚举、检验结果、报废状态、盘点状态、按钮文字等）
- [ ] **Step 2:** 在 `locales/en.json` 新增对应英文翻译
- [ ] **Commit:** `feat: add i18n entries for blindplate master data`

---

## Task 14: 后端测试

- [ ] **Step 1:** 编写 `BlindPlateServiceTest`：QR/RFID 生成唯一性、状态变更写入历史、分页查询过滤
- [ ] **Step 2:** 编写 `InspectionServiceTest`：检验记录更新 nextInspectionDate、lifecycleStatus 计算
- [ ] **Step 3:** 编写 `ScrapServiceTest`：报废申请校验（已 scrapped 拒绝、已 pending 拒绝）、审批通过状态变更
- [ ] **Step 4:** 编写 `StocktakeServiceTest`：差异比对逻辑（matched/missing/unexpected/location_mismatch）
- [ ] **Step 5:** 编写 `ExcelImportServiceTest`：格式验证、错误行报告、行数限制
- [ ] **Step 6:** 编写 Controller 集成测试：分页查询、多维过滤、导入端点、导出端点
- [ ] **Commit:** `test: add backend tests for blindplate master data`

---

## Task 15: 前端测试

- [ ] **Step 1:** 编写 BlindPlateList 组件测试：分页渲染、筛选交互、导入弹窗、导出按钮
- [ ] **Step 2:** 编写 BlindPlateDetailDrawer 组件测试：Tab 切换、状态历史列表、检验记录列表
- [ ] **Step 3:** 编写 StocktakeDetail 组件测试：差异报告分区展示
- [ ] **Commit:** `test: add frontend tests for blindplate master data`