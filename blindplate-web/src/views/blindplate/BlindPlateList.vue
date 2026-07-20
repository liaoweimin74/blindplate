<template>
  <div class="blindplate-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盲板列表</span>
          <el-button type="primary" @click="showCreateDialog">新增盲板</el-button>
        </div>
      </template>
      <el-table :data="blindPlates" v-loading="loading">
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="material" label="材质" />
        <el-table-column prop="status" label="状态" />
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
import { getBlindPlates, deleteBlindPlate } from '@/api/blindplate'
import type { BlindPlate } from '@/types'

const blindPlates = ref<BlindPlate[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getBlindPlates()
    blindPlates.value = res.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  // TODO: 实现创建对话框
}

function showEditDialog(row: BlindPlate) {
  // TODO: 实现编辑对话框
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该盲板？', '提示', { type: 'warning' })
  await deleteBlindPlate(id)
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
