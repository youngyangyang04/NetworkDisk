import axios from 'axios'

const fileRequest = axios.create({
  baseURL: '/api/v1/recycles',
  timeout: 10000
})

// 获取回收站文件列表
export function getRecycleList() {
  return fileRequest({
    url: '/list',
    method: 'get'
  })
}

// 还原文件
export function restoreFiles(data) {
  return fileRequest({
    url: '/recycle/restore',
    method: 'put',
    data
  })
}

// 彻底删除文件
export function deleteRecycleFiles(data) {
  return fileRequest({
    url: '/recycle',
    method: 'delete',
    data
  })
} 