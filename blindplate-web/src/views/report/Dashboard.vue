<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <span>数据概览</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-statistic title="盲板总数" :value="stats.totalBlindPlates || 0" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="工单总数" :value="stats.totalOrders || 0" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="巡检计划" :value="0" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const stats = ref<any>({})

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
