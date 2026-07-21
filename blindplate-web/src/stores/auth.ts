import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  username: string
  name: string
}

function getStoredUserInfo(): UserInfo | null {
  const stored = localStorage.getItem('userInfo')
  return stored ? JSON.parse(stored) : null
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(getStoredUserInfo())

  function setToken(newToken: string, user?: UserInfo) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    if (user) {
      userInfo.value = user
      localStorage.setItem('userInfo', JSON.stringify(user))
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, setToken, logout }
})
