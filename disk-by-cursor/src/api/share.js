import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const shareRequest = axios.create({
  baseURL: '/api/v1/shares',
  timeout: 10000
})
attachInterceptors(shareRequest, 'shareRequest')


// 获取分享列表
export function getShareList() {
  return shareRequest({
    url: '/list',
    method: 'get'
  })
}

// 创建分享
export function createShare(data) {
  return shareRequest({
    url: '/share',
    method: 'post',
    data
  })
}

// 取消分享
export function cancelShare(data) {
  return shareRequest({
    url: '/share',
    method: 'delete',
    data
  })
}

// 获取分享详情
export function getShareDetail(shareId) {
  return shareRequest({
    url: '/share',
    method: 'get',
    params: { shareId }
  })
}

// 获取分享简单详情
export function getShareSimpleDetail(params) {
  return shareRequest({
    url: '/share/simple',
    method: 'get',
    params
  })
}

// 校验分享码
export function checkShareCode(data) {
  return shareRequest({
    url: '/share/code/check',
    method: 'post',
    data
  })
}

