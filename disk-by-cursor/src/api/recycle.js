import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const recycleRequest = axios.create({
  baseURL: '/api/v1/recycles',
  timeout: 10000
})
attachInterceptors(recycleRequest, 'recycleRequest')

// 获取回收站文件列表
export function getRecycleList() {
  return recycleRequest({
    url: '/list',
    method: 'get'
  })
}

// 还原文件
export function restoreFiles(data) {
  return recycleRequest({
    url: '/recycle/restore',
    method: 'put',
    data
  })
}

// 彻底删除文件
export function deleteRecycleFiles(data) {
  return recycleRequest({
    url: '/recycle',
    method: 'delete',
    data
  })
} 
