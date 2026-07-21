<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'
import type { FormInstance, FormRules } from 'element-plus'
import type { BlindPlate } from '@/types'
import type { Location } from '@/types'

const props = defineProps<{
  visible: boolean
  data?: any
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: any): void
}>()

const formRef = ref<FormInstance>()
const blindPlateOptions = ref<BlindPlate[]>([])
const locationOptions = ref<Location[]>([])

const form = ref({
  orderNo: '',
  type: 'install',
  blindplateId: null as number | null,
  locationId: null as number | null,
  plannedDate: '',
  description: ''
})

const rules: FormRules = {
  orderNo: [{ required: true, message: 'Please enter order number', trigger: 'blur' }],
  type: [{ required: true, message: 'Please select type', trigger: 'change' }],
  blindplateId: [{ required: true, message: 'Please select blind plate', trigger: 'change' }],
  locationId: [{ required: true, message: 'Please select location', trigger: 'change' }],
  plannedDate: [{ required: true, message: 'Please select planned date', trigger: 'change' }]
}

async function fetchOptions() {
  try {
    const [bpRes, locRes]: any[] = await Promise.all([
      axios.get('/api/v1/blindplates'),
      axios.get('/api/v1/locations/tree')
    ])
    blindPlateOptions.value = bpRes.data.data || []
    locationOptions.value = flattenTree(locRes.data.data || [])
  } catch (e) {
    console.error('Failed to fetch options', e)
  }
}

function flattenTree(nodes: Location[], result: Location[] = []): Location[] {
  nodes.forEach(node => {
    result.push(node)
    if (node.children) flattenTree(node.children, result)
  })
  return result
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })

onMounted(fetchOptions)

function handleClose() {
  emit('update:visible', false)
  resetForm()
}

function resetForm() {
  form.value = {
    orderNo: '',
    type: 'install',
    blindplateId: null,
    locationId: null,
    plannedDate: '',
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

function getLabel(bp: BlindPlate) {
  return bp.code + ' - ' + bp.name
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="data ? 'Edit Operation Order' : 'Create Operation Order'"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Order No." prop="orderNo">
            <el-input v-model="form.orderNo" placeholder="Enter order number" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Type" prop="type">
            <el-select v-model="form.type" placeholder="Select type" style="width: 100%">
              <el-option label="Install" value="install" />
              <el-option label="Remove" value="remove" />
              <el-option label="Inspect" value="inspect" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="Blind Plate" prop="blindplateId">
        <el-select v-model="form.blindplateId" placeholder="Select blind plate" filterable style="width: 100%">
          <el-option
            v-for="bp in blindPlateOptions"
            :key="bp.id"
            :label="getLabel(bp)"
            :value="bp.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Location" prop="locationId">
        <el-tree-select
          v-model="form.locationId"
          :data="locationOptions"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="Select location"
          filterable
          check-strictly
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="Planned Date" prop="plannedDate">
        <el-date-picker
          v-model="form.plannedDate"
          type="date"
          placeholder="Select date"
          style="width: 100%"
          value-format="YYYY-MM-DD"
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