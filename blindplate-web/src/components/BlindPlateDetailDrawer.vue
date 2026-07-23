<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getStatusHistory } from '@/api/blindplate'
import { getInspectionsByBlindPlate, createBlindPlateInspection, deleteBlindPlateInspection } from '@/api/blindplate-inspection'
import { getScrapsByBlindPlate, submitScrap, approveScrap } from '@/api/scrap'
import type { BlindPlate, BlindPlateStatusHistory, BlindPlateInspection, BlindPlateScrapRecord } from '@/types'

const props = defineProps<{
  visible: boolean
  blindPlate: BlindPlate | null
}>()

const emit = defineEmits<{ (e: 'update:visible', v: boolean): void }>()

const activeTab = ref('basic')
const statusHistory = ref<BlindPlateStatusHistory[]>([])
const inspections = ref<BlindPlateInspection[]>([])
const scraps = ref<BlindPlateScrapRecord[]>([])
const loading = ref(false)

// Inspection form
const inspectionDialogVisible = ref(false)
const inspectionForm = ref({ inspectionDate: '', result: 'qualified', nextInspectionDate: '', inspector: '', remark: '' })

// Scrap form
const scrapDialogVisible = ref(false)
const scrapForm = ref({ applicant: '', reason: '' })

watch(() => props.blindPlate?.id, (id) => {
  if (id && props.visible) {
    loadAll(id)
  }
})

watch(() => props.visible, (v) => {
  if (v && props.blindPlate?.id) {
    loadAll(props.blindPlate.id)
  }
})

async function loadAll(id: number) {
  loading.value = true
  try {
    const [hist, insp, scr]: any[] = await Promise.all([
      getStatusHistory(id),
      getInspectionsByBlindPlate(id),
      getScrapsByBlindPlate(id)
    ])
    statusHistory.value = hist.data || []
    inspections.value = insp.data || []
    scraps.value = scr.data || []
  } finally {
    loading.value = false
  }
}

const statusLabels: Record<string, string> = {
  in_stock: '在库', in_use: '在用', in_inspection: '送检中', scrapped: '已报废', lost: '遗失',
  normal: '正常', inspection_due: '待检验', overdue: '超期'
}

const resultLabels: Record<string, string> = { qualified: '合格', unqualified: '不合格', pending: '待检' }
const scrapStatusLabels: Record<string, string> = { pending: '待审批', approved: '已通过', rejected: '已驳回' }

function openInspectionDialog() {
  inspectionForm.value = { inspectionDate: '', result: 'qualified', nextInspectionDate: '', inspector: '', remark: '' }
  inspectionDialogVisible.value = true
}

async function handleInspectionSubmit() {
  if (!props.blindPlate) return
  try {
    await createBlindPlateInspection(props.blindPlate.id, inspectionForm.value)
    ElMessage.success('检验记录已添加')
    inspectionDialogVisible.value = false
    loadAll(props.blindPlate.id)
  } catch (e) { /* handled */ }
}

async function handleInspectionDelete(id: number) {
  if (!props.blindPlate) return
  await ElMessageBox.confirm('确认删除该检验记录？', '提示', { type: 'warning' })
  await deleteBlindPlateInspection(props.blindPlate.id, id)
  ElMessage.success('已删除')
  loadAll(props.blindPlate.id)
}

function openScrapDialog() {
  scrapForm.value = { applicant: '', reason: '' }
  scrapDialogVisible.value = true
}

async function handleScrapSubmit() {
  if (!props.blindPlate) return
  try {
    await submitScrap({ blindPlateId: props.blindPlate.id, applicant: scrapForm.value.applicant, reason: scrapForm.value.reason })
    ElMessage.success('报废申请已提交')
    scrapDialogVisible.value = false
    loadAll(props.blindPlate.id)
  } catch (e) { /* handled */ }
}

async function handleApprove(scrapId: number, approved: boolean) {
  const comment = approved ? '审批通过' : '审批驳回'
  await approveScrap(scrapId, { approved, approver: 'admin', comment })
  ElMessage.success('已处理')
  if (props.blindPlate) loadAll(props.blindPlate.id)
}

function handleClose() {
  emit('update:visible', false)
}
</script>

<template>
  <el-drawer :model-value="visible" @update:model-value="handleClose" title="盲板详情" size="60%" direction="rtl">
    <el-tabs v-model="activeTab" v-loading="loading">
      <el-tab-pane label="基本信息" name="basic">
        <el-descriptions v-if="blindPlate" :column="2" border>
          <el-descriptions-item label="编号">{{ blindPlate.code }}</el-descriptions-item>
          <el-descriptions-item label="名称">{{ blindPlate.name }}</el-descriptions-item>
          <el-descriptions-item label="型号">{{ blindPlate.modelType }}</el-descriptions-item>
          <el-descriptions-item label="规格">{{ blindPlate.spec }}</el-descriptions-item>
          <el-descriptions-item label="材质">{{ blindPlate.material }}</el-descriptions-item>
          <el-descriptions-item label="厚度">{{ blindPlate.thickness }} mm</el-descriptions-item>
          <el-descriptions-item label="直径">{{ blindPlate.diameter }} mm</el-descriptions-item>
          <el-descriptions-item label="压力">{{ blindPlate.pressure }} MPa</el-descriptions-item>
          <el-descriptions-item label="制造商">{{ blindPlate.manufacturer }}</el-descriptions-item>
          <el-descriptions-item label="出厂编号">{{ blindPlate.factoryCode }}</el-descriptions-item>
          <el-descriptions-item label="采购日期">{{ blindPlate.purchaseDate }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusLabels[blindPlate.status] || blindPlate.status }}</el-descriptions-item>
          <el-descriptions-item label="生命周期">{{ statusLabels[blindPlate.lifecycleStatus] || blindPlate.lifecycleStatus }}</el-descriptions-item>
          <el-descriptions-item label="下次检验">{{ blindPlate.nextInspectionDate }}</el-descriptions-item>
          <el-descriptions-item label="安装次数">{{ blindPlate.installCount }}</el-descriptions-item>
          <el-descriptions-item label="累计使用天数">{{ blindPlate.totalUsageDays }}</el-descriptions-item>
          <el-descriptions-item label="二维码">{{ blindPlate.qrCode }}</el-descriptions-item>
          <el-descriptions-item label="RFID">{{ blindPlate.rfidTag }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ blindPlate.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="状态历史" name="history">
        <el-table :data="statusHistory" border>
          <el-table-column prop="previousStatus" label="原状态" width="120">
            <template #default="{ row }">{{ statusLabels[row.previousStatus] || row.previousStatus }}</template>
          </el-table-column>
          <el-table-column prop="newStatus" label="新状态" width="120">
            <template #default="{ row }">{{ statusLabels[row.newStatus] || row.newStatus }}</template>
          </el-table-column>
          <el-table-column prop="operator" label="操作人" width="100" />
          <el-table-column prop="changedAt" label="变更时间" width="180" />
          <el-table-column prop="reason" label="原因" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="检验记录" name="inspection">
        <div style="margin-bottom: 12px">
          <el-button type="primary" size="small" @click="openInspectionDialog">
            <el-icon><Plus /></el-icon>新增检验
          </el-button>
        </div>
        <el-table :data="inspections" border>
          <el-table-column prop="inspectionDate" label="检验日期" width="120" />
          <el-table-column label="结果" width="80">
            <template #default="{ row }">
              <el-tag :type="row.result === 'qualified' ? 'success' : row.result === 'unqualified' ? 'danger' : 'warning'" size="small">
                {{ resultLabels[row.result] || row.result }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="nextInspectionDate" label="下次检验" width="120" />
          <el-table-column prop="inspector" label="检验人" width="100" />
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button size="small" text type="danger" @click="handleInspectionDelete(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="报废信息" name="scrap">
        <div style="margin-bottom: 12px">
          <el-button type="danger" size="small" @click="openScrapDialog" :disabled="blindPlate?.status === 'scrapped'">
            发起报废申请
          </el-button>
        </div>
        <el-table :data="scraps" border>
          <el-table-column prop="applicant" label="申请人" width="100" />
          <el-table-column prop="reason" label="报废原因" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'" size="small">
                {{ scrapStatusLabels[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approver" label="审批人" width="100" />
          <el-table-column prop="approveComment" label="审批意见" />
          <el-table-column label="操作" width="120" v-if="scraps.some(s => s.status === 'pending')">
            <template #default="{ row }">
              <el-button v-if="row.status === 'pending'" size="small" text type="success" @click="handleApprove(row.id, true)">通过</el-button>
              <el-button v-if="row.status === 'pending'" size="small" text type="danger" @click="handleApprove(row.id, false)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Inspection Dialog -->
    <el-dialog v-model="inspectionDialogVisible" title="新增检验记录" width="500px" append-to-body>
      <el-form :model="inspectionForm" label-width="100px">
        <el-form-item label="检验日期"><el-date-picker v-model="inspectionForm.inspectionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="检验结果">
          <el-select v-model="inspectionForm.result" style="width: 100%">
            <el-option label="合格" value="qualified" />
            <el-option label="不合格" value="unqualified" />
            <el-option label="待检" value="pending" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次检验"><el-date-picker v-model="inspectionForm.nextInspectionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="检验人"><el-input v-model="inspectionForm.inspector" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="inspectionForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inspectionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInspectionSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- Scrap Dialog -->
    <el-dialog v-model="scrapDialogVisible" title="发起报废申请" width="500px" append-to-body>
      <el-form :model="scrapForm" label-width="80px">
        <el-form-item label="申请人"><el-input v-model="scrapForm.applicant" /></el-form-item>
        <el-form-item label="报废原因"><el-input v-model="scrapForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scrapDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleScrapSubmit">提交</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>