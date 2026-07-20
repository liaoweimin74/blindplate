<template>
  <div class="location-tree">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>位置管理</span>
          <el-button type="primary" @click="showCreateDialog">新增位置</el-button>
        </div>
      </template>
      <el-tree :data="treeData" :props="{ label: 'name', children: 'children' }" v-loading="loading" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import type { Location } from '@/types'

const treeData = ref<Location[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/locations/tree')
    treeData.value = res.data.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  // TODO: 实现创建对话框
}

onMounted(fetchData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
