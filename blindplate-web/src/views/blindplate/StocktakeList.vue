<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getStocktakes, createStocktake, getStocktake, getStocktakeItems, scanStocktakeCodes, closeStocktake } from '@/api/stocktake'
import type { BlindPlateStocktake, BlindPlateStocktakeItem } from '@/types'

const stocktakes = ref<BlindPlateStocktake[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const createDialogVisible = ref(false)
const createForm = ref({ batchName: '', operator: '' })

const detailVisible = ref(false)
const detailData = ref<BlindPlateStocktake | null>(null)
const detailItems = ref<BlindPlateStocktakeItem[]>([])
const detailLoading = ref(false)
const scanInput = ref('')

const matchStatusLabels: Record<string, string> = {
  matched: '匹配',
  missing: '缺失',
  unexpected: '多余',
  location_mismatch: '位置异常'
}
const matchStatusTagType: Record<string, string> = {
  matched: 'success',
  missing: 'danger',
  unexpected: 'warning',
  location_mismatch: 'warning',
  pending: 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getStocktakes({ page: currentPage.value - 1, size: pageSize.value })
    stocktakes.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  createForm.value = { batchName: '', operator: '' }
  createDialogVisible.value = true
}

async function handleCreate() {
  await createStocktake(createForm.value)
  ElMessage.success('盘点批次已创建')
  createDialogVisible.value = false
  fetchData()
}

async function openDetail(row: BlindPlateStocktake) {
  detailData.value = row
  detailVisible.value = true
  await loadItems(row.id)
}

async function loadItems(id: number) {
  detailLoading.value = true
  try {
    const res: any = await getStocktakeItems(id)
    detailItems.value = res.data || []
  } finally {
    detailLoading.value = false
  }
}

async function handleScan() {
  if (!detailData.value || !scanInput.value.trim()) return
  const codes = scanInput.value.trim().split('\n').map(s => s.trim()).filter(s => s)
  try {
    await scanStocktakeCodes(detailData.value.id, codes)
    ElMessage.success(`已提交 ${codes.length} 条扫描记录`)
    scanInput.value = ''
    loadItems(detailData.value.id)
  } catch (e) { /* handled */ }
}

async function handleCloseBatch() {
  if (!detailData.value) return
  await ElMessageBox.confirm('关闭后将生成差异报告，确认关闭？', '提示', { type: 'warning' })
  await closeStocktake(detailData.value!.id)
  ElMessage.success('盘点批次已关闭')
  const res: any = await getStocktake(detailData.value!.id)
  detailData.value = res.data
  loadItems(detailData.value!.id)
  fetchData()
}

function handlePageChange(page: number) { currentPage.value = page; fetchData() }

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">盘点管理</h1>
      <p class="page-subtitle">盲板盘点批次与差异报告</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left"></div>
          <div class="header-right">
            <el-button type="primary" @click="openCreateDialog">
              <el-icon><Plus /></el-icon>新建盘点
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="stocktakes" v-loading="loading" border>
        <el-table-column prop="batchNo" label="批次号" width="180" />
        <el-table-column prop="batchName" label="批次名称" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'closed' ? 'info' : 'success'" size="small">
              {{ row.status === 'closed' ? '已关闭' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="closedAt" label="关闭时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" :total="total" :page-size="pageSize"
          layout="total, prev, pager, next" @current-change="handlePageChange" />
      </div>
    </el-card>

    <!-- Create Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建盘点批次" width="500px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="批次名称"><el-input v-model="createForm.batchName" /></el-form-item>
        <el-form-item label="操作人"><el-input v-model="createForm.operator" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="盘点详情" size="70%" direction="rtl">
      <template v-if="detailData">
        <el-descriptions :column="2" border style="margin-bottom: 16px">
          <el-descriptions-item label="批次号">{{ detailData.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="批次名称">{{ detailData.batchName }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detailData.operator }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detailData.status === 'closed' ? '已关闭' : '进行中' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detailData.status === 'in_progress'" style="margin-bottom: 16px">
          <el-input v-model="scanInput" type="textarea" :rows="4" placeholder="输入盲板编码，每行一个" />
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button type="primary" @click="handleScan">提交扫描</el-button>
            <el-button type="warning" @click="handleCloseBatch">关闭批次</el-button>
          </div>
        </div>

        <el-table :data="detailItems" v-loading="detailLoading" border>
          <el-table-column prop="blindPlateCode" label="盲板编码" width="150" />
          <el-table-column label="匹配状态" width="120">
            <template #default="{ row }">
              <el-tag :type="matchStatusTagType[row.matchStatus] || 'info'" size="small">
                {{ matchStatusLabels[row.matchStatus] || row.matchStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="scannedAt" label="扫描时间" width="180" />
        </el-table>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped>
.page-container { padding: var(--brand-spacing-6); }
.page-header { margin-bottom: var(--brand-spacing-6); }
.page-title { font-size: var(--brand-font-size-2xl); font-weight: var(--brand-font-weight-semibold); color: var(--brand-text-primary); margin: 0 0 var(--brand-spacing-2) 0; }
.page-subtitle { font-size: var(--brand-font-size-base); color: var(--brand-text-secondary); margin: 0; }
.content-card { background: var(--brand-bg-white); border-radius: var(--brand-radius-lg); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: var(--brand-spacing-4); }
</style>