<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ $t('page.isolationPointImport') }}</h1>
      <p class="page-subtitle">{{ $t('page.isolationPointImportSubtitle') }}</p>
    </div>

    <el-card>
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
      >
        <el-button type="primary">
          <el-icon><Upload /></el-icon>
          {{ $t('button.selectFile') }}
        </el-button>
        <template #tip>
          <div class="el-upload__tip">{{ $t('message.excelOnly') }}</div>
        </template>
      </el-upload>

      <div style="margin-top: 16px; display: flex; gap: 12px;">
        <el-button type="success" :loading="importing" :disabled="!selectedFile" @click="handleImport">
          {{ $t('button.import') }}
        </el-button>
        <el-button @click="handleDownloadTemplate">
          <el-icon><Download /></el-icon>
          {{ $t('button.downloadTemplate') }}
        </el-button>
      </div>

      <el-alert
        v-if="importResult"
        :title="importTitle"
        :type="importResult.length > 0 ? 'warning' : 'success'"
        show-icon
        style="margin-top: 16px;"
      >
        <template v-if="importResult.length > 0">
          <ul style="margin: 0; padding-left: 20px;">
            <li v-for="(err, idx) in importResult" :key="idx">{{ err }}</li>
          </ul>
        </template>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Upload, Download } from '@element-plus/icons-vue'
import type { UploadFile } from 'element-plus'
import { importIsolationPoints } from '@/api/location'

const { t } = useI18n()

const selectedFile = ref<File | null>(null)
const importing = ref(false)
const importResult = ref<string[]>([])

const importTitle = computed(() => {
  if (importResult.value.length === 0) return t('message.importSuccess')
  return t('message.importWithErrors', { count: importResult.value.length })
})

function handleFileChange(file: UploadFile) {
  selectedFile.value = file.raw || null
}

function handleExceed() {
  ElMessage.warning(t('message.oneFileOnly'))
}

async function handleImport() {
  if (!selectedFile.value) return
  importing.value = true
  importResult.value = []
  try {
    const res: any = await importIsolationPoints(selectedFile.value)
    importResult.value = res.data?.data || []
    if (importResult.value.length === 0) {
      ElMessage.success(t('message.importSuccess'))
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || t('message.error'))
  } finally {
    importing.value = false
  }
}

function handleDownloadTemplate() {
  ElMessage.info(t('message.featureComing'))
}
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
</style>
