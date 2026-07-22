## Context

盲板管理系统基于 Spring Boot 3.x + Vue3 + TypeScript 架构。当前存在基础 `BlindPlate` 实体，支持简单 CRUD 操作。PRD 7.1.1 要求实现完整的盲板主数据管理功能，包括入库登记、档案查询、状态变更记录、检验管理、报废管理、盘点六大子功能。

现有 `BlindPlate` 实体字段（code, name, spec, material, diameter, pressure, manufacturer, status, remark, createdAt, updatedAt）与 PRD 6.2 数据模型存在差距：缺少型号(modelType)、厚度(thickness)、出厂编号(factoryCode)、采购日期(purchaseDate)、安装位置(currentLocationId)、安装次数(installCount)、使用时长(totalUsageDays)、生命周期状态(lifecycleStatus)、下次检验日期(nextInspectionDate)、RFID编码(rfidTag)、二维码编码(qrCode)。状态枚举值也与 PRD 不一致。

现有 Controller 仅提供无分页的 list 接口，无批量导入、无多维查询、无状态历史、无检验管理、无报废流程、无盘点功能。

**约束条件：**
- 后端 Spring Boot 3.x + Spring Data JPA + MySQL 8.0
- 前端 Vue3 + TypeScript + Element Plus + Vite
- 内网部署，无外部系统集成
- 现有 operation 模块引用 BlindPlate，不能破坏已有功能
- 模块化单体架构，模块间通过 Service 接口通信

## Goals / Non-Goals

**Goals:**
1. 扩展 `BlindPlate` 实体，对齐 PRD 6.2 盲板实物主数据模型
2. 实现入库登记（手工录入 + Excel 批量导入），自动生成 QR/RFID 标签
3. 实现多维档案查询（分页 + 型号/材质/状态/生命周期状态过滤）
4. 实现状态变更自动记录，保留完整历史
5. 实现检验记录管理 + 到期提醒 + 超期告警
6. 实现报废申请 + 审批流程，报废后移出可用库存
7. 实现 Web 端盘点批次管理 + 差异报告
8. 前端增强盲板列表页，新增详情抽屉、检验管理、报废审批、盘点页面

**Non-Goals:**
1. 不实现移动端扫码盘点（Web 端先行，移动端后续迭代）
2. 不实现 PDF/CSV 导出（本期仅 Excel）
3. 不实现复杂工作流引擎（报废审批用简单状态机）
4. 不修改 operation 模块的 BlindPlate 引用逻辑
5. 不实现盲板安装/拆除操作状态变更（由作业管理模块负责）
6. 不实现检验到期推送通知（仅系统内告警列表）

## Decisions

### D1: 实体渐进式扩展（方案 A）
**选择**: 在现有 `BlindPlate` 实体上直接添加 PRD 6.2 缺失字段，新增 4 个关联实体

**理由**: 迁移成本最低，复用现有代码结构。`name` 改为可选字段（PRD 无此字段，标识用 code + modelType），`spec` 改为可选（实际规格由 diameter + thickness + pressure 组合体现）

**替代方案**: 新建 `BlindPlateMaster` 替代旧实体（迁移成本高）、1:1 Profile 关联（过度设计）

### D2: 状态枚举对齐 PRD
**选择**: 5 个状态值：`in_stock`/`in_use`/`under_inspection`/`scrapped`/`lost`

**理由**: 与 PRD 6.2 完全一致。通过 Flyway/手工迁移旧值：`available`→`in_stock`、`installed`→`in_use`、`removed`→`in_stock`、`maintenance`→`under_inspection`

**替代方案**: 保留旧状态值（与 PRD 不一致，影响数据语义）

### D3: QR/RFID 自动生成策略
**选择**: 入库时系统自动生成
- `qrCode` 格式：`BP-{yyyyMMdd}-{6位序号}`（日期+序号，人类可读）
- `rfidTag` 格式：UUID

**理由**: QR 码需要人类可读性（便于现场核对），RFID 用 UUID 保证全局唯一性

### D4: Excel 批量导入方案
**选择**: Apache POI 解析 Excel

**理由**: Spring Boot 生态成熟，模板下载 + 数据校验 + 行级错误报告。导入前校验唯一性（code 不能重复），错误行汇总返回

**替代方案**: EasyExcel（阿里出品，更轻量但需额外依赖）

### D5: 状态变更记录策略
**选择**: 在 Service 层显式调用记录

**理由**: 每次修改 status 时，先查旧状态，写入 `BlindPlateStatusHistory` 表，再更新主表。不用 AOP（简单直接，避免代理问题）

### D6: 检验到期定时扫描
**选择**: `@Scheduled` 每日 08:00 扫描 `nextInspectionDate <= 当前日期+7天` 的盲板

**理由**: 简单可靠，内网环境无需分布式定时任务框架。告警结果写入 `BlindPlateInspectionAlert` 表或直接更新 `lifecycleStatus`

### D7: 报废审批状态机
**选择**: 4 状态：`pending`（待审批）→ `approved`（已批准）/ `rejected`（已驳回）

**理由**: 满足 PRD "申请与审批流程"要求，无复杂工作流引擎需求。报废批准后主表 status 改为 `scrapped`，从可用库存移除

## Risks / Trade-offs

### R1: 状态枚举迁移可能影响已有数据
- **风险**: 现有数据库中 status 字段已有旧值，前端硬编码了旧值
- **缓解**: 提供数据迁移脚本，前端表单状态选项对齐新枚举

### R2: Excel 导入大文件可能 OOM
- **风险**: 大批量盲板数据导入时 Apache POI 内存占用高
- **缓解**: 限制单次导入行数（5000行/次），使用 SAX 模式解析

### R3: QR 码序号并发生成可能冲突
- **风险**: 多用户同时入库时序号可能重复
- **缓解**: 使用数据库序列或 UUID 后缀保证唯一性

### R4: 现有 operation 模块依赖 BlindPlate.status
- **风险**: 状态枚举变更可能影响 operation 模块的业务逻辑
- **缓解**: 检查 operation 模块中的 status 引用，同步更新。本期不修改 operation 逻辑

### R5: 盘点功能 Web 端录入效率
- **风险**: Web 端逐条录入扫描结果效率低
- **缓解**: 支持批量录入扫码结果（CSV上传或批量表单录入）
