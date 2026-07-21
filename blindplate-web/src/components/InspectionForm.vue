<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps<{
  visible: boolean
  data?: any
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: any): void
}>()

const formRef = ref<FormInstance>()
const form = ref({
  name: '',
  frequency: 'weekly',
  status: 'active',
  description: ''
})

const rules: FormRules = {
  name: [{ required: true, message: 'Please enter plan name', trigger: 'blur' }],
  frequency: [{ required: true, message: 'Please select frequency', trigger: 'change' }]
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  resetForm()
}

function resetForm() {
  form.value = {
    name: '',
    frequency: 'weekly',
    status: 'active',
    description: ''
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
    :title="data ? 'Edit Inspection Plan' : 'Create Inspection Plan'"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="Plan Name" prop="name">
        <el-input v-model="form.name" placeholder="Enter plan name" />
      </el-form-item>
      <el-form-item label="Frequency" prop="frequency">
        <el-select v-model="form.frequency" placeholder="Select frequency" style="width: 100%">
          <el-option label="Daily" value="daily" />
          <el-option label="Weekly" value="weekly" />
          <el-option label="Monthly" value="monthly" />
        </el-select>
      </el-form-item>
      <el-form-item label="Status" prop="status">
        <el-select v-model="form.status" placeholder="Select status" style="width: 100%">
          <el-option label="Active" value="active" />
          <el-option label="Paused" value="paused" />
        </el-select>
      </el-form-item>
      <el-form-item label="Description" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="Enter description (optional)" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Save</el-button>
    </template>
  </el-dialog>
</template>
