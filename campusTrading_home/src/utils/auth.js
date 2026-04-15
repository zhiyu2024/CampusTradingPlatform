// src/utils/auth.js
const TOKEN_KEY = 'campus_token'
const USER_KEY = 'campus_user'

export const authUtils = {
  setLogin(token, userInfo) {
    sessionStorage.setItem(TOKEN_KEY, token)
    sessionStorage.setItem(USER_KEY, JSON.stringify(userInfo))
  },
  
  getToken() {
    return sessionStorage.getItem(TOKEN_KEY)
  },
  
  isLogin() {
    return !!this.getToken()
  },
  
  getUserInfo() {
    const userStr = sessionStorage.getItem(USER_KEY)
    return userStr ? JSON.parse(userStr) : null
  },
  
  logout() {
    sessionStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
  }
}