/**
 * Cookie工具函数
 */

import Cookies from 'js-cookie'

const LOGIN_TOKEN = 'login_token'
const SHARE_TOKEN = 'share_token'
const EMPTY_STR = ''

export function setToken(token) {
  Cookies.set(LOGIN_TOKEN, token)
}

export function getToken() {
  let token = Cookies.get(LOGIN_TOKEN)
  if (token) {
    return token
  }
  return EMPTY_STR
}

export function clearToken() {
  Cookies.remove(LOGIN_TOKEN)
}

export function setShareToken(token) {
  Cookies.set(SHARE_TOKEN, token)
}

export function getShareToken() {
  let token = Cookies.get(SHARE_TOKEN)
  if (token) {
    return token
  }
  return EMPTY_STR
}

export function clearShareToken() {
  Cookies.remove(SHARE_TOKEN)
}
