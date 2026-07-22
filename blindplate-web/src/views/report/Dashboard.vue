<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">{{ $t('page.dashboard') }}</h1>
      <p class="dashboard-subtitle">{{ $t('page.dashboardSubtitle') }}</p>
    </div>
    
    <el-row :gutter="24" class="stats-row">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #1a73e8, #4a90d9)">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalBlindPlates || 0 }}</div>
            <div class="stat-label">{{ $t('dashboard.totalBlindPlates') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #34a853, #81c995)">
            <el-icon size="32"><List /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            <div class="stat-label">{{ $t('dashboard.totalOrders') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fbbc04, #fdd663)">
            <el-icon size="32"><DataAnalysis /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalLocations || 0 }}</div>
            <div class="stat-label">{{ $t('dashboard.locations') }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ea4335, #f28b82)">
            <el-icon size="32"><Location /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalInspections || 0 }}</div>
            <div class="stat-label">{{ $t('dashboard.inspections') }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="content-row">
      <el-col :span="16">
        <el-card class="content-card">
          <template #header>
            <span class="card-title">{{ $t('dashboard.recentOperations') }}</span>
          </template>
          <div class="empty-state">
            <el-empty :description="$t('empty.noRecentOps')" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="content-card">
          <template #header>
            <span class="card-title">{{ $t('dashboard.quickActions') }}</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" class="action-btn">
              <el-icon><Document /></el-icon>
              {{ $t('dashboard.addBlindPlate') }}
            </el-button>
            <el-button type="success" class="action-btn">
              <el-icon><Location /></el-icon>
              {{ $t('dashboard.manageLocations') }}
            </el-button>
            <el-button type="warning" class="action-btn">
              <el-icon><List /></el-icon>
              {{ $t('dashboard.viewOrders') }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Document, List, DataAnalysis, Location } from '@element-plus/icons-vue'

const stats = ref<any>({
  totalBlindPlates: 0,
  totalOrders: 0,
  totalLocations: 0,
  totalInspections: 0
})

async function fetchStats() {
  try {
    const res: any = await axios.get('/api/v1/reports/statistics')
    stats.value = res.data.data
  } catch (e) {
    console.error('Failed to fetch statistics', e)
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard {
  padding: var(--brand-spacing-6);
}
.dashboard-header {
  margin-bottom: var(--brand-spacing-6);
}
.dashboard-title {
  font-size: var(--brand-font-size-3xl);
  font-weight: var(--brand-font-weight-bold);
  color: var(--brand-text-primary);
  margin: 0 0 var(--brand-spacing-2) 0;
}
.dashboard-subtitle {
  font-size: var(--brand-font-size-base);
  color: var(--brand-text-secondary);
  margin: 0;
}
.stats-row {
  margin-bottom: var(--brand-spacing-6);
}
.stat-card {
  display: flex;
  align-items: center;
  gap: var(--brand-spacing-4);
  padding: var(--brand-spacing-5);
  background: var(--brand-bg-white);
  border-radius: var(--brand-radius-lg);
  box-shadow: var(--brand-shadow-sm);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--brand-shadow-md);
}
.stat-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--brand-radius-lg);
  color: white;
}
.stat-content {
  flex: 1;
}
.stat-value {
  font-size: var(--brand-font-size-4xl);
  font-weight: var(--brand-font-weight-bold);
  color: var(--brand-text-primary);
  line-height: 1.2;
}
.stat-label {
  font-size: var(--brand-font-size-sm);
  color: var(--brand-text-secondary);
  margin-top: var(--brand-spacing-1);
}
.content-row {
  margin-bottom: var(--brand-spacing-6);
}
.content-card {
  height: 400px;
}
.card-title {
  font-size: var(--brand-font-size-lg);
  font-weight: var(--brand-font-weight-semibold);
  color: var(--brand-text-primary);
}
.empty-state {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--brand-spacing-3);
}
.action-btn {
  justify-content: flex-start;
  height: 48px;
  font-size: var(--brand-font-size-base);
}
</style>
