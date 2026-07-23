<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.changeApproval') }}</h1>
      <p class="page-subtitle">{{ $t('page.changeApprovalSubtitle') }}</p>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <el-select v-model="statusFilter" :placeholder="$t('form.selectStatus')" clearable style="width: 160px;" @change="fetchData">
            <el-option :label="$t('filter.all')" value="" />
            <el-option :label="$t('filter.pending')" value="PENDING" />
            <el-option :label="$t('filter.approved')" value="APPROVED" />
            <el-option :label="$t('filter.rejected')" value="REJECTED" />
          </el-select>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="locationId" :label="$t('table.locationName')" width="120" />
        <el-table-column prop="changeType" :label="$t('table.changeType')" width="100">
          <template #default="{ row }">
            <el-tag :type="changeTypeTag(row.changeType)">{{ changeTypeLabel(row.changeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fieldChanged" :label="$t('table.fieldChanged')" width="120" />
        <el-table-column prop="oldValue" :label="$t('table.oldValue')" width="140" />
        <el-table-column prop="newValue" :label="$t('table.newValue')" width="140" />
        <el-table-column prop="status" :label="$t('table.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appliedAt" :label="$t('table.appliedAt')" width="160" />
        <el-table-column :label="$t('table.actions')" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button type="success" size="small" @click="handleApprove(row, true)">
                {{ $t('button.approve') }}
              </el-button>
              <el-button type="danger" size="small" @click="handleApprove(row, false)">
                {{ $t('button.reject') }}
              </el-button>
            </template>
            <span v-else>{{ statusLabel(row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="commentDialogVisible" :title="commentApproved ? $t('dialog.approveConfirm') : $t('dialog.rejectConfirm')" width="400px">
      <el-input v-model="comment" type="textarea" :placeholder="$t('form.enterComment')" :rows="3" />
      <template #footer>
        <el-button @click="commentDialogVisible = false">{{ $t('button.cancel') }}</el-button>
        <el-button :type="commentApproved ? 'success' : 'danger'" @click="submitApproval">{{ $t('button.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getChangeRecords, approveChangeRecord } from '@/api/location'
import type { LocationChangeRecord } from '@/types'

const { t } = useI18n()

const records = ref<LocationChangeRecord[]>([])
const loading = ref(false)
const statusFilter = ref('')

const commentDialogVisible = ref(false)
const commentApproved = ref(true)
const comment = ref('')
const currentRecord = ref<LocationChangeRecord | null>(null)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getChangeRecords(statusFilter.value || undefined)
    records.value = res.data?.data || []
  } finally {
    loading.value = false
  }
}

function changeTypeTag(type: string) {
  const map: Record<string, string> = { CREATE: 'success', UPDATE: 'warning', DELETE: 'danger' }
  return map[type] || 'info'
}

function changeTypeLabel(type: string) {
  const map: Record<string, string> = { CREATE: t('tag.create'), UPDATE: t('tag.update'), DELETE: t('tag.delete') }
  return map[type] || type
}

function statusTag(status: string) {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status: string) {
  const map: Record<string, string> = { PENDING: t('tag.pending'), APPROVED: t('tag.approved'), REJECTED: t('tag.rejected') }
  return map[status] || status
}

function handleApprove(row: LocationChangeRecord, approved: boolean) {
  currentRecord.value = row
  commentApproved.value = approved
  comment.value = ''
  commentDialogVisible.value = true
}

async function submitApproval() {
  if (!currentRecord.value) return
  try {
    await approveChangeRecord(currentRecord.value.id, {
      approved: commentApproved.value,
      comment: comment.value
    })
    ElMessage.success(commentApproved.value ? t('message.approved') : t('message.rejected'))
    commentDialogVisible.value = false
    fetchData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || t('message.error'))
  }
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
.card-header {
  display: flex;
  justify-content: flex-end;
}
</style>
