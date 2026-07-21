<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { User } from '@/types'

const props = defineProps<{
  visible: boolean
  data?: User | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: any): void
}>()

const formRef = ref<FormInstance>()
const form = ref({
  username: '',
  name: '',
  phone: '',
  email: '',
  role: 'operator',
  status: 1
})

const rules: FormRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username must be 3-20 characters', trigger: 'blur' }
  ],
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }],
  role: [{ required: true, message: 'Please select role', trigger: 'change' }]
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = {
      username: val.username,
      name: val.name,
      phone: val.phone || '',
      email: val.email || '',
      role: val.role || 'operator',
      status: val.status
    }
  }
}, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  resetForm()
}

function resetForm() {
  form.value = {
    username: '',
    name: '',
    phone: '',
    email: '',
    role: 'operator',
    status: 1
  }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', form.value)
      handleClose()
    }
  })
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="data ? 'Edit User' : 'Add User'"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="Username" prop="username">
        <el-input v-model="form.username" placeholder="Enter username" :disabled="!!data" />
      </el-form-item>
      <el-form-item label="Name" prop="name">
        <el-input v-model="form.name" placeholder="Enter display name" />
      </el-form-item>
      <el-form-item label="Phone" prop="phone">
        <el-input v-model="form.phone" placeholder="Enter phone number" />
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="form.email" placeholder="Enter email address" />
      </el-form-item>
      <el-form-item label="Role" prop="role">
        <el-select v-model="form.role" placeholder="Select role" style="width: 100%">
          <el-option label="Admin" value="admin" />
          <el-option label="Manager" value="manager" />
          <el-option label="Operator" value="operator" />
        </el-select>
      </el-form-item>
      <el-form-item label="Status" prop="status">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="Active" inactive-text="Disabled" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Save</el-button>
    </template>
  </el-dialog>
</template>