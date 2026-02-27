import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import Cookies from 'js-cookie'
import { getUserInfo } from '@/api/user'
import { login, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(Cookies.get('token') || '')
  const userInfo = ref({})
  const rootFileId = ref('')
  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken || ''
    if (token.value) {
      Cookies.set('token', token.value, { expires: 7 })
    } else {
      Cookies.remove('token')
    }
  }

  const clearToken = () => {
    setToken('')
    userInfo.value = {}
    rootFileId.value = ''
  }

  const loginAction = async (loginData) => {
    try {
      const response = await login(loginData)
      if (!response?.success) {
        return { success: false, message: response?.message || '登录失败' }
      }

      const tokenValue = response.data
      if (!tokenValue || typeof tokenValue !== 'string') {
        return { success: false, message: '登录返回token无效' }
      }

      setToken(tokenValue)
      await nextTick()
      await getUserInfoAction()
      return { success: true }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  const registerAction = async (registerData) => {
    try {
      const response = await register(registerData)
      if (response?.success) {
        return { success: true, data: response.data }
      }
      return { success: false, message: response?.message || '注册失败' }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  }

  const getUserInfoAction = async () => {
    try {
      const response = await getUserInfo()
      if (!response?.success) {
        return
      }

      userInfo.value = response.data || {}
      if (userInfo.value.rootFileId) {
        rootFileId.value = userInfo.value.rootFileId
      }
    } catch (_) {
      // no-op
    }
  }

  const initUserInfo = async () => {
    if (token.value) {
      await getUserInfoAction()
    }
  }

  const logout = () => {
    clearToken()
  }

  const updateAvatar = (avatarUrl) => {
    userInfo.value.avatar = avatarUrl
  }

  return {
    token,
    userInfo,
    rootFileId,
    isLoggedIn,
    loginAction,
    registerAction,
    getUserInfoAction,
    initUserInfo,
    logout,
    updateAvatar
  }
})
