<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.inspections') }}</h1>
      <p class="page-subtitle">{{ $t('page.inspectionsSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchQuery"
              placeholder="Search by plan name"
              clearable
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="frequencyFilter" placeholder="Frequency" clearable class="frequency-select">
              <el-option label="All" value="" />
              <el-option label="Daily" value="daily" />
              <el-option label="Weekly" value="weekly" />
              <el-option label="Monthly" value="monthly" />
            </el-select>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              New Plan
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredPlans" v-loading="loading" class="data-table">
        <el-table-column prop="name" label="Plan Name" min-width="200" />
        <el-table-column prop="frequency" label="Frequency" width="120">
          <template #default="{ row }">
            <el-tag :type="getFrequencyColor(row.frequency)" effect="plain">
              {{ row.frequency }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextDate" label="Next Execution" width="150" />
        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              Edit
            </el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <InspectionForm
      v-model:visible="dialogVisible"
      :data="editData"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'
import InspectionForm from '@/components/InspectionForm.vue'

const plans = ref<any[]>([])
const loading = ref(false)
const searchQuery = ref('')
const frequencyFilter = ref('')
const dialogVisible = ref(false)
const editData = ref<any>(null)

const filteredPlans = computed(() => {
  let result = plans.value
  if (frequencyFilter.value) {
    result = result.filter(p => p.frequency === frequencyFilter.value)
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(p => p.name.toLowerCase().includes(query))
  }
  return result
})

function getFrequencyColor(frequency: string) {
  switch (frequency) {
    case 'daily': return 'danger'
    case 'weekly': return 'warning'
    case 'monthly': return 'info'
    default: return ''
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'active': return 'success'
    case 'paused': return 'warning'
    case 'completed': return 'info'
    default: return ''
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/inspections')
    plans.value = res.data.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog(row: any) {
  editData.value = { ...row }
  dialogVisible.value = true
}

async function handleFormSubmit(_data: any) {
  // TODO: Implement save logic
  ElMessage.success(editData.value ? 'Updated successfully' : 'Created successfully')
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Are you sure you want to delete this inspection plan?', 'Confirm Delete', { type: 'warning' })
  await axios.delete(`/api/v1/inspections/${id}`)
  ElMessage.success('Deleted successfully')
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
  width: 220px;
}
.frequency-select {
  width: 140px;
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
