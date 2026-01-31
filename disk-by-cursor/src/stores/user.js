import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import Cookies from 'js-cookie'
import { getUserInfo } from '@/api/user'
import { login, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(Cookies.get('token') || '')
  const userInfo = ref({})
  const isLoggedIn = computed(() => !!token.value)
  const rootFileId = ref('')

  // 初始化时如果有token就获取用户信息
  const initUserInfo = async () => {
    if (token.value) {
      console.log("初始化时发现token，开始获取用户信息")
      await getUserInfoAction()
    }
  }

  const setToken = (newToken) => {
    console.log("newToken: ", newToken)
    token.value = newToken
    Cookies.set('token', newToken, { expires: 7 })
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = {}
    Cookies.remove('token')
  }

  const loginAction = async (loginData) => {
    try {
      console.log("开始登录，登录数据:", loginData)
      const response = await login(loginData)
      console.log("登录响应:", response)

      // 拦截器已经处理了响应，如果成功会返回response.data
      if (response && response.data) {
        console.log("登录成功，响应数据:", response.data)

        // 处理不同的响应格式
        let token = null
        if (typeof response.data === 'string' && response.data === 'SUCCESS') {
          // 如果后端返回SUCCESS字符串，token可能在响应头中
          console.log("后端返回SUCCESS，检查响应头中的token")
          // 这里token应该已经在拦截器中处理了
          token = response.data.token
        } else if (response.data.token) {
          token = response.data.token
        } else if (typeof response.data === 'string') {
          token = response.data
        } else {
          console.log("无法提取token，响应数据:", response.data)
          return { success: false, message: '无法获取登录token' }
        }
        console.log("提取的token:", token)
        setToken(token)
        await nextTick() // 等待 token 响应式写入
        console.log("token设置完成，当前token:", token.value)
        console.log("Cookies中的token:", Cookies.get('token'))
        // 等待一下确保token完全设置
        await new Promise(resolve => setTimeout(resolve, 100))
        console.log("开始获取用户信息")
        try {
          await getUserInfoAction()
          console.log("用户信息获取完成，用户信息:", userInfo.value)
        } catch (error) {
          console.error("获取用户信息失败:", error)
          // 即使获取用户信息失败，登录仍然成功
        }
        console.log("登录流程完成，返回成功")
        return { success: true }
      } else {
        console.log("响应格式错误:", response)
        return { success: false, message: '响应格式错误' }
      }
    } catch (error) {
      console.log("登录错误:", error)
      return { success: false, message: error.message || '登录失败' }
    }
  }

  const registerAction = async (registerData) => {
    try {
      const response = await register(registerData)
      console.log("register response:", response)
      
      // 拦截器已经处理了响应，如果成功会返回response.data
      if (response && response.data) {
        return { success: true }
      } else {
        return { success: false, message: '响应格式错误' }
      }
    } catch (error) {
      console.log("register error:", error)
      return { success: false, message: error.message || '注册失败' }
    }
  }

  const getUserInfoAction = async () => {
    try {
      console.log("开始获取用户信息，当前token:", Cookies.get('token'))
      const response = await getUserInfo()
      console.log("获取用户信息响应:", response)

      // 拦截器已经处理了响应，如果成功会返回response.data
      if (response && response.data) {
        console.log("用户信息获取成功:", response.data)
        userInfo.value = response.data
        // 新增：保存rootFileId
        if (response.data.rootFileId) {
          rootFileId.value = response.data.rootFileId
          console.log('保存rootFileId:', rootFileId.value)
        }
      } else {
        console.error('获取用户信息响应格式错误:', response)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
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
    isLoggedIn,
    rootFileId,
    loginAction,
    registerAction,
    getUserInfoAction,
    logout,
    updateAvatar,
    initUserInfo
  }
}) 