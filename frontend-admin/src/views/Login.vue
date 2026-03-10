<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-brand">
        <div class="brand-icon">🍜</div>
        <h1>美味外卖</h1>
        <p>高效管理您的外卖业务<br>订单、商家、菜品一站式管理</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-card">
        <div class="login-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
            </svg>
          </div>
          <span class="logo-text">管理后台</span>
        </div>
        <p class="login-title">登录您的账号以继续</p>
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input
              ref="usernameRef"
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              :readonly="readonlyFlag"
              @focus="readonlyFlag = false"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              ref="passwordRef"
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              :readonly="readonlyFlag"
              @focus="readonlyFlag = false"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleLogin">登 录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const usernameRef = ref()
const passwordRef = ref()
const loading = ref(false)
const readonlyFlag = ref(true)

const form = reactive({
  username: '',
  password: ''
})

onMounted(() => {
  setTimeout(() => {
    form.username = ''
    form.password = ''
    const inputs = document.querySelectorAll('.login-card input')
    inputs.forEach(input => { input.value = '' })
  }, 300)
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>
