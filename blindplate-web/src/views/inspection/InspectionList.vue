<template>
  <div class="inspection-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>巡检计划</span>
          <el-button type="primary" @click="showCreateDialog">新增计划</el-button>
        </div>
      </template>
      <el-table :data="plans" v-loading="loading">
        <el-table-column prop="name" label="计划名称" />
        <el-table-column prop="frequency" label="频率" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const plans = ref<any[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/inspections')
    plans.value = res.data.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  // TODO: 实现创建对话框
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该计划？', '提示', { type: 'warning' })
  await axios.delete(`/api/v1/inspections/${id}`)
  ElMessage.success('删除成功')
  fetchData()
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
