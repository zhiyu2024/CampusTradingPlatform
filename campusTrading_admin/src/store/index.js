import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || ''
  }),
  
  actions: {
    setUserInfo(info) {
      this.userInfo = info
    },
    
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    
    logout() {
      this.userInfo = null
      this.token = ''
      localStorage.removeItem('token')
    }
  }
})