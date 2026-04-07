import axios from 'axios'
import { attachInterceptors } from '@/utils/attachInterceptors'

const aiRequest = axios.create({
  baseURL: '/api/v1/ai',
  timeout: 30000
})
attachInterceptors(aiRequest, 'aiRequest')

export function getAiCapabilities() {
  return aiRequest({
    url: '/capabilities',
    method: 'get'
  })
}

export function indexAiFile(data) {
  return aiRequest({
    url: '/files/index',
    method: 'post',
    data
  })
}

export function summarizeFile(data) {
  return aiRequest({
    url: '/files/summary',
    method: 'post',
    data
  })
}

export function generateFileTags(data) {
  return aiRequest({
    url: '/files/tags',
    method: 'post',
    data
  })
}

export function askSingleFileQuestion(data) {
  return aiRequest({
    url: '/files/question',
    method: 'post',
    data
  })
}
