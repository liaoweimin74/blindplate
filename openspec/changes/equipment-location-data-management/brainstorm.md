## Design Summary

实现 PRD 7.1.2 装置/位置数据管理，扩展现有 Location 装置位置树为 PRD 定义的四级层级结构（工厂→装置→单元→隔离点），并增加隔离点主数据维护（PID 台账、批量导入）、隔离点位置标注（3D 模型/流程图坐标绑定）、隔离点数据变更管理（审批流+变更历史）三大能力。

**现状分析：**
- 后端 `Location` 实体为通用树结构（id, parentId, name, type, description, children），5 种 type（area/building/floor/room/equipment），不含 PRD 定义的隔离点元数据字段
- 前端 `LocationForm.vue` + `api/location.ts` 仅有基础 CRUD + tree 查询
- 现有 `location-tree` spec 仅定义了三级（device_area→pipeline_no→flange_no）基础 CRUD

**目标变更：**
1. **装置层级结构维护**：重构 type 枚举为 FACTORY/EQUIPMENT/UNIT/ISOLATION_POINT 四级，引入 level 字段约束层级关系
2. **隔离点主数据维护**：新增 `IsolationPointDetail` 实体（1:1 关联 ISOLATION_POINT 类型 Location 节点），承载 PID 图纸引用、介质、压力、温度、危害等级、隔离点编码等专有字段；支持 Excel 批量导入
3. **隔离点位置标注**：IsolationPointDetail 中存储坐标（coord_x/coord_y/coord_z）+ 关联图纸 ID（diagram_id 对接现有 BlindBoardProject）
4. **隔离点数据变更管理**：新增 `LocationChangeRecord` 实体记录变更历史，含审批状态（PENDING/APPROVED/REJECTED）、变更类型、变更前后快照、审批人、审批意见

## Alternatives Considered

### 方案 A：内联扩展 Location 实体（所有字段加到 Location 表）
- **做法**：在 Location 实体直接添加 pid_diagram_ref、medium、pressure、temperature、hazard_level、coordinates 等字段，仅 ISOLATION_POINT 类型节点填充
- **优点**：改动最小，无新实体，复用现有 CRUD
- **缺点**：Location 实体职责过重，大量字段对 FACTORY/EQUIPMENT/UNIT 节点无意义，数据模型不清晰，单表过宽
- **为何未采用**：违反单一职责原则，长期维护成本高

### 方案 B：独立 IsolationPoint 实体，外键关联 Location
- **做法**：IsolationPoint 作为独立实体，location_id 外键关联 Location 树中的 ISOLATION_POINT 节点，承载所有隔离点专有字段
- **优点**：完全解耦，数据模型清晰
- **缺点**：查询隔离点需 JOIN Location，层级查询和隔离点查询分离增加 API 复杂度
- **为何未采用**：隔离点本质是 Location 树叶节点，独立实体导致概念割裂，树遍历需额外关联

## Agreed Approach

采用 **方案 C：扩展 Location 类类型 + 新增 IsolationPointDetail 伴生实体**

- **Location 实体扩展**：
  - type 枚举重构为 `FACTORY`(工厂) / `EQUIPMENT`(装置) / `UNIT`(单元) / `ISOLATION_POINT`(隔离点)
  - 新增 `code` 字段（隔离点编码，唯一）
  - 新增 `level` 字段（层级深度，树约束校验）
  - 保留现有 parentId/children 树结构

- **IsolationPointDetail 实体（新增，1:1 关联 ISOLATION_POINT 类型 Location）**：
  - `location_id` → Location.id（外键）
  - `pid_diagram_ref`：PID 图纸引用（文本路径或文件 ID）
  - `medium`：介质
  - `pressure`：压力（MPa）
  - `temperature`：温度（℃）
  - `hazard_level`：危害等级（A/B/C/D 对应 PRD 7.10.1 风险分级）
  - `isolation_type`：隔离类型（BLIND_PLATE/DOUBLE_BLOCK/VALVE 等）
  - `coord_x`/`coord_y`/`coord_z`：三维坐标
  - `diagram_id`：关联图纸项目 ID（对接 BlindBoardProject）

- **LocationChangeRecord 实体（新增，变更审计日志）**：
  - `location_id` → Location.id
  - `change_type`：变更类型（CREATE/UPDATE/DELETE/MOVE）
  - `field_name`/`old_value`/`new_value`：字段级变更快照（JSON）
  - `status`：审批状态（PENDING/APPROVED/REJECTED）
  - `applicant_id`/`approver_id`：申请人与审批人
  - `approval_comment`：审批意见
  - `created_at`/`approved_at`：时间戳

- **为什么选择方案 C**：保持 Location 树结构的统一性，隔离点专有字段独立存储避免数据冗余，变更审计独立实体清晰可追溯，数据模型与 PRD 7.1.2 四大需求一一对应

## Key Decisions

1. **type 枚举重构而非新增**：现有 area/building/floor/room/equipment 是 init-project-skeleton 的 placeholder，重构为 PRD 定义的四级层级更符合领域语义。需数据迁移脚本处理存量数据。
2. **IsolationPointDetail 1:1 而非继承**：JPA TABLE_PER_CLASS 继承查询性能差，1:1 伴生实体更灵活且符合 JPA 惯例。
3. **变更记录用独立实体**：字段级变更快照（JSON）而非整实体版本表，兼顾查询效率和审计完整性。
4. **坐标绑定参考 BlindBoardProject**：复用现有盲板看板项目的图纸体系，diagram_id 外键关联已有 BoardProject。
5. **批量导入用 Excel**：复用 EasyExcel/POI 解析，模板含 code/name/type/parent_code/medium/pressure/temperature/hazard_level 列。
6. **审批流为简化版**：暂实现单级审批（提交→审批/驳回），不接入完整工作流引擎，后续 PRD 7.10 可扩展。

## Open Questions

1. 现有 Location 存量数据的迁移策略——是否需要保留旧 type 并映射到新四级？还是项目为初始化阶段可直接重置？
2. 批量导入是否需要支持 PID 图纸上自动识别隔离点？还是仅支持 Excel 模板线下填写后上传？
3. 变更审批是否需对接现有 User 权限角色（PRD 7.10.2）的审批权限配置？还是先用硬编码 ADMIN 角色审批？
4. 隔离点位置标注在三维模型上的坐标系统是自定义相对坐标还是对接外部三维工厂系统（PRD 8 集成）？
