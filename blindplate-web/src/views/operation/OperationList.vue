<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.operations') }}</h1>
      <p class="page-subtitle">{{ $t('page.operationsSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchQuery"
              placeholder="Search by order number"
              clearable
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="statusFilter" placeholder="Status" clearable class="status-select">
              <el-option label="All" value="" />
              <el-option label="Pending" value="pending" />
              <el-option label="In Progress" value="in_progress" />
              <el-option label="Completed" value="completed" />
              <el-option label="Cancelled" value="cancelled" />
            </el-select>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              New Order
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredOrders" v-loading="loading" class="data-table">
        <el-table-column prop="orderNo" label="Order No." width="150" />
        <el-table-column prop="type" label="Type" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)" effect="plain">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedDate" label="Planned Date" width="150" />
        <el-table-column prop="description" label="Description" min-width="200" show-overflow-tooltip />
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

    <OperationForm
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
import type { OperationOrder } from '@/types'
import OperationForm from '@/components/OperationForm.vue'

const orders = ref<OperationOrder[]>([])
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const editData = ref<OperationOrder | null>(null)

const filteredOrders = computed(() => {
  let result = orders.value
  if (statusFilter.value) {
    result = result.filter(o => o.status === statusFilter.value)
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(o => o.orderNo.toLowerCase().includes(query))
  }
  return result
})

function getTypeColor(type: string) {
  switch (type) {
    case 'install': return 'success'
    case 'remove': return 'warning'
    case 'inspect': return 'info'
    default: return ''
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'completed': return 'success'
    case 'in_progress': return 'primary'
    case 'pending': return 'warning'
    case 'cancelled': return 'danger'
    default: return 'info'
  }
}

function formatStatus(status: string) {
  return status.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase())
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/operations')
    orders.value = res.data.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog(row: OperationOrder) {
  editData.value = { ...row }
  dialogVisible.value = true
}

async function handleFormSubmit(_data: any) {
  // TODO: Implement save logic
  ElMessage.success(editData.value ? 'Updated successfully' : 'Created successfully')
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Are you sure you want to delete this order?', 'Confirm Delete', { type: 'warning' })
  await axios.delete(`/api/v1/operations/${id}`)
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
.status-select {
  width: 150px;
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
