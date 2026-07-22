## Why

装置/位置数据当前仅有通用树CRUD，不满足PRD 7.1.2要求：1）type枚举为area/building/floor/room/equipment，与PRD四级层级（工厂→装置→单元→隔离点）不匹配；2）缺隔离点主数据维护（PID台账、介质/压力/温度/危害等级、Excel批量导入）；3）缺隔离点位置坐标标注能力；4）缺变更审批与历史追溯。需实现这些能力以支撑后续隔离方案管理（PRD 7.2）和盲板全流程作业（PRD 7.3-7.5）。

## What Changes

**Location type 枚举重构**
- From: 五种 type（area/building/floor/room/equipment）
- To: 四种 type（FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT）对应 PRD 四级层级
- Reason: 与 PRD 7.1.2 装置层级结构维护对齐
- Impact: breaking，前后端 type 常量、i18n key、DataInitializer 需同步修改

**Location 实体新增字段**
- From: 仅有 id/parentId/name/description/type/children
- To: 新增 code（唯一编码）、level（层级深度）
- Reason: 隔离点需要唯一编码用于扫码（PRD 7.5），level 用于层级约束校验
- Impact: non-breaking（新增字段）

**隔离点主数据维护（新增）**
- IsolationPointDetail 实体 1:1 关联 ISOLATION_POINT 类型 Location
- 字段：pid_diagram_ref, medium, pressure, temperature, hazard_level, isolation_type, coord_x/y/z, diagram_id
- Excel 批量导入模板 + 后端解析

**隔离点位置标注（新增）**
- IsolationPointDetail 中存储 coord_x/coord_y/coord_z 坐标
- diagram_id 关联现有 BlindBoardProject 图纸项目

**隔离点数据变更管理（新增）**
- LocationChangeRecord 实体，记录变更历史（字段级 JSON 快照）
- 审批流：提交→ADMIN审批/驳回
- 变更类型：CREATE/UPDATE/DELETE/MOVE

## Capabilities

### New Capabilities
- `isolation-point-data`: 隔离点主数据维护，含 IsolationPointDetail 实体管理、Excel 批量导入、PID 图纸引用、介质/压力/温度/危害等级等专有字段 CRUD
- `location-change-management`: Location 变更审批与历史追溯，含 LocationChangeRecord 实体、提交→审批/驳回流程、字段级变更快照、变更历史查询

### Modified Capabilities
- `location-tree`: Location type 枚举重构为 FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT 四级层级，新增 code 和 level 字段，层级约束校验

## Impact

**后端：**
- 新增实体：IsolationPointDetail, LocationChangeRecord
- 新增 repository/service/controller：IsolationPointDetailService, LocationChangeRecordService
- 修改：Location 实体（type 枚举、code、level 字段）、LocationService（层级校验）、LocationController（新 endpoints）
- DataInitializer 更新：初始化数据使用新 type 枚举
- 新增依赖：EasyExcel 或 Apache POI（Excel 解析）

**前端：**
- 修改：LocationForm.vue（type 选项、隔离点专有字段表单）
- 修改：api/location.ts（新增导入、审批 API 调用）
- 新增：IsolationPointImport.vue（Excel 导入组件）
- 新增：ChangeApproval.vue（变更审批列表页）
- 修改：i18n locale（新 type 选项、新页面文案）
- 修改：router（新增变更审批路由）

**数据库：**
- 新增表：bp_isolation_point_detail, bp_location_change_record
- 修改表：bp_location 新增 code、level 列
- 迁移：存量 type 数据重置（开发阶段）
