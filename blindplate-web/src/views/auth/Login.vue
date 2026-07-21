<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">Blind Plate Management</h1>
        <p class="login-subtitle">Industrial Safety System</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="Username"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="Password"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            Login
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { login } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()

const form = ref({ username: '', password: '' })
const loading = ref(false)

const rules: FormRules = {
  username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res: any = await login(form.value)
      authStore.setToken(res.data.token, {
        username: res.data.username,
        name: res.data.name
      })
      router.push('/dashboard')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, var(--brand-color-primary) 0%, var(--brand-color-primary-dark) 100%);
}
.login-card {
  width: 420px;
  padding: var(--brand-spacing-8);
  background: var(--brand-bg-white);
  border-radius: var(--brand-radius-xl);
  box-shadow: var(--brand-shadow-lg);
}
.login-header {
  text-align: center;
  margin-bottom: var(--brand-spacing-8);
}
.login-title {
  font-size: var(--brand-font-size-3xl);
  font-weight: var(--brand-font-weight-bold);
  color: var(--brand-text-primary);
  margin: 0 0 var(--brand-spacing-2) 0;
}
.login-subtitle {
  font-size: var(--brand-font-size-base);
  color: var(--brand-text-secondary);
  margin: 0;
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: var(--brand-font-size-base);
  font-weight: var(--brand-font-weight-medium);
}
:deep(.el-input__wrapper) {
  height: 44px;
}
</style>
