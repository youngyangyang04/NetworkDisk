import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export function attachInterceptors(instance, label = '') {
  // 请求拦截器
  instance.interceptors.request.use(
    (config) => {
      if (!config.headers) config.headers = {}
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = `${userStore.token}`
        if (label) console.log(`[${label}] set authorization token`, userStore.token)
      } else {
        if (label) console.log(`[${label}] no token in userStore`)
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  // 响应拦截器
  instance.interceptors.response.use(
    (response) => {
      // 检查业务未登录
      if (
        response.data &&
        (
          response.data.code === 'NOT_LOGIN' ||
          response.data.message === '未登录'
        )
      ) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
        return Promise.reject(new Error('未登录'))
      }
      console.log(`[${label}] 响应拦截器收到响应:`, response)
      console.log(`[${label}] 响应数据:`, response.data)
      
      // 检查响应数据类型
      if (typeof response.data === 'string') {
        console.log(`[${label}] 响应是字符串:`, response.data)
        console.log(`[${label}] 响应头:`, response.headers)
        
        if (response.data === 'SUCCESS') {
          console.log(`[${label}] 登录成功`)
          // 检查响应头中是否有token
          const token = response.headers['authorization'] || response.headers['Authorization'] || response.headers['x-access-token']
          console.log(`[${label}] 从响应头获取的token:`, token)
          return { 
            code: 200, 
            data: { token: token, message: response.data }, 
            message: '登录成功' 
          }
        } else {
          console.log(`[${label}] 登录失败:`, response.data)
          ElMessage.error(response.data || '登录失败')
          return Promise.reject(new Error(response.data || '登录失败'))
        }
      }
      
      if (response.data && response.data.code !== undefined) {
        console.log(`[${label}] 响应code:`, response.data.code)
        if (response.data.code === 200 || response.data.success === true) {
          console.log(`[${label}] 响应成功，返回数据:`, response.data)
          return response.data
        } else {
          console.log(`[${label}] 响应失败:`, response.data.message)
          ElMessage.error(response.data.message || '请求失败')
          return Promise.reject(new Error(response.data.message || '请求失败'))
        }
      }
      console.log(`[${label}] 响应数据格式不符合预期，直接返回:`, response)
      return response
    },
    (error) => {
      console.log(`[${label}] 响应拦截器错误:`, error)
      console.log(`[${label}] 错误响应:`, error.response)
      
      // 检查 401 未登录或登录过期
      if (error.response?.status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        // 强制跳转登录页并刷新，彻底清理所有状态
        window.location.href = '/login'
      }
      ElMessage.error(error.message || '网络错误')
      return Promise.reject(error)
    }
  )
}