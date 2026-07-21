<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import type { FormInstance, FormRules } from 'element-plus'
import type { Location } from '@/types'

const props = defineProps<{
  visible: boolean
  data?: Location | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: any): void
}>()

const formRef = ref<FormInstance>()
const parentOptions = ref<Location[]>([])
const form = ref({
  name: '',
  code: '',
  type: 'area',
  parentId: null as number | null,
  description: ''
})

const rules: FormRules = {
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }],
  code: [{ required: true, message: 'Please enter code', trigger: 'blur' }],
  type: [{ required: true, message: 'Please select type', trigger: 'change' }]
}

async function fetchParentOptions() {
  try {
    const res: any = await axios.get('/api/v1/locations/tree')
    parentOptions.value = flattenTree(res.data.data)
  } catch (e) {
    console.error('Failed to fetch locations', e)
  }
}

function flattenTree(nodes: Location[], result: Location[] = []): Location[] {
  nodes.forEach(node => {
    result.push(node)
    if (node.children) {
      flattenTree(node.children, result)
    }
  })
  return result
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = {
      name: val.name,
      code: val.code,
      type: val.type,
      parentId: val.parentId,
      description: val.description || ''
    }
  }
}, { immediate: true })

onMounted(fetchParentOptions)

function handleClose() {
  emit('update:visible', false)
  resetForm()
}

function resetForm() {
  form.value = {
    name: '',
    code: '',
    type: 'area',
    parentId: null,
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
    :title="data ? 'Edit Location' : 'Add Location'"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="Code" prop="code">
        <el-input v-model="form.code" placeholder="Enter location code" />
      </el-form-item>
      <el-form-item label="Name" prop="name">
        <el-input v-model="form.name" placeholder="Enter location name" />
      </el-form-item>
      <el-form-item label="Type" prop="type">
        <el-select v-model="form.type" placeholder="Select type" style="width: 100%">
          <el-option label="Area" value="area" />
          <el-option label="Building" value="building" />
          <el-option label="Floor" value="floor" />
          <el-option label="Room" value="room" />
          <el-option label="Equipment" value="equipment" />
        </el-select>
      </el-form-item>
      <el-form-item label="Parent" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="parentOptions"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="Select parent location"
          clearable
          check-strictly
          style="width: 100%"
        />
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
