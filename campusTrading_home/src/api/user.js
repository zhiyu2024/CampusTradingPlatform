// src/api/user.js
import request from '@/utils/request' // 统一从utils导入

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 登录接口
export const userLogin = (data) => {
  return request({
    url: '/public/login',
    method: 'post',
    data
  })
}

// 退出登录
export const userLogout = () => {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}