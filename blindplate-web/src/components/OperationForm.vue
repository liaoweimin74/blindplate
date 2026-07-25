<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import request from '@/api/request'
import type { FormInstance, FormRules } from 'element-plus'
import type { BlindPlate } from '@/types'
import type { Location } from '@/types'

const { t } = useI18n()

const props = defineProps<{ visible: boolean; data?: any }>()
const emit = defineEmits<{ (e: 'update:visible', value: boolean): void; (e: 'submit', data: any): void }>()

const formRef = ref<FormInstance>()
const blindPlateOptions = ref<BlindPlate[]>([])
const locationOptions = ref<Location[]>([])
const form = ref({ orderNo: '', type: 'install', blindplateId: null as number | null, locationId: null as number | null, plannedDate: '', description: '' })

const rules: FormRules = {
  orderNo: [{ required: true, message: t('form.enterOrderNo'), trigger: 'blur' }],
  type: [{ required: true, message: t('form.selectType'), trigger: 'change' }],
  blindplateId: [{ required: true, message: t('form.selectBlindPlate'), trigger: 'change' }],
  locationId: [{ required: true, message: t('form.selectLocation'), trigger: 'change' }],
  plannedDate: [{ required: true, message: t('form.selectDate'), trigger: 'change' }]
}

async function fetchOptions() {
  try {
    const [bpRes, locRes]: any[] = await Promise.all([request.get('/blindplates'), request.get('/locations/tree')])
    blindPlateOptions.value = bpRes.data || []
    locationOptions.value = flattenTree(locRes.data || [])
  } catch (e) { console.error(e) }
}

function flattenTree(nodes: Location[], result: Location[] = []): Location[] {
  nodes.forEach(node => { result.push(node); if (node.children) flattenTree(node.children, result) }); return result
}

function getLabel(bp: BlindPlate) { return bp.code + ' - ' + bp.name }

watch(() => props.data, (val) => { if (val) form.value = { ...val } }, { immediate: true })
onMounted(fetchOptions)

function handleClose() {
  emit('update:visible', false)
  form.value = { orderNo: '', type: 'install', blindplateId: null, locationId: null, plannedDate: '', description: '' }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => { if (valid) { emit('submit', form.value); handleClose() } })
}
</script>

<template>
  <el-dialog :model-value="visible" :title="data ? t('dialog.editOrder') : t('dialog.createOrder')" width="600px" :close-on-click-modal="false" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelOrderNo')" prop="orderNo">
            <el-input v-model="form.orderNo" :placeholder="t('form.enterOrderNo')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelType')" prop="type">
            <el-select v-model="form.type" :placeholder="t('form.selectType')" style="width: 100%">
              <el-option :label="t('form.optionInstall')" value="install" />
              <el-option :label="t('form.optionRemove')" value="remove" />
              <el-option :label="t('form.optionInspect')" value="inspect" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item :label="t('form.labelBlindPlate')" prop="blindplateId">
        <el-select v-model="form.blindplateId" :placeholder="t('form.selectBlindPlate')" filterable style="width: 100%">
          <el-option v-for="bp in blindPlateOptions" :key="bp.id" :label="getLabel(bp)" :value="bp.id" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('form.labelLocation')" prop="locationId">
        <el-tree-select v-model="form.locationId" :data="locationOptions" :props="{ label: 'name', value: 'id', children: 'children' }" :placeholder="t('form.selectLocation')" filterable check-strictly style="width: 100%" />
      </el-form-item>
      <el-form-item :label="t('form.labelPlannedDate')" prop="plannedDate">
        <el-date-picker v-model="form.plannedDate" type="date" :placeholder="t('form.selectDate')" style="width: 100%" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item :label="t('form.labelDescription')" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" :placeholder="t('form.enterDescription')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">{{ t('button.cancel') }}</el-button>
      <el-button type="primary" @click="handleSubmit">{{ t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>