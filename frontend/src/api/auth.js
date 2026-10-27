import request from './request'

// 登录：POST /login  -> { code, msg, token, userInfo }
export function login(data) {
  return request({ url: '/login', method: 'post', data })
}

// 退出登录：POST /logout（受 token 保护）
export function logout() {
  return request({ url: '/logout', method: 'post' })
}

// 获取当前登录用户：GET /me（受 token 保护，用于校验 token 是否仍有效）
export function getMe() {
  return request({ url: '/me', method: 'get' })
}
