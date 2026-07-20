<template>
  <div class="operation-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>作业工单</span>
          <el-button type="primary" @click="showCreateDialog">新建工单</el-button>
        </div>
      </template>
      <el-table :data="orders" v-loading="loading">
        <el-table-column prop="orderNo" label="工单号" width="150" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="plannedDate" label="计划日期" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
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
import type { OperationOrder } from '@/types'

const orders = ref<OperationOrder[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await axios.get('/api/v1/operations')
    orders.value = res.data.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  // TODO: 实现创建对话框
}

function showEditDialog(row: OperationOrder) {
  // TODO: 实现编辑对话框
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该工单？', '提示', { type: 'warning' })
  await axios.delete(`/api/v1/operations/${id}`)
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
