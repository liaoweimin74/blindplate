<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { getUsers, deleteUser, resetPassword } from '@/api/user'
import type { User } from '@/types'
import UserForm from '@/components/UserForm.vue'

const users = ref<User[]>([])
const loading = ref(false)
const searchQuery = ref('')
const dialogVisible = ref(false)
const editData = ref<User | null>(null)

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(u =>
    u.username.toLowerCase().includes(query) ||
    u.name.toLowerCase().includes(query)
  )
})

function getStatusType(status: number) {
  switch (status) {
    case 1: return 'success'
    case 0: return 'danger'
    default: return 'info'
  }
}

function getStatusText(status: number) {
  return status === 1 ? 'Active' : 'Disabled'
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await getUsers()
    users.value = res.data
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  editData.value = null
  dialogVisible.value = true
}

function showEditDialog(row: User) {
  editData.value = { ...row }
  dialogVisible.value = true
}

async function handleFormSubmit(_data: any) {
  ElMessage.success(editData.value ? 'Updated successfully' : 'Created successfully')
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Are you sure you want to delete this user?', 'Confirm Delete', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('Deleted successfully')
  fetchData()
}

async function handleResetPassword(id: number) {
  await ElMessageBox.confirm('Reset password to default?', 'Confirm', { type: 'warning' })
  await resetPassword(id)
  ElMessage.success('Password reset successfully')
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Users</h1>
      <p class="page-subtitle">User account management</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchQuery"
              placeholder="Search by username or name"
              clearable
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              Add User
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" v-loading="loading" class="data-table">
        <el-table-column prop="username" label="Username" width="150" />
        <el-table-column prop="name" label="Name" width="150" />
        <el-table-column prop="phone" label="Phone" width="150" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              Edit
            </el-button>
            <el-button size="small" text type="warning" @click="handleResetPassword(row.id)">
              <el-icon><Key /></el-icon>
              Reset PW
            </el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <UserForm
      v-model:visible="dialogVisible"
      :data="editData"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<style scoped>
@import '@/styles/global.scss';
</style>