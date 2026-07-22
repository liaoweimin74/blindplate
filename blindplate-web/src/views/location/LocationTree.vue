<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.locations') }}</h1>
      <p class="page-subtitle">{{ $t('page.locationsSubtitle') }}</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="8">
        <el-card class="tree-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">{{ $t('menu.locations') }}</span>
              <el-button type="primary" size="small" @click="showCreateDialog">
                <el-icon><Plus /></el-icon>
                Add
              </el-button>
            </div>
          </template>
          <el-input
            v-model="searchQuery"
            :placeholder="$t('placeholder.searchLocations')"
            clearable
            class="tree-search"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-tree
            :data="treeData"
            :props="{ label: 'name', children: 'children' }"
            :filter-node-method="filterNode"
            ref="treeRef"
            v-loading="loading"
            class="location-tree"
            highlight-current
            @node-click="handleNodeClick"
          />
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">{{ $t('table.locationName') }}</span>
              <el-button v-if="selectedNode" type="primary" size="small" @click="showEditDialog">
<el-icon><Edit /></el-icon>
              {{ $t('button.edit') }}
              </el-button>
            </div>
          </template>
          <div v-if="selectedNode" class="detail-content">
            <el-descriptions :column="2" border>
              <el-descriptions-item :label="$t('descriptions.name')">{{ selectedNode.name }}</el-descriptions-item>
              <el-descriptions-item :label="$t('descriptions.code')">{{ selectedNode.code }}</el-descriptions-item>
              <el-descriptions-item :label="$t('descriptions.type')">{{ selectedNode.type }}</el-descriptions-item>
              <el-descriptions-item :label="$t('descriptions.parent')">{{ selectedNode.parentName || $t('descriptions.root') }}</el-descriptions-item>
              <el-descriptions-item :label="$t('descriptions.description')" :span="2">{{ selectedNode.description || 'N/A' }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else :description="$t('empty.selectLocation')" />
        </el-card>
      </el-col>
    </el-row>

    <LocationForm
      v-model:visible="dialogVisible"
      :data="editData"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus, Search, Edit } from '@element-plus/icons-vue'
import type { Location } from '@/types'
import type { ElTree } from 'element-plus'
import LocationForm from '@/components/LocationForm.vue'

const treeData = ref<Location[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedNode = ref<Location | null>(null)
const treeRef = ref<InstanceType<typeof ElTree>>()
const dialogVisible = ref(false)
const editData = ref<Location | null>(null)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/locations/tree')
    treeData.value = res.data.data
  } finally {
    loading.value = false
  }
}

function filterNode(value: string, data: Location) {
  if (!value) return true
  return data.name.toLowerCase().includes(value.toLowerCase())
}

watch(searchQuery, (val) => {
  treeRef.value?.filter(val)
})

function handleNodeClick(data: Location) {
  selectedNode.value = data
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog() {
  editData.value = selectedNode.value ? { ...selectedNode.value } : null
  dialogVisible.value = true
}

async function handleFormSubmit(_data: any) {
  // TODO: Implement save logic
  ElMessage.success(editData.value ? 'Updated successfully' : 'Created successfully')
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
.tree-card, .detail-card {
  background: var(--brand-bg-white);
  border-radius: var(--brand-radius-lg);
  min-height: 500px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-size: var(--brand-font-size-lg);
  font-weight: var(--brand-font-weight-semibold);
  color: var(--brand-text-primary);
}
.tree-search {
  margin-bottom: var(--brand-spacing-4);
}
.location-tree {
  height: 400px;
  overflow: auto;
}
:deep(.el-tree-node__content) {
  height: 36px;
  border-radius: var(--brand-radius-md);
}
:deep(.el-tree-node__content:hover) {
  background-color: var(--brand-bg-hover);
}
:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: var(--brand-color-primary-light);
  color: var(--brand-color-primary);
}
.detail-content {
  padding: var(--brand-spacing-4) 0;
}
</style>
