## Design Summary

实现 PRD 7.1.1 盲板主数据管理功能，在现有基础 CRUD 之上扩展为完整的主数据管理模块。

### 现状分析

当前 `BlindPlate` 实体仅有基础字段（code, name, spec, material, diameter, pressure, manufacturer, status, remark），控制器仅提供简单 CRUD，无分页、无批量导入、无状态历史、无检验管理、无报废流程、无盘点功能。

### 目标功能（PRD 7.1.1）

1. **盲板入库登记**：批量导入（Excel）+ 手工录入，自动生成唯一二维码/RFID 标签
2. **盲板档案查询**：按 ID、型号、规格、材质、状态等多维检索，分页返回
3. **盲板状态变更记录**：任何状态变更（在库→在用→报废等）自动记录并保留历史
4. **盲板检验管理**：定期检验到期提醒，超期未检自动告警，检验记录管理
5. **盲板报废管理**：报废申请与审批流程，报废后从可用库存中移除
6. **盲板盘点**：移动端扫码盘点，自动生成盘点差异报告

### 实体扩展设计

**BlindPlate 实体**（扩展现有）：
- 新增字段：`modelType`（型号枚举：8字盲板/插板/垫环/盲法兰/其他）、`thickness`（厚度）、`factoryCode`（出厂编号）、`purchaseDate`（采购日期）、`currentLocationId`（当前安装位置 FK）、`installCount`（累计安装次数）、`totalUsageDays`（累计使用时长）、`lifecycleStatus`（生命周期状态：正常/到期检验/超期/报废）、`nextInspectionDate`（下次检验日期）、`rfidTag`（RFID标签编码）、`qrCode`（二维码编码）
- 状态值对齐 PRD：`in_stock`（在库）/ `in_use`（在用）/ `under_inspection`（送检）/ `scrapped`（报废）/ `lost`（丢失）

**新增实体**：
- `BlindPlateStatusHistory`：状态变更记录（盲板ID、操作前状态、操作后状态、变更时间、操作人、变更原因）
- `BlindPlateInspection`：检验记录（盲板ID、检验日期、检验结果、下次检验日期、检验人、备注）
- `BlindPlateScrapRecord`：报废记录（盲板ID、申请时间、申请人、审批状态、审批人、报废原因）
- `BlindPlateStocktake`：盘点记录（盘点批次号、盘点时间、盘点人、盲板ID、扫描状态、差异类型）

### API 设计

- `GET /api/v1/blindplates` — 分页查询，支持 keyword/modelType/material/status/lifecycleStatus 过滤
- `POST /api/v1/blindplates` — 手工录入，自动生成 qrCode 和 rfidTag
- `POST /api/v1/blindplates/import` — Excel 批量导入
- `GET /api/v1/blindplates/{id}/status-history` — 状态变更历史
- `GET /api/v1/blindplates/{id}/inspections` — 检验记录
- `POST /api/v1/blindplates/{id}/inspections` — 新增检验记录，更新 nextInspectionDate
- `POST /api/v1/blindplates/{id}/scrap` — 发起报废申请
- `PUT /api/v1/blindplates/{id}/scrap/approve` — 审批报废申请
- `POST /api/v1/blindplates/stocktake` — 创建盘点批次
- `GET /api/v1/blindplates/stocktake/{batchId}` — 获取盘点差异报告
- `GET /api/v1/blindplates/export` — 导出 Excel

### 前端界面

- **盲板列表页**（增强现有 BlindPlateList.vue）：多维筛选 + 分页 + 批量导入按钮 + 导出按钮 + 状态标签颜色区分 + 生命周期状态标签
- **盲板详情抽屉**：新增 Tab 页（基本信息 / 状态历史 / 检验记录 / 报废信息）
- **检验管理弹窗**：新增检验记录表单 + 检验到期提醒列表
- **报废审批弹窗**：报废申请表单 + 审批操作
- **盘点页面**：盘点批次列表 + 盘点差异报告

## Alternatives Considered

### 方案 A：渐进式扩展（在现有 BlindPlate 实体上直接加字段）
- **做法**：扩展现有 BlindPlate 实体添加 PRD 6.2 的缺失字段，新增 4 个关联实体，在现有 Controller 上扩展 API
- **优点**：改动集中、迁移成本最低、复用现有代码结构、不破坏已有功能
- **缺点**：现有 BlindPlate 的 `name`、`spec` 字段语义不精确（PRD 中无 `name`，有 `modelType`；`spec` 应拆分为 `diameter`+`thickness`+`pressure`）
- **为何采用**：成本最低且符合现有项目模式，字段语义不对齐可通过迁移解决

### 方案 B：重建实体（新建 BlindPlateMaster 替代 BlindPlate）
- **做法**：新建 `BlindPlateMaster` 实体严格对齐 PRD 6.2 数据模型，废弃旧 `BlindPlate`
- **优点**：数据模型完全对齐 PRD，无历史包袱
- **缺点**：需要迁移已有数据、影响已有前端 API 调用、重写大量代码、破坏现有 operation 模块对 BlindPlate 的引用
- **为何未采用**：迁移成本过高，现有 `BlindPlate` 实体已可用，只需字段扩展和语义对齐

### 方案 C：双实体并存（BlindPlate 基础信息 + BlindPlateProfile 扩展信息）
- **做法**：保留 BlindPlate 不变，新建 BlindPlateProfile 1:1 关联存储 PRD 6.2 扩展字段
- **优点**：完全不修改现有代码，风险隔离
- **缺点**：增加 JOIN 查询复杂度、数据一致性需额外维护、代码理解成本高
- **为何未采用**：过度设计，1:1 关联实体没有实际好处

## Agreed Approach

采用**方案 A：渐进式扩展**。在现有 `BlindPlate` 实体上直接扩展 PRD 6.2 缺失字段，新增 4 个关联实体（状态历史、检验记录、报废记录、盘点记录），在现有 Controller 上扩展 API。同时迁移旧字段语义：

- `name` → 保留但改为可选（PRD 中无盲板名称字段，标识用 `code` + `modelType`）
- `spec` → 保留但改为可选（实际规格由 `diameter` + `thickness` + `pressure` 组合体现）

理由：迁移成本最低，复用现有代码结构，字段语义不对齐可通过数据迁移和前端适配解决。

## Key Decisions

1. **状态枚举对齐 PRD**：`in_stock`/`in_use`/`under_inspection`/`scrapped`/`lost`，需迁移旧值 `available`→`in_stock`、`installed`→`in_use`、`removed`→`in_stock`、`maintenance`→`under_inspection`
2. **QR/RFID 自动生成**：入库时系统自动生成 `qrCode`（格式：BP-{yyyyMMdd}-{6位序号}）和 `rfidTag`（UUID），用户无需手动输入
3. **Excel 批量导入**：使用 Apache POI 解析 Excel，支持模板下载 + 数据校验 + 错误报告
4. **状态变更记录**：在 Service 层通过显式调用记录每次 status 变更到 `BlindPlateStatusHistory` 表
5. **检验到期告警**：通过定时任务（@Scheduled）每日扫描 `nextInspectionDate <= 当前日期+7天` 的盲板，生成告警
6. **报废流程**：简单状态机（申请→待审批→已批准→已驳回），无复杂工作流引擎
7. **盘点功能**：Web 端创建盘点批次 + 录入扫描结果 + 自动比对生成差异报告；移动端扫码在后续阶段实现

## Open Questions

- **盘点移动端扫码**：本期先实现 Web 端盘点批次管理和差异报告，移动端扫码盘点留待后续迭代
- **导出格式**：本期支持 Excel 导出，PDF/CSV 留待后续
