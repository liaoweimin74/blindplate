<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

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
    // TODO: Save settings
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('Settings saved successfully')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">System Settings</h1>
      <p class="page-subtitle">Configure system parameters</p>
    </div>

    <el-card class="content-card">
      <el-form :model="form" label-width="180px" style="max-width: 600px">
        <el-divider content-position="left">General</el-divider>
        <el-form-item label="Company Name">
          <el-input v-model="form.companyName" />
        </el-form-item>
        <el-form-item label="Notification Email">
          <el-input v-model="form.notificationEmail" />
        </el-form-item>

        <el-divider content-position="left">Backup</el-divider>
        <el-form-item label="Auto Backup">
          <el-switch v-model="form.autoBackup" />
        </el-form-item>
        <el-form-item label="Backup Frequency" v-if="form.autoBackup">
          <el-select v-model="form.backupFrequency" style="width: 100%">
            <el-option label="Daily" value="daily" />
            <el-option label="Weekly" value="weekly" />
            <el-option label="Monthly" value="monthly" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">Security</el-divider>
        <el-form-item label="Max Login Attempts">
          <el-input-number v-model="form.maxLoginAttempts" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="Session Timeout (min)">
          <el-input-number v-model="form.sessionTimeout" :min="5" :max="120" :step="5" />
        </el-form-item>
        <el-form-item label="Enable Audit Log">
          <el-switch v-model="form.enableAuditLog" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">Save Settings</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
@import '@/styles/global.scss';
</style>