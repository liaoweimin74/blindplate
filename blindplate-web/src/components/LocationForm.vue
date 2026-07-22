<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import type { FormInstance, FormRules } from 'element-plus'
import type { Location } from '@/types'

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
const form = ref({ name: '', code: '', type: 'area', parentId: null as number | null, description: '' })

const rules: FormRules = {
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  code: [{ required: true, message: t('form.enterCode'), trigger: 'blur' }],
  type: [{ required: true, message: t('form.selectType'), trigger: 'change' }]
}

async function fetchParentOptions() {
  try {
    const res: any = await axios.get('/api/v1/locations/tree')
    parentOptions.value = flattenTree(res.data.data)
  } catch (e) { console.error(e) }
}

function flattenTree(nodes: Location[], result: Location[] = []): Location[] {
  nodes.forEach(node => { result.push(node); if (node.children) flattenTree(node.children, result) })
  return result
}

watch(() => props.data, (val) => {
  if (val) form.value = { name: val.name, code: val.code, type: val.type, parentId: val.parentId, description: val.description || '' }
}, { immediate: true })

onMounted(fetchParentOptions)

function handleClose() {
  emit('update:visible', false)
  form.value = { name: '', code: '', type: 'area', parentId: null, description: '' }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => { if (valid) { emit('submit', form.value); handleClose() } })
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="data ? t('dialog.editLocation') : t('dialog.addLocation')"
    width="500px"
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
          <el-option :label="t('form.optionArea')" value="area" />
          <el-option :label="t('form.optionBuilding')" value="building" />
          <el-option :label="t('form.optionFloor')" value="floor" />
          <el-option :label="t('form.optionRoom')" value="room" />
          <el-option :label="t('form.optionEquipment')" value="equipment" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('form.labelParent')" prop="parentId">
        <el-tree-select v-model="form.parentId" :data="parentOptions" :props="{ label: 'name', value: 'id', children: 'children' }" :placeholder="t('form.selectParent')" clearable check-strictly style="width: 100%" />
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