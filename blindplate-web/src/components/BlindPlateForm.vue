<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { FormInstance, FormRules } from 'element-plus'

const { t } = useI18n()

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
  code: [{ required: true, message: t('form.enterCode'), trigger: 'blur' }],
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  spec: [{ required: true, message: t('form.enterSpec'), trigger: 'blur' }],
  material: [{ required: true, message: t('form.enterMaterial'), trigger: 'blur' }],
  diameter: [{ required: true, message: t('form.enterDiameter'), trigger: 'blur' }],
  pressure: [{ required: true, message: t('form.enterPressure'), trigger: 'blur' }]
}

watch(() => props.data, (val) => {
  if (val) form.value = { ...val }
}, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  form.value = { code: '', name: '', spec: '', material: '', diameter: '', pressure: '', manufacturer: '', status: 'available', remark: '' }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) { emit('submit', form.value); handleClose() }
  })
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="data ? t('dialog.editBlindPlate') : t('dialog.addBlindPlate')"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelCode')" prop="code">
            <el-input v-model="form.code" :placeholder="t('form.enterCode')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelName')" prop="name">
            <el-input v-model="form.name" :placeholder="t('form.enterName')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelSpec')" prop="spec">
            <el-input v-model="form.spec" :placeholder="t('form.enterSpec')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelMaterial')" prop="material">
            <el-input v-model="form.material" :placeholder="t('form.enterMaterial')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelDiameter')" prop="diameter">
            <el-input v-model="form.diameter" :placeholder="t('form.enterDiameter')" type="number" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelPressure')" prop="pressure">
            <el-input v-model="form.pressure" :placeholder="t('form.enterPressure')" type="number" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelManufacturer')" prop="manufacturer">
            <el-input v-model="form.manufacturer" :placeholder="t('form.enterManufacturer')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelStatus')" prop="status">
            <el-select v-model="form.status" :placeholder="t('form.selectStatus')" style="width: 100%">
              <el-option :label="t('form.optionAvailable')" value="available" />
              <el-option :label="t('form.optionInstalled')" value="installed" />
              <el-option :label="t('form.optionRemoved')" value="removed" />
              <el-option :label="t('form.optionMaintenance')" value="maintenance" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item :label="t('form.labelRemark')" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" :placeholder="t('form.enterRemark')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">{{ t('button.cancel') }}</el-button>
      <el-button type="primary" @click="handleSubmit">{{ t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>