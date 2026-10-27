import axios from 'axios'
import store from '../store'

const service = axios.create({ baseURL: process.env.VUE_APP_BASE_API, timeout: 5000 })

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  // 后端 TokenInterceptor 要求：Authorization: Bearer <token>
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

service.interceptors.response.use(
  response => {
    const result = response.data
    if (result && result.code !== 200) {
      return Promise.reject(new Error(result.msg || '请求失败'))
    }
    return result
  },
  error => {
    // token 失效 / 未登录：后端返回 HTTP 401，统一清空并跳回登录页
    if (error.response && error.response.status === 401) {
      store.commit('clearUserInfo')
      if (window.location.hash !== '#/login') {
        window.location.href = '/#/login'
      }
    }
    return Promise.reject(error)
  }
)

export default service
