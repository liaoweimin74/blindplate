<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { FormInstance, FormRules } from 'element-plus'
import type { User } from '@/types'

const { t } = useI18n()

const props = defineProps<{ visible: boolean; data?: User | null }>()
const emit = defineEmits<{ (e: 'update:visible', value: boolean): void; (e: 'submit', data: any): void }>()

const formRef = ref<FormInstance>()
const form = ref({ username: '', name: '', phone: '', email: '', role: 'operator', status: 1 })

const rules: FormRules = {
  username: [{ required: true, message: t('form.enterUsername'), trigger: 'blur' }, { min: 3, max: 20, message: '3-20 characters', trigger: 'blur' }],
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  role: [{ required: true, message: t('form.selectRole'), trigger: 'change' }]
}

watch(() => props.data, (val) => {
  if (val) form.value = { username: val.username, name: val.name, phone: val.phone || '', email: val.email || '', role: val.role || 'operator', status: val.status }
}, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  form.value = { username: '', name: '', phone: '', email: '', role: 'operator', status: 1 }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => { if (valid) { emit('submit', form.value); handleClose() } })
}
</script>

<template>
  <el-dialog :model-value="visible" :title="data ? t('dialog.editUser') : t('dialog.addUser')" width="500px" :close-on-click-modal="false" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('form.labelUsername')" prop="username">
        <el-input v-model="form.username" :placeholder="t('form.enterUsername')" :disabled="!!data" />
      </el-form-item>
      <el-form-item :label="t('form.labelName')" prop="name">
        <el-input v-model="form.name" :placeholder="t('form.enterName')" />
      </el-form-item>
      <el-form-item :label="t('form.labelPhone')" prop="phone">
        <el-input v-model="form.phone" :placeholder="t('form.enterPhone')" />
      </el-form-item>
      <el-form-item :label="t('form.labelEmail')" prop="email">
        <el-input v-model="form.email" :placeholder="t('form.enterEmail')" />
      </el-form-item>
      <el-form-item :label="t('form.labelRole')" prop="role">
        <el-select v-model="form.role" :placeholder="t('form.selectRole')" style="width: 100%">
          <el-option :label="t('form.optionAdmin')" value="admin" />
          <el-option :label="t('form.optionManager')" value="manager" />
          <el-option :label="t('form.optionOperator')" value="operator" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('form.labelStatus')" prop="status">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" :active-text="t('form.optionActive')" :inactive-text="t('form.optionPaused')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">{{ t('button.cancel') }}</el-button>
      <el-button type="primary" @click="handleSubmit">{{ t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>