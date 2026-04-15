import { defineStore } from 'pinia'
import { authUtils } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLogin: authUtils.isLogin(),
    token: authUtils.getToken(),
    userInfo: authUtils.getUserInfo()
  }),
  
  actions: {
    login(token, userInfo) {
      authUtils.setLogin(token, userInfo)
      this.isLogin = true
      this.token = token
      this.userInfo = userInfo
    },
    
    logout() {
      authUtils.logout()
      this.isLogin = false
      this.token = null
      this.userInfo = null
    },
       // ✅ 初始化或刷新状态
    initAuth() {
      this.isLogin = authUtils.isLogin()
      this.token = authUtils.getToken()
      this.userInfo = authUtils.getUserInfo()
    }
  }
})