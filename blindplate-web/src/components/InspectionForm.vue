<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { FormInstance, FormRules } from 'element-plus'

const { t } = useI18n()

const props = defineProps<{ visible: boolean; data?: any }>()
const emit = defineEmits<{ (e: 'update:visible', value: boolean): void; (e: 'submit', data: any): void }>()

const formRef = ref<FormInstance>()
const form = ref({ name: '', frequency: 'weekly', status: 'active', description: '' })

const rules: FormRules = {
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  frequency: [{ required: true, message: t('form.selectFrequency'), trigger: 'change' }]
}

watch(() => props.data, (val) => { if (val) form.value = { ...val } }, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  form.value = { name: '', frequency: 'weekly', status: 'active', description: '' }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => { if (valid) { emit('submit', form.value); handleClose() } })
}
</script>

<template>
  <el-dialog :model-value="visible" :title="data ? t('dialog.editInspection') : t('dialog.createInspection')" width="500px" :close-on-click-modal="false" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('form.labelPlanName')" prop="name">
        <el-input v-model="form.name" :placeholder="t('form.enterName')" />
      </el-form-item>
      <el-form-item :label="t('form.labelFrequency')" prop="frequency">
        <el-select v-model="form.frequency" :placeholder="t('form.selectFrequency')" style="width: 100%">
          <el-option :label="t('form.optionDaily')" value="daily" />
          <el-option :label="t('form.optionWeekly')" value="weekly" />
          <el-option :label="t('form.optionMonthly')" value="monthly" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('form.labelStatus')" prop="status">
        <el-select v-model="form.status" :placeholder="t('form.selectStatus')" style="width: 100%">
          <el-option :label="t('form.optionActive')" value="active" />
          <el-option :label="t('form.optionPaused')" value="paused" />
        </el-select>
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