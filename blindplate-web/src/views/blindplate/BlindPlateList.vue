<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import { getBlindPlates, deleteBlindPlate, createBlindPlate, updateBlindPlate } from '@/api/blindplate'
import type { BlindPlate } from '@/types'
import BlindPlateForm from '@/components/BlindPlateForm.vue'
import BlindPlateDetailDrawer from '@/components/BlindPlateDetailDrawer.vue'

const { t } = useI18n()

const blindPlates = ref<BlindPlate[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchQuery = ref('')
const filterStatus = ref('')
const filterLifecycle = ref('')
const filterModelType = ref('')
const dialogVisible = ref(false)
const editData = ref<BlindPlate | null>(null)
const drawerVisible = ref(false)
const drawerData = ref<BlindPlate | null>(null)

const statusTagType: Record<string, string> = {
  in_stock: 'success',
  in_use: 'primary',
  in_inspection: 'warning',
  scrapped: 'danger',
  lost: 'info'
}

const lifecycleTagType: Record<string, string> = {
  normal: 'success',
  inspection_due: 'warning',
  overdue: 'danger',
  scrapped: 'info'
}

const statusLabels: Record<string, string> = {
  in_stock: '在库',
  in_use: '在用',
  in_inspection: '送检中',
  scrapped: '已报废',
  lost: '遗失'
}

const lifecycleLabels: Record<string, string> = {
  normal: '正常',
  inspection_due: '待检验',
  overdue: '超期',
  scrapped: '已报废'
}

const modelTypeOptions = ['8字盲板', '插板', '垫环', '盲法兰', '其他']

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getBlindPlates({
      keyword: searchQuery.value,
      status: filterStatus.value,
      lifecycleStatus: filterLifecycle.value,
      modelType: filterModelType.value,
      page: currentPage.value - 1,
      size: pageSize.value
    })
    blindPlates.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchData()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchData()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  fetchData()
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog(row: BlindPlate) {
  editData.value = { ...row }
  dialogVisible.value = true
}

function showDetailDrawer(row: BlindPlate) {
  drawerData.value = { ...row }
  drawerVisible.value = true
}

async function handleFormSubmit(data: any) {
  try {
    if (editData.value) {
      await updateBlindPlate(editData.value.id, data)
    } else {
      await createBlindPlate(data)
    }
    ElMessage.success(editData.value ? t('message.updated') : t('message.created'))
    fetchData()
  } catch (e) {
    // error handled by interceptor
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm(t('confirm.deleteBlindPlate'), t('confirm.deleteTitle'), { type: 'warning' })
  await deleteBlindPlate(id)
  ElMessage.success(t('message.deleted'))
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.blindplates') }}</h1>
      <p class="page-subtitle">{{ $t('page.blindplatesSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input v-model="searchQuery" placeholder="搜索编号/名称/规格" clearable class="search-input"
              @keyup.enter="handleSearch" @clear="handleSearch">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
            <el-select v-model="filterModelType" placeholder="型号" clearable @change="handleSearch" style="width: 120px">
              <el-option v-for="m in modelTypeOptions" :key="m" :label="m" :value="m" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态" clearable @change="handleSearch" style="width: 120px">
              <el-option v-for="(label, val) in statusLabels" :key="val" :label="label" :value="val" />
            </el-select>
            <el-select v-model="filterLifecycle" placeholder="生命周期" clearable @change="handleSearch" style="width: 120px">
              <el-option v-for="(label, val) in lifecycleLabels" :key="val" :label="label" :value="val" />
            </el-select>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>{{ $t('button.addBlindPlate') }}
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="blindPlates" v-loading="loading" class="data-table">
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="modelType" label="型号" width="100" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="material" label="材质" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status] || 'info'" size="small">{{ statusLabels[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="生命周期" width="100">
          <template #default="{ row }">
            <el-tag :type="lifecycleTagType[row.lifecycleStatus] || 'info'" size="small">{{ lifecycleLabels[row.lifecycleStatus] || row.lifecycleStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextInspectionDate" label="下次检验" width="120" />
        <el-table-column prop="qrCode" label="二维码" width="160" />
        <el-table-column :label="$t('table.actions')" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showDetailDrawer(row)">
              <el-icon><View /></el-icon>详情
            </el-button>
            <el-button size="small" text type="primary" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>{{ $t('button.edit') }}
            </el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>{{ $t('button.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <BlindPlateForm v-model:visible="dialogVisible" :data="editData" @submit="handleFormSubmit" />
    <BlindPlateDetailDrawer v-model:visible="drawerVisible" :blind-plate="drawerData" />
  </div>
</template>

<style scoped>
.page-container { padding: var(--brand-spacing-6); }
.page-header { margin-bottom: var(--brand-spacing-6); }
.page-title { font-size: var(--brand-font-size-2xl); font-weight: var(--brand-font-weight-semibold); color: var(--brand-text-primary); margin: 0 0 var(--brand-spacing-2) 0; }
.page-subtitle { font-size: var(--brand-font-size-base); color: var(--brand-text-secondary); margin: 0; }
.content-card { background: var(--brand-bg-white); border-radius: var(--brand-radius-lg); }
.card-header { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: var(--brand-spacing-4); }
.header-left { display: flex; gap: var(--brand-spacing-3); flex-wrap: wrap; }
.search-input { width: 240px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: var(--brand-spacing-4); }
</style>