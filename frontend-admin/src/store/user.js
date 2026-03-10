import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getInfo as getInfoApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(loginForm) {
    const data = await loginApi(loginForm)
    token.value = data.token
    userInfo.value = data
    localStorage.setItem('token', data.token)
    return data
  }

  async function getInfo() {
    const data = await getInfoApi()
    userInfo.value = data
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getInfo, logout }
})
