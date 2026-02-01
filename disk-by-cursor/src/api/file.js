import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const fileRequest = axios.create({
  baseURL: '/api/v1/files',
  timeout: 10000
})
attachInterceptors(fileRequest, 'fileRequest')

// 获取文件列表
export function getFileList(params) {
  return fileRequest({
    url: '/folders-files',
    method: 'get',
    params
  })
}

// 获取面包屑
export function getBreadcrumbs(params) {
  return fileRequest({
    url: '/file/breadcrumbs',
    method: 'get',
    params
  })
}

// 创建文件夹
export function createFolder(data) {
  return fileRequest({
    url: '/folder',
    method: 'post',
    data
  })
}

// 文件重命名
export function renameFile(data) {
  return fileRequest({
    url: '/file',
    method: 'put',
    data
  })
}

// 删除文件
export function deleteFiles(data) {
  return fileRequest({
    url: '/file',
    method: 'delete',
    data
  })
}

// 文件复制
export function copyFiles(data) {
  return fileRequest({
    url: '/file/copy',
    method: 'post',
    data
  })
}

// 文件转移
export function transferFiles(data) {
  return fileRequest({
    url: '/file/transfer',
    method: 'post',
    data
  })
}

// 文件下载
export function downloadFile(params) {
  return fileRequest({
    url: '/file/download',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 文件预览
export function previewFile(params) {
  return fileRequest({
    url: '/file/preview',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 搜索文件
export function searchFiles(params) {
  return fileRequest({
    url: '/file/search',
    method: 'get',
    params
  })
}

// 获取文件夹树
export function getFolderTree() {
  return fileRequest({
    url: '/file/folder/tree',
    method: 'get'
  })
}

// 单文件上传
export function uploadFile(data) {
  return fileRequest({
    url: '/file/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 文件秒传
export function secUpload(data) {
  return fileRequest({
    url: '/file/sec-upload',
    method: 'post',
    data
  })
}

// 分片上传
export function chunkUpload(data) {
  return fileRequest({
    url: '/file/chunk-upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取已上传分片
export function getUploadedChunks(params) {
  return fileRequest({
    url: '/file/chunk-upload',
    method: 'get',
    params
  })
}

// 合并分片
export function mergeChunks(data) {
  return fileRequest({
    url: '/file/merge',
    method: 'post',
    data
  })
} 