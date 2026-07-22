<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { getUsers, deleteUser, resetPassword } from '@/api/user'
import type { User } from '@/types'
import UserForm from '@/components/UserForm.vue'

const { t } = useI18n()

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
  ElMessage.success(t('message.' + (editData.value ? 'updated' : 'created')))
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm(t('confirm.deleteUser'), t('confirm.deleteTitle'), { type: 'warning' })
  await deleteUser(id)
  ElMessage.success(t('message.deleted'))
  fetchData()
}

async function handleResetPassword(id: number) {
  await ElMessageBox.confirm(t('confirm.resetPwContent'), t('confirm.deleteTitle'), { type: 'warning' })
  await resetPassword(id)
  ElMessage.success(t('message.passwordReset'))
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ t('page.users') }}</h1>
      <p class="page-subtitle">{{ t('page.usersSubtitle') }}</p>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchQuery"
              :placeholder="t('placeholder.searchByUser')"
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
              {{ t('button.addUser') }}
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" v-loading="loading" class="data-table">
        <el-table-column prop="username" :label="t('table.username')" width="150" />
        <el-table-column prop="name" :label="t('table.name')" width="150" />
        <el-table-column prop="phone" :label="t('table.phone')" width="150" />
        <el-table-column prop="status" :label="t('table.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ row.status === 1 ? t('tag.active') : t('tag.disabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('table.actions')" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              {{ t('button.edit') }}
            </el-button>
            <el-button size="small" text type="warning" @click="handleResetPassword(row.id)">
              <el-icon><Key /></el-icon>
              {{ t('button.resetPw') }}
            </el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>
              {{ t('button.delete') }}
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
.content-card {
  background: var(--brand-bg-white);
  border-radius: var(--brand-radius-lg);
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: nowrap;
  gap: var(--brand-spacing-4);
}
.header-left {
  display: flex;
  gap: var(--brand-spacing-3);
}
.header-right {
  display: flex;
  gap: var(--brand-spacing-3);
  flex-shrink: 0;
}
.search-input {
  width: 280px;
}
.data-table {
  width: 100%;
}
:deep(.el-table__header th) {
  background-color: var(--brand-bg-page);
  color: var(--brand-text-primary);
  font-weight: var(--brand-font-weight-medium);
}
:deep(.el-table__row:hover td) {
  background-color: var(--brand-bg-hover);
}
</style>