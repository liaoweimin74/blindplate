## Context

当前装置/位置数据管理仅有通用的 `Location` 树结构（id/parentId/name/type/description），type 枚举为 area/building/floor/room/equipment 五种，由 init-project-skeleton 阶段定义。尚未实现 PRD 7.1.2 所要求的装置四级层级（工厂→装置→单元→隔离点）、隔离点主数据（PID 台账、介质/压力/温度/危害等级）、隔离点位置标注（3D 坐标绑定）、隔离点数据变更管理（审批+历史）。

技术栈：Spring Boot 3 + JPA + Lombok（后端），Vue 3 + Element Plus + Pinia + TypeScript（前端）。位置树已存在于后端 `com.mangban.location` 包，前端 `api/location.ts` + `components/LocationForm.vue` 提供基础 CRUD。

## Goals / Non-Goals

**Goals:**
- 重构 Location type 枚举为 PRD 定义的四级层级：FACTORY / EQUIPMENT / UNIT / ISOLATION_POINT
- 新增 IsolationPointDetail 实体，1:1 关联 ISOLATION_POINT 类型节点，承载 PID 图纸引用、介质、压力、温度、危害等级、隔离类型、三维坐标、关联图纸 ID
- 新增 LocationChangeRecord 实体，记录隔离点及其他 Location 节点的变更历史，含提交→审批/驳回流程
- 提供 Excel 批量导入隔离点主数据能力
- 前端增强 LocationForm 支持新 type 选项、隔离点专有字段编辑、坐标绑定
- 前端新增变更审批列表页面
- 新增批次导入 UI

**Non-Goals:**
- 完整的工作流引擎接入（仅单级审批，后续 PRD 7.10 扩展）
- 三维模型渲染引擎开发（仅存储坐标，渲染由已有 BlindBoardEditor 或外部系统负责）
- PID 图纸自动识别隔离点（仅人工标注+Excel 导入）
- 与 DCS/PI/MES（PRD 8）的集成对接（后续变更）
- 盲板主数据管理（PRD 7.1.1，已由 blindplate-catalog capability 覆盖）

## Decisions

### 1. type 枚举重构而非新增
现有 area/building/floor/room/equipment 来自 init-skeleton placeholder，不对应领域语义。直接重构为 PRD 四级：FACTORY(工厂) / EQUIPMENT(装置) / UNIT(单元) / ISOLATION_POINT(隔离点)。

**替代方案**：保留旧 type 并新增映射表 → 否决，增加复杂度且存量数据为测试数据可重置。

### 2. IsolationPointDetail 1:1 伴生实体
Location 表保持通用树骨架，隔离点专有字段独立存储在 IsolationPointDetail 中（location_id 外键 1:1）。仅 ISOLATION_POINT 类型 Location 触发伴生记录创建。

**替代方案 A**：全部字段内联到 Location → 否决，违反单一职责，单表过宽
**替代方案 B**：独立 IsolationPoint 实体外键关联 → 否决，树遍历与隔离点查询割裂

### 3. LocationChangeRecord 字段级变更快照
变更审计用独立实体，记录 field_name + old_value + new_value（JSON 序列化），而非整实体版本表。

**理由**：字段级快照查询效率高，审计完整性足够，避免整实体版本表的外键关联复杂度。

### 4. 审批流简化为单级
变更提交 → ADMIN 角色审批/驳回。不接入工作流引擎（Camunda/Flowable），不实现多级/会签/PENDING/APPROVED/REJECTED 三态。

**理由**：YAGNI，PRD 7.10.1 规则引擎未实现前硬编码足够。后续变更可替换。

### 5. 批量导入用 Excel 模板
Excel 模板列：code, name, type, parent_code, medium, pressure, temperature, hazard_level, isolation_type。后端用 EasyExcel 或 Apache POI 解析。

### 6. 坐标为自定义相对坐标
coord_x/coord_y/coord_z 为自定义相对坐标（Double），diagram_id 关联已有 BlindBoardProject 图纸项目。不对外对接三维工厂系统坐标系（PRD 8 后续变更）。

## Risks / Trade-offs

- **[Risk] 存量 type 数据迁移** → 须提供 SQL 迁移脚本将旧 type 映射到新枚举；当前为开发阶段无生产数据，可直接重建
- **[Risk] type 枚举重构为 Breaking Change** → 前端 LocationForm type 选项需同步修改；i18n key 同步更新
- **[Risk] 1:1 关联创建时机** → ISOLATION_POINT 节点创建时需同步创建 IsolationPointDetail，使用 JPA @OneToOne + cascade ALL 保证一致性；事务协调
- **[Trade-off] 单级审批无法满足多级场景** → 接受限制，后续变更替换
- **[Trade-off] 字段级快照仅适用单层嵌套对象** → 复杂嵌套对象变更仅记录顶层字段名 + JSON 快照
- **[Risk] 批量导入部分失败处理** → 采用事务回滚或记录错误行号返回给前端；选择记录错误行号模式（部分成功）便于用户修正后重试

## Migration Plan

1. 后端：新增 IsolationPointDetail、LocationChangeRecord 实体 + repository/service/controller
2. 后端：重构 Location.type 枚举，DataInitializer 更新初始化数据
3. 后端：新增 Excel 导入 endpoint + 变更审批 endpoint
4. 前端：更新 LocationForm type 选项 + 隔离点专有字段表单
5. 前端：新增 IsolationPointImport 组件 + ChangeApproval 列表页
6. 数据迁移：执行 SQL 脚本重置 Location 表 type 为新枚举（开发阶段可直接 DROP+RECREATE）

## Open Questions

1. 存量 Location 数据是否需要保留迁移？开发阶段建议直接重置。
2. 批量导入是否需要支持下载失败行修正模板？当前设计返回错误列表。
3. 变更审批是否需对接 PRD 7.10.2 RBAC 角色权限？当前硬编码 ADMIN。
4. 隔离点位置坐标是否对接外部三维工厂系统？当前为自定义相对坐标。
