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
            <el-input
              v-model="searchQuery"
              :placeholder="$t('placeholder.searchByCode')"
              clearable
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              {{ $t('button.addBlindPlate') }}
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredBlindPlates" v-loading="loading" class="data-table">
        <el-table-column prop="code" :label="$t('table.code')" width="120" />
        <el-table-column prop="name" :label="$t('table.name')" min-width="150" />
        <el-table-column prop="spec" :label="$t('table.spec')" width="150" />
        <el-table-column prop="material" :label="$t('table.material')" width="120" />
        <el-table-column prop="status" :label="$t('table.status')" width="120" />
        <el-table-column :label="$t('table.actions')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              {{ $t('button.edit') }}
            </el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>
              {{ $t('button.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <BlindPlateForm
      v-model:visible="dialogVisible"
      :data="editData"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getBlindPlates, deleteBlindPlate } from '@/api/blindplate'
import type { BlindPlate } from '@/types'
import BlindPlateForm from '@/components/BlindPlateForm.vue'

const { t } = useI18n()

const blindPlates = ref<BlindPlate[]>([])
const loading = ref(false)
const searchQuery = ref('')
const dialogVisible = ref(false)
const editData = ref<BlindPlate | null>(null)

const filteredBlindPlates = computed(() => {
  if (!searchQuery.value) return blindPlates.value
  const query = searchQuery.value.toLowerCase()
  return blindPlates.value.filter(bp =>
    bp.code.toLowerCase().includes(query) ||
    bp.name.toLowerCase().includes(query)
  )
})

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getBlindPlates()
    blindPlates.value = res.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog(row: BlindPlate) {
  editData.value = { ...row }
  dialogVisible.value = true
}

async function handleFormSubmit(_data: any) {
  // TODO: Implement save logic
  ElMessage.success(editData.value ? t('message.updated') : t('message.created'))
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm(t('confirm.deleteBlindPlate'), t('confirm.deleteTitle'), { type: 'warning' })
  await deleteBlindPlate(id)
  ElMessage.success(t('message.deleted'))
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.page-container {
  padding: var(--brand-spacing-6);
}
.page-header {
  margin-bottom: var(--brand-spacing-6);
}
.page-title {
  font-size: var(--brand-font-size-2xl);
  font-weight: var(--brand-font-weight-semibold);
  color: var(--brand-text-primary);
  margin: 0 0 var(--brand-spacing-2) 0;
}
.page-subtitle {
  font-size: var(--brand-font-size-base);
  color: var(--brand-text-secondary);
  margin: 0;
}
.content-card {
  background: var(--brand-bg-white);
  border-radius: var(--brand-radius-lg);
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: var(--brand-spacing-4);
}
.header-left {
  display: flex;
  gap: var(--brand-spacing-3);
}
.header-right {
  display: flex;
  gap: var(--brand-spacing-3);
}
.search-input {
  width: 280px;
}
.data-table {
  width: 100%;
}
:deep(.el-table__header th) {
  background-color: var(--brand-bg-page);
  color: var(--brand-text-primary);
  font-weight: var(--brand-font-weight-medium);
}
:deep(.el-table__row:hover td) {
  background-color: var(--brand-bg-hover);
}
</style>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
