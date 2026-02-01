import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const userRequest = axios.create({
  baseURL: '/api/v1/users',
  timeout: 10000
})
attachInterceptors(userRequest, 'userRequest')

// 获取用户信息
export function getUserInfo() {
  return userRequest({
    url: '/get-user-info',
    method: 'get'
  })
}

// 修改密码
export function changePassword(data) {
  return userRequest({
    url: '/user/password/change',
    method: 'post',
    data
  })
}

// 校验用户名
export function checkUsername(data) {
  return userRequest({
    url: '/user/username/check',
    method: 'post',
    data
  })
}

// 校验密保答案
export function checkAnswer(data) {
  return userRequest({
    url: '/user/answer/check',
    method: 'post',
    data
  })
}

// 重置密码
export function resetPassword(data) {
  return userRequest({
    url: '/user/password/reset',
    method: 'post',
    data
  })
} 