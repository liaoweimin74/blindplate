<script setup lang="ts">
import { ref, watch, computed } from 'vue'
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
const isEdit = computed(() => !!props.data)
const form = ref({
  code: '',
  name: '',
  spec: '',
  modelType: '',
  material: '',
  diameter: '' as string | number,
  pressure: '' as string | number,
  thickness: '' as string | number,
  manufacturer: '',
  factoryCode: '',
  purchaseDate: '',
  status: 'in_stock',
  lifecycleStatus: 'normal',
  nextInspectionDate: '',
  remark: '',
  qrCode: '',
  rfidTag: ''
})

const rules: FormRules = {
  code: [{ required: true, message: t('form.enterCode'), trigger: 'blur' }],
  name: [{ required: true, message: t('form.enterName'), trigger: 'blur' }],
  spec: [{ required: true, message: t('form.enterSpec'), trigger: 'blur' }],
  material: [{ required: true, message: t('form.enterMaterial'), trigger: 'blur' }]
}

const statusOptions = [
  { label: '在库', value: 'in_stock' },
  { label: '在用', value: 'in_use' },
  { label: '送检中', value: 'in_inspection' },
  { label: '已报废', value: 'scrapped' },
  { label: '遗失', value: 'lost' }
]

const modelTypeOptions = [
  { label: '8字盲板', value: '8字盲板' },
  { label: '插板', value: '插板' },
  { label: '垫环', value: '垫环' },
  { label: '盲法兰', value: '盲法兰' },
  { label: '其他', value: '其他' }
]

watch(() => props.data, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })

function handleClose() {
  emit('update:visible', false)
  form.value = { code: '', name: '', spec: '', modelType: '', material: '', diameter: '', pressure: '', thickness: '', manufacturer: '', factoryCode: '', purchaseDate: '', status: 'in_stock', lifecycleStatus: 'normal', nextInspectionDate: '', remark: '', qrCode: '', rfidTag: '' }
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
    width="720px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelCode')" prop="code">
            <el-input v-model="form.code" :placeholder="t('form.enterCode')" :disabled="isEdit" />
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
          <el-form-item label="型号" prop="modelType">
            <el-select v-model="form.modelType" placeholder="请选择型号" style="width: 100%">
              <el-option v-for="opt in modelTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelSpec')" prop="spec">
            <el-input v-model="form.spec" :placeholder="t('form.enterSpec')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelMaterial')" prop="material">
            <el-input v-model="form.material" :placeholder="t('form.enterMaterial')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="厚度(mm)">
            <el-input-number v-model="form.thickness" :precision="2" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelDiameter')" prop="diameter">
            <el-input-number v-model="form.diameter" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('form.labelPressure')" prop="pressure">
            <el-input-number v-model="form.pressure" :precision="2" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelManufacturer')">
            <el-input v-model="form.manufacturer" :placeholder="t('form.enterManufacturer')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出厂编号">
            <el-input v-model="form.factoryCode" placeholder="请输入出厂编号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="采购日期">
            <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下次检验日期">
            <el-date-picker v-model="form.nextInspectionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('form.labelStatus')" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item v-if="isEdit && form.qrCode" label="二维码">
        <el-input v-model="form.qrCode" readonly />
      </el-form-item>
      <el-form-item v-if="isEdit && form.rfidTag" label="RFID标签">
        <el-input v-model="form.rfidTag" readonly />
      </el-form-item>
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