## 1. 后端实体扩展与数据库迁移

- [ ] 1.1 扩展 BlindPlate 实体，新增字段：modelType, thickness, factoryCode, purchaseDate, currentLocationId, installCount, totalUsageDays, lifecycleStatus, nextInspectionDate, rfidTag, qrCode
- [ ] 1.2 创建 BlindPlateStatusHistory 实体（id, blindPlateId, previousStatus, newStatus, changedAt, operator, reason）
- [ ] 1.3 创建 BlindPlateInspection 实体（id, blindPlateId, inspectionDate, result, nextInspectionDate, inspector, remark, createdAt）
- [ ] 1.4 创建 BlindPlateScrapRecord 实体（id, blindPlateId, applyTime, applicant, status, approver, approveTime, approveComment, reason）
- [ ] 1.5 创建 BlindPlateStocktake 实体和 BlindPlateStocktakeItem 子实体
- [ ] 1.6 创建对应的 Repository 接口（含自定义查询方法）
- [ ] 1.7 在 DataInitializer 中添加状态枚举值迁移脚本

## 2. 后端 API - 盲板目录管理

- [ ] 2.1 修改 BlindPlateController，list 接口改为分页+多维过滤
- [ ] 2.2 新增 POST /api/v1/blindplates/import 端点，支持 Excel 批量导入
- [ ] 2.3 新增 GET /api/v1/blindplates/export 端点，导出为 Excel
- [ ] 2.4 新增 GET /api/v1/blindplates/template 端点，下载导入模板
- [ ] 2.5 修改 BlindPlateService.create()，自动生成 QR 码和 RFID 标签
- [ ] 2.6 修改 BlindPlateService.update()，检测 status 变更时写入 BlindPlateStatusHistory 表
- [ ] 2.7 新增 GET /api/v1/blindplates/{id}/status-history 端点

## 3. 后端 API - 检验管理

- [ ] 3.1 创建 InspectionController，提供 CRUD 端点
- [ ] 3.2 新增 GET /api/v1/blindplates/{id}/inspections 端点
- [ ] 3.3 创建 InspectionService，新增检验记录时更新 nextInspectionDate 和 lifecycleStatus
- [ ] 3.4 创建检验到期定时任务（@Scheduled 每日08:00）
- [ ] 3.5 定时任务中更新 lifecycleStatus 为 inspection_due 或 overdue
- [ ] 3.6 新增 GET /api/v1/blindplates/inspection-alerts 端点

## 4. 后端 API - 报废管理

- [ ] 4.1 创建 ScrapController，提供 POST /api/v1/blindplates/{id}/scrap
- [ ] 4.2 新增 PUT /api/v1/blindplates/scrap/{scrapId}/approve
- [ ] 4.3 创建 ScrapService，报废申请校验
- [ ] 4.4 报废审批通过后更新 status 和 lifecycleStatus 为 scrapped
- [ ] 4.5 新增 GET /api/v1/blindplates/scrap 端点，分页查询报废记录
- [ ] 4.6 新增 GET /api/v1/blindplates/{id}/scrap 端点

## 5. 后端 API - 盘点管理

- [ ] 5.1 创建 StocktakeController，提供 POST /api/v1/blindplates/stocktake
- [ ] 5.2 新增 POST /api/v1/blindplates/stocktake/{batchId}/scan 端点
- [ ] 5.3 新增 PUT /api/v1/blindplates/stocktake/{batchId}/close 端点
- [ ] 5.4 创建 StocktakeService，关闭批次时自动比对差异
- [ ] 5.5 新增 GET /api/v1/blindplates/stocktake 端点
- [ ] 5.6 新增 GET /api/v1/blindplates/stocktake/{batchId} 端点
- [ ] 5.7 盘点批次号自动生成

## 6. 前端类型与 API 层

- [ ] 6.1 扩展 types/index.ts 中的 BlindPlate 接口，新增字段
- [ ] 6.2 新增 TypeScript 类型：BlindPlateStatusHistory, BlindPlateInspection, BlindPlateScrapRecord, BlindPlateStocktake, DifferenceReport
- [ ] 6.3 扩展 api/blindplate.ts，新增 API 函数

## 7. 前端 - 盲板列表页增强

- [ ] 7.1 修改 BlindPlateList.vue，查询改为分页 API 调用
- [ ] 7.2 添加多维筛选栏
- [ ] 7.3 添加批量导入按钮
- [ ] 7.4 添加导出Excel按钮
- [ ] 7.5 添加下载模板按钮
- [ ] 7.6 表格列新增：modelType, thickness, lifecycleStatus, nextInspectionDate, qrCode
- [ ] 7.7 status 列使用 Tag 组件颜色区分
- [ ] 7.8 lifecycleStatus 列使用 Tag 组件颜色区分
- [ ] 7.9 添加查看详情按钮

## 8. 前端 - 盲板表单增强

- [ ] 8.1 修改 BlindPlateForm.vue，新增字段表单项
- [ ] 8.2 更新 status 下拉选项为新枚举值
- [ ] 8.3 modelType 下拉选项
- [ ] 8.4 创建时 qrCode 和 rfidTag 隐藏；编辑时只读显示

## 9. 前端 - 盲板详情抽屉

- [ ] 9.1 创建 BlindPlateDetailDrawer.vue 组件
- [ ] 9.2 Tab1 基本信息
- [ ] 9.3 Tab2 状态历史
- [ ] 9.4 Tab3 检验记录
- [ ] 9.5 Tab4 报废信息

## 10. 前端 - 检验管理

- [ ] 10.1 创建 InspectionForm.vue 弹窗组件
- [ ] 10.2 在详情抽屉检验记录 Tab 中嵌入 InspectionForm
- [ ] 10.3 创建 InspectionAlerts.vue 组件
- [ ] 10.4 告警列表排序和高亮

## 11. 前端 - 报废管理

- [ ] 11.1 创建 ScrapForm.vue 弹窗组件
- [ ] 11.2 创建 ScrapApprovalDialog.vue 弹窗组件
- [ ] 11.3 在盲板列表页添加发起报废操作按钮
- [ ] 11.4 报废列表页面或弹窗

## 12. 前端 - 盘点管理

- [ ] 12.1 创建 StocktakeList.vue 页面
- [ ] 12.2 创建 StocktakeCreateDialog.vue 弹窗
- [ ] 12.3 创建 StocktakeScanInput.vue 组件
- [ ] 12.4 创建 StocktakeDetail.vue 页面
- [ ] 12.5 在 AppSidebar 或路由中添加盘点管理入口

## 13. 前端 - i18n 与路由

- [ ] 13.1 在 locales 中新增国际化条目
- [ ] 13.2 在 router 中新增盘点管理路由

## 14. 后端测试

- [ ] 14.1 编写 BlindPlateService 单元测试
- [ ] 14.2 编写 InspectionService 单元测试
- [ ] 14.3 编写 ScrapService 单元测试
- [ ] 14.4 编写 StocktakeService 单元测试
- [ ] 14.5 编写 Excel 导入测试
- [ ] 14.6 编写 Controller 集成测试

## 15. 前端测试

- [ ] 15.1 编写 BlindPlateList 组件测试
- [ ] 15.2 编写 BlindPlateDetailDrawer 组件测试
- [ ] 15.3 编写 StocktakeDetail 组件测试