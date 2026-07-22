## Why

当前盲板管理系统仅有基础 CRUD 功能，BlindPlate 实体字段不完整、无批量导入、无状态历史、无检验管理、无报废流程、无盘点功能。PRD 7.1.1 要求实现完整的盲板主数据管理，覆盖入库登记、档案查询、状态变更记录、检验管理、报废管理、盘点六大子功能，以满足石化厂盲板全生命周期数字化管理的合规需求。本期变更将现有基础模块升级为 PRD 要求的完整主数据管理模块。

## What Changes

**盲板实体字段扩展**
- From: 基础字段（code, name, spec, material, diameter, pressure, manufacturer, status, remark）
- To: 对齐 PRD 6.2 完整字段（新增 modelType, thickness, factoryCode, purchaseDate, currentLocationId, installCount, totalUsageDays, lifecycleStatus, nextInspectionDate, rfidTag, qrCode）
- Reason: PRD 6.2 数据模型要求
- Impact: non-breaking（新增字段可为空，旧数据兼容）

**状态枚举对齐**
- From: available/installed/removed/maintenance
- To: in_stock/in_use/under_inspection/scrapped/lost
- Reason: PRD 6.2 定义的状态值
- Impact: breaking（需迁移已有数据，前端表单更新）

**查询接口增强**
- From: 无分页全量返回 GET /api/v1/blindplates
- To: 分页+多维过滤（keyword/modelType/material/status/lifecycleStatus）
- Reason: PRD 7.1.1 要求多维检索
- Impact: non-breaking（前端适配分页参数）

**新增入库登记功能**
- To: 手工录入自动生成 QR/RFID + Excel 批量导入
- Reason: PRD 7.1.1 入库登记要求
- Impact: 新增 API + 前端导入弹窗

**新增状态变更记录**
- To: 每次状态变更自动写入历史表
- Reason: PRD 7.1.1 状态变更记录要求
- Impact: 新增 BlindPlateStatusHistory 实体 + API

**新增检验管理**
- To: 检验记录 CRUD + 到期提醒 + 超期告警
- Reason: PRD 7.1.1 检验管理要求
- Impact: 新增 BlindPlateInspection 实体 + 定时任务 + API + 前端检验管理页

**新增报废管理**
- To: 报废申请 + 审批流程，批准后移出库存
- Reason: PRD 7.1.1 报废管理要求
- Impact: 新增 BlindPlateScrapRecord 实体 + API + 前端报废审批弹窗

**新增盘点管理**
- To: Web 端盘点批次管理 + 差异报告
- Reason: PRD 7.1.1 盘点要求
- Impact: 新增 BlindPlateStocktake 实体 + API + 前端盘点页面

## Capabilities

### New Capabilities
- `blindplate-inspection`: 盲板检验记录管理与到期告警
- `blindplate-scrap`: 盲板报废申请与审批流程管理
- `blindplate-stocktake`: 盲板盘点批次管理与差异报告

### Modified Capabilities
- `blindplate-catalog`: 扩展盲板实体字段、状态枚举、分页查询、批量导入、状态变更历史

## Impact

**后端影响：**
- 修改 `BlindPlate` 实体（新增 11 个字段）
- 修改 `BlindPlateRepository`（新增自定义查询方法）
- 修改 `BlindPlateController`（新增分页、导入、导出、状态历史等端点）
- 修改 `BlindPlateService`（新增批量导入、QR/RFID 生成、状态历史记录逻辑）
- 新增 3 个实体类 + Repository + Service + Controller（Inspection, Scrap, Stocktake）
- 新增定时任务（检验到期扫描）
- 新增 Excel 导入/导出工具类（Apache POI）
- 新增 DataInitializer 迁移逻辑（状态枚举值迁移）

**前端影响：**
- 修改 `BlindPlateList.vue`（分页 + 多维筛选 + 批量导入 + 导出 + 状态标签）
- 修改 `BlindPlateForm.vue`（新增字段表单 + 状态枚举更新）
- 修改 `types/index.ts`（BlindPlate 接口扩展）
- 修改 `api/blindplate.ts`（新增 API 调用）
- 新增盲板详情抽屉组件（Tab 页：基本信息/状态历史/检验记录/报废信息）
- 新增检验管理页面/弹窗
- 新增报废审批弹窗
- 新增盘点管理页面
- 修改 i18n locales（新增条目）

**数据库影响：**
- `bp_blind_plate` 表新增 11 列
- 新增 4 张表：`bp_status_history`、`bp_inspection`、`bp_scrap_record`、`bp_stocktake`
- status 字段值迁移
