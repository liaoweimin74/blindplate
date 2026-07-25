<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import request from '@/api/request'
import type { FormInstance, FormRules } from 'element-plus'
import type { Location, IsolationPointDetail } from '@/types'

const { t } = useI18n()

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
  type: 'FACTORY',
  parentId: null as number | null,
  description: '',
  detail: {
    medium: '',
    hazardLevel: '',
    isolationType: '',
    pressure: null as number | null,
    temperature: null as number | null
  } as IsolationPointDetail
})

const isIsolationPoint = computed(() => form.value.type === 'ISOLATION_POINT')
const isFactory = computed(() => form.value.type === 'FACTORY')

const rules: FormRules = {
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  code: [{ required: true, message: t('form.enterCode'), trigger: 'blur' }],
  type: [{ required: true, message: t('form.selectType'), trigger: 'change' }]
}

const typeOptions = [
  { label: t('form.optionFactory'), value: 'FACTORY' },
  { label: t('form.optionEquipment'), value: 'EQUIPMENT' },
  { label: t('form.optionUnit'), value: 'UNIT' },
  { label: t('form.optionIsolationPoint'), value: 'ISOLATION_POINT' }
]

const hazardLevelOptions = [
  { label: 'A', value: 'A' },
  { label: 'B', value: 'B' },
  { label: 'C', value: 'C' },
  { label: 'D', value: 'D' }
]

const isolationTypeOptions = [
  { label: t('form.optionFlange'), value: '法兰' },
  { label: t('form.optionValve'), value: '阀门' },
  { label: t('form.optionBlindPlate'), value: '盲板' },
  { label: t('form.optionFigure8'), value: '8字盲板' }
]

async function fetchParentOptions() {
  try {
    const res: any = await request.get('/locations/tree')
    parentOptions.value = flattenTree(res.data)
  } catch (e) { console.error(e) }
}

function flattenTree(nodes: Location[], result: Location[] = []): Location[] {
  nodes.forEach(node => { result.push(node); if (node.children) flattenTree(node.children, result) })
  return result
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = {
      name: val.name,
      code: val.code,
      type: val.type,
      parentId: val.parentId,
      description: val.description || '',
      detail: val.isolationPointDetail
        ? { ...val.isolationPointDetail }
        : { medium: '', hazardLevel: '', isolationType: '', pressure: null, temperature: null }
    }
  }
}, { immediate: true })

watch(() => form.value.type, (newType) => {
  if (newType === 'FACTORY') {
    form.value.parentId = null
  }
})

onMounted(fetchParentOptions)

function handleClose() {
  emit('update:visible', false)
  form.value = {
    name: '', code: '', type: 'FACTORY', parentId: null, description: '',
    detail: { medium: '', hazardLevel: '', isolationType: '', pressure: null, temperature: null }
  }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      const submitData: any = { ...form.value }
      if (!isIsolationPoint.value) delete submitData.detail
      emit('submit', submitData)
      handleClose()
    }
  })
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="data ? t('dialog.editLocation') : t('dialog.addLocation')"
    width="560px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('form.labelCode')" prop="code">
        <el-input v-model="form.code" :placeholder="t('form.enterCode')" />
      </el-form-item>
      <el-form-item :label="t('form.labelName')" prop="name">
        <el-input v-model="form.name" :placeholder="t('form.enterName')" />
      </el-form-item>
      <el-form-item :label="t('form.labelType')" prop="type">
        <el-select v-model="form.type" :placeholder="t('form.selectType')" style="width: 100%">
          <el-option v-for="opt in typeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('form.labelParent')" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="parentOptions"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          :placeholder="t('form.selectParent')"
          :disabled="isFactory"
          clearable
          check-strictly
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item :label="t('form.labelDescription')">
        <el-input v-model="form.description" type="textarea" :placeholder="t('form.enterDescription')" />
      </el-form-item>

      <template v-if="isIsolationPoint">
        <el-divider>{{ t('form.isolationPointDetail') }}</el-divider>
        <el-form-item :label="t('form.labelMedium')">
          <el-input v-model="form.detail.medium" :placeholder="t('form.enterMedium')" />
        </el-form-item>
        <el-form-item :label="t('form.labelHazardLevel')">
          <el-select v-model="form.detail.hazardLevel" :placeholder="t('form.selectHazardLevel')" style="width: 100%">
            <el-option v-for="opt in hazardLevelOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('form.labelIsolationType')">
          <el-select v-model="form.detail.isolationType" :placeholder="t('form.selectIsolationType')" style="width: 100%">
            <el-option v-for="opt in isolationTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('form.labelPressure')">
          <el-input-number v-model="form.detail.pressure" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('form.labelTemperature')">
          <el-input-number v-model="form.detail.temperature" :precision="1" style="width: 100%" />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">{{ t('button.cancel') }}</el-button>
      <el-button type="primary" @click="handleSubmit">{{ t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>
