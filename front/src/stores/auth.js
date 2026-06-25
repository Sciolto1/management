import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  // State
  const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || null)
  const isLoggedIn = computed(() => currentUser.value !== null)

  // Actions
  async function login(username, password, role) {
    try {
      const res = await request.post('/auth/login', { username, password, role })
      currentUser.value = res.data
      localStorage.setItem('currentUser', JSON.stringify(res.data))
      return true
    } catch (error) {
      return false
    }
  }

  function logout() {
    currentUser.value = null
    localStorage.removeItem('currentUser')
  }

  return {
    currentUser,
    isLoggedIn,
    login,
    logout
  }
})
