import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const authRequest = axios.create({
  baseURL: '/api/v1/auth',
  timeout: 10000
})
attachInterceptors(authRequest, 'authRequest')

// 用户登录
export function login(data) {
  return authRequest({
    url: '/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return authRequest({
    url: '/register',
    method: 'post',
    data
  })
}
// 用户登出
export function logout() {
  return authRequest({
    url: '/logout',
    method: 'post'
  })
}