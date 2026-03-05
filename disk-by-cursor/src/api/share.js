import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const shareRequest = axios.create({
  baseURL: '/api/v1/shares',
  timeout: 10000
})
attachInterceptors(shareRequest, 'shareRequest')

export function getShareList() {
  return shareRequest({
    url: '/list',
    method: 'get'
  })
}

export function createShare(data) {
  return shareRequest({
    url: '/share',
    method: 'post',
    data
  })
}

export function cancelShare(data) {
  return shareRequest({
    url: '/share',
    method: 'delete',
    data
  })
}

export function getShareDetail(shareId) {
  return shareRequest({
    url: '/share',
    method: 'get',
    params: { shareId }
  })
}

export function getShareSimpleDetail(params) {
  return shareRequest({
    url: '/share/simple',
    method: 'get',
    params
  })
}

export function checkShareCode(data) {
  return shareRequest({
    url: '/share/code/check',
    method: 'post',
    data
  })
}

export function getShareFiles(params) {
  return shareRequest({
    url: '/share/files',
    method: 'get',
    params
  })
}

export function downloadShareFile(params) {
  return shareRequest({
    url: '/share/file/download',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
