import { createStore } from 'vuex'

export default createStore({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null
  },
  mutations: {
    setToken(state, val) {
      state.token = val
      localStorage.setItem('token', val)
    },
    setUserInfo(state, val) {
      state.userInfo = val
      localStorage.setItem('userInfo', JSON.stringify(val))
    },
    clearUserInfo(state) {
      state.token = ''
      state.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  },
  actions: {
  },
  getters: {
  }
})
