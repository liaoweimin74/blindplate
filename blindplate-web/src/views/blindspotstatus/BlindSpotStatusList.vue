<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Refresh, View as ViewIcon } from '@element-plus/icons-vue'
import { getBlindSpotStatusList, getBlindSpotStatusHistory } from '@/api/blindspotstatus'
import { getLocations } from '@/api/location'
import type { BlindSpotStatus, StatusHistoryItem, Location } from '@/types'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref<BlindSpotStatus[]>([])
const locations = ref<Location[]>([])

const filterLocationId = ref<number | undefined>(undefined)
const filterStatus = ref<string>('')
const filterAbnormalOnly = ref(false)

const historyVisible = ref(false)
const historyLoading = ref(false)
const historyData = ref<StatusHistoryItem[]>([])
const historyLocationName = ref('')

const statusOptions = [
  { value: '', label: 'blindSpotStatus.filterStatus' },
  { value: '通', label: 'status.pass' },
  { value: '盲', label: 'status.blind' },
  { value: '未知', label: 'status.unknown' }
]

const locationTreeProps = {
  value: 'id',
  label: 'name',
  children: 'children'
}

const locationTreeData = computed(() => {
  const map = new Map<number, Location & { children: Location[] }>()
  const roots: (Location & { children: Location[] })[] = []
  locations.value.forEach((loc) => {
    map.set(loc.id, { ...loc, children: [] })
  })
  locations.value.forEach((loc) => {
    const node = map.get(loc.id)!
    if (loc.parentId && map.has(loc.parentId)) {
      map.get(loc.parentId)!.children.push(node)
    } else {
      roots.push(node)
    }
  })
  return roots
})

function statusTagType(status: string): string {
  switch (status) {
    case '通': return 'success'
    case '盲': return 'danger'
    case '盲板已拆除': return 'warning'
    default: return 'info'
  }
}

function opTypeLabel(type: string): string {
  switch (type) {
    case 'INSTALL': return t('blindSpotStatus.install')
    case 'REMOVE': return t('blindSpotStatus.remove')
    case 'INSPECT': return t('blindSpotStatus.inspect')
    default: return type
  }
}

function opTypeTagType(type: string): string {
  switch (type) {
    case 'INSTALL': return 'danger'
    case 'REMOVE': return 'success'
    case 'INSPECT': return 'info'
    default: return 'info'
  }
}

function formatDuration(hours: number | null): string {
  if (hours === null || hours === undefined) return '-'
  if (hours < 24) return `${Math.round(hours)}小时`
  const days = Math.floor(hours / 24)
  const remainHours = Math.round(hours % 24)
  return `${days}天${remainHours}小时`
}

function formatTime(time: string): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

function rowClassName({ row }: { row: BlindSpotStatus }): string {
  return row.abnormal ? 'abnormal-row' : ''
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {}
    if (filterLocationId.value) params.locationId = filterLocationId.value
    if (filterStatus.value) params.status = filterStatus.value
    if (filterAbnormalOnly.value) params.abnormalOnly = true
    const res = await getBlindSpotStatusList(params)
    tableData.value = res.data || []
  } catch {
    ElMessage.error(t('message.error'))
  } finally {
    loading.value = false
  }
}

async function fetchLocations() {
  try {
    const res = await getLocations() as unknown as { data: Location[] }
    locations.value = res.data || []
  } catch {
    // silent fail - filter is optional
  }
}

async function showHistory(row: BlindSpotStatus) {
  historyLocationName.value = row.locationName
  historyVisible.value = true
  historyLoading.value = true
  try {
    const res = await getBlindSpotStatusHistory(row.locationId)
    historyData.value = res.data || []
  } catch {
    ElMessage.error(t('message.error'))
    historyData.value = []
  } finally {
    historyLoading.value = false
  }
}

function handleFilterChange() {
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchLocations()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ t('page.blindSpotStatus') }}</h1>
      <p class="page-subtitle">{{ t('page.blindSpotStatusSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-tree-select
              v-model="filterLocationId"
              :data="locationTreeData"
              :props="locationTreeProps"
              :placeholder="t('blindSpotStatus.filterDeviceArea')"
              clearable
              check-strictly
              class="filter-item"
              @change="handleFilterChange"
            />
            <el-select
              v-model="filterStatus"
              :placeholder="t('blindSpotStatus.filterStatus')"
              clearable
              class="filter-item"
              @change="handleFilterChange"
            >
              <el-option
                v-for="opt in statusOptions"
                :key="opt.value"
                :label="opt.value === '' ? t('blindSpotStatus.filterStatus') : t(opt.label)"
                :value="opt.value"
              />
            </el-select>
            <el-switch
              v-model="filterAbnormalOnly"
              :active-text="t('blindSpotStatus.filterAbnormalOnly')"
              @change="handleFilterChange"
            />
          </div>
          <div class="header-right">
            <el-button :icon="Refresh" @click="fetchData">
              {{ t('blindSpotStatus.refresh') }}
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" :row-class-name="rowClassName" class="data-table">
        <el-table-column prop="locationName" :label="t('blindSpotStatus.location')" min-width="120" />
        <el-table-column prop="parentPath" :label="t('blindSpotStatus.locationPath')" min-width="180" show-overflow-tooltip />
        <el-table-column :label="t('blindSpotStatus.currentStatus')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.currentStatus)" size="small">
              {{ t('status.' + (row.currentStatus === '通' ? 'pass' : row.currentStatus === '盲' ? 'blind' : 'unknown')) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentBlindPlateCode" :label="t('blindSpotStatus.currentBlindPlate')" width="130" />
        <el-table-column prop="relatedOrderNo" :label="t('blindSpotStatus.relatedOrder')" width="130" />
        <el-table-column :label="t('blindSpotStatus.lastOperationTime')" width="160">
          <template #default="{ row }">
            {{ formatTime(row.lastOperationTime) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('blindSpotStatus.duration')" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.statusDurationHours) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('blindSpotStatus.abnormal')" width="120" align="center">
          <template #default="{ row }">
            <el-tooltip v-if="row.abnormal" :content="row.abnormalDescription" placement="top">
              <el-tag type="warning" size="small">{{ t('blindSpotStatus.abnormal') }}</el-tag>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('blindSpotStatus.operation')" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showHistory(row)">
              <el-icon><ViewIcon /></el-icon>
              {{ t('blindSpotStatus.viewHistory') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="historyVisible" :title="t('blindSpotStatus.historyTitle')" width="600px">
      <div v-loading="historyLoading">
        <p v-if="historyLocationName" style="margin-bottom: 12px; font-weight: 600;">
          {{ historyLocationName }}
        </p>
        <el-timeline v-if="historyData.length > 0">
          <el-timeline-item
            v-for="item in historyData"
            :key="item.operationOrderId"
            :timestamp="formatTime(item.operationTime)"
            placement="top"
          >
            <div class="history-item">
              <el-tag :type="opTypeTagType(item.operationType)" size="small" style="margin-right: 8px;">
                {{ opTypeLabel(item.operationType) }}
              </el-tag>
              <span v-if="item.blindPlateCode" style="margin-right: 8px;">
                {{ item.blindPlateCode }}
              </span>
              <el-tag :type="statusTagType(item.resultingStatus)" size="small">
                {{ t('status.' + (item.resultingStatus === '通' ? 'pass' : item.resultingStatus === '盲' ? 'blind' : 'unknown')) }}
              </el-tag>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else :description="t('blindSpotStatus.noHistory')" />
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.filter-item {
  width: 200px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.history-item {
  display: flex;
  align-items: center;
}
</style>

<style>
.abnormal-row {
  background-color: var(--el-color-warning-light-9) !important;
}
</style>
