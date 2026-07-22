<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'

const { t } = useI18n()

const form = ref({
  companyName: 'Blind Plate Management System',
  notificationEmail: 'admin@example.com',
  autoBackup: true,
  backupFrequency: 'daily',
  maxLoginAttempts: 5,
  sessionTimeout: 30,
  enableAuditLog: true
})

const loading = ref(false)

async function handleSave() {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success(t('message.settingsSaved'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ t('page.settings') }}</h1>
      <p class="page-subtitle">{{ t('page.settingsSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <el-form :model="form" :label-width="180" style="max-width: 600px">
        <el-divider content-position="left">{{ t('settings.general') }}</el-divider>
        <el-form-item :label="t('settings.companyName')">
          <el-input v-model="form.companyName" />
        </el-form-item>
        <el-form-item :label="t('settings.notificationEmail')">
          <el-input v-model="form.notificationEmail" />
        </el-form-item>

        <el-divider content-position="left">{{ t('settings.backup') }}</el-divider>
        <el-form-item :label="t('settings.autoBackup')">
          <el-switch v-model="form.autoBackup" />
        </el-form-item>
        <el-form-item :label="t('settings.backupFrequency')" v-if="form.autoBackup">
          <el-select v-model="form.backupFrequency" style="width: 100%">
            <el-option :label="t('tag.daily')" value="daily" />
            <el-option :label="t('tag.weekly')" value="weekly" />
            <el-option :label="t('tag.monthly')" value="monthly" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">{{ t('settings.security') }}</el-divider>
        <el-form-item :label="t('settings.maxLoginAttempts')">
          <el-input-number v-model="form.maxLoginAttempts" :min="1" :max="10" />
        </el-form-item>
        <el-form-item :label="t('settings.sessionTimeout')">
          <el-input-number v-model="form.sessionTimeout" :min="5" :max="120" :step="5" />
        </el-form-item>
        <el-form-item :label="t('settings.enableAuditLog')">
          <el-switch v-model="form.enableAuditLog" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">{{ t('button.saveSettings') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>