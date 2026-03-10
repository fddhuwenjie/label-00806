import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 加载状态管理
let loadingInstance = null
let loadingCount = 0

const showLoading = () => {
  if (loadingCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.1)'
    })
  }
  loadingCount++
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    loadingInstance?.close()
    loadingInstance = null
  }
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/admin',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 添加请求ID用于追踪
    config.headers['X-Request-Id'] = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    
    // 显示加载状态（可通过 config.showLoading = false 禁用）
    if (config.showLoading !== false) {
      showLoading()
    }
    
    return config
  },
  error => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    hideLoading()
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    }
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  error => {
    hideLoading()
    
    // 网络错误
    if (!error.response) {
      ElMessage.error('网络连接失败，请检查网络设置')
      return Promise.reject(error)
    }
    
    const { status, data } = error.response
    
    switch (status) {
      case 401:
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        break
      case 403:
        ElMessage.error('没有权限执行此操作')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 422:
        // 参数验证错误
        ElMessage.error(data?.message || '参数验证失败')
        break
      case 429:
        ElMessage.error('请求过于频繁，请稍后再试')
        break
      case 500:
        ElMessage.error('服务器内部错误，请稍后再试')
        break
      case 502:
      case 503:
      case 504:
        ElMessage.error('服务暂时不可用，请稍后再试')
        break
      default:
        ElMessage.error(data?.message || error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

// 带重试机制的请求
export const requestWithRetry = async (config, retries = 3, delay = 1000) => {
  for (let i = 0; i < retries; i++) {
    try {
      return await request(config)
    } catch (error) {
      if (i === retries - 1) throw error
      if (error.response?.status >= 500) {
        await new Promise(resolve => setTimeout(resolve, delay * (i + 1)))
      } else {
        throw error
      }
    }
  }
}

export default request
