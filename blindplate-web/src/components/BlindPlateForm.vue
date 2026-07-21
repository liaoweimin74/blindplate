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
  code: '',
  name: '',
  spec: '',
  material: '',
  diameter: '',
  pressure: '',
  manufacturer: '',
  status: 'available',
  remark: ''
})

const rules: FormRules = {
  code: [{ required: true, message: 'Please enter code', trigger: 'blur' }],
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }],
  spec: [{ required: true, message: 'Please enter specification', trigger: 'blur' }],
  material: [{ required: true, message: 'Please enter material', trigger: 'blur' }],
  diameter: [{ required: true, message: 'Please enter diameter', trigger: 'blur' }],
  pressure: [{ required: true, message: 'Please enter pressure', trigger: 'blur' }]
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
    code: '',
    name: '',
    spec: '',
    material: '',
    diameter: '',
    pressure: '',
    manufacturer: '',
    status: 'available',
    remark: ''
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
    :title="data ? 'Edit Blind Plate' : 'Add Blind Plate'"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Code" prop="code">
            <el-input v-model="form.code" placeholder="Enter code" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Name" prop="name">
            <el-input v-model="form.name" placeholder="Enter name" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Specification" prop="spec">
            <el-input v-model="form.spec" placeholder="e.g. DN100" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Material" prop="material">
            <el-input v-model="form.material" placeholder="e.g. Carbon Steel" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Diameter (mm)" prop="diameter">
            <el-input v-model="form.diameter" placeholder="Enter diameter" type="number" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Pressure (MPa)" prop="pressure">
            <el-input v-model="form.pressure" placeholder="Enter pressure" type="number" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Manufacturer" prop="manufacturer">
            <el-input v-model="form.manufacturer" placeholder="Enter manufacturer" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Status" prop="status">
            <el-select v-model="form.status" placeholder="Select status" style="width: 100%">
              <el-option label="Available" value="available" />
              <el-option label="Installed" value="installed" />
              <el-option label="Removed" value="removed" />
              <el-option label="Maintenance" value="maintenance" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="Remark" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="Enter remark (optional)" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Save</el-button>
    </template>
  </el-dialog>
</template>
