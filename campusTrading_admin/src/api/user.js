import request from '@/utils/request'

export const userApi = {
  adminLogin(data) {
    return request({
      url: '/AdminLogin',
      method: 'post',
      data
    })
  },

  adminRegister(data) {
    return request({
      url: '/AdminRegister',
      method: 'post',
      data
    })
  },

  getAdminInfo() {
    return request({
      url: '/admin/info',
      method: 'get'
    })
  },

  adminLogout() {
    return request({
      url: '/admin/logout',
      method: 'post'
    })
  },

  getDashboardStats() {
    return request({
      url: '/admin/dashboard/stats',
      method: 'get'
    })
  },

  getDashboardData() {
    return request({
      url: '/admin/dashboard/data',
      method: 'get'
    })
  },

  getBuyerList(params) {
    return request({
      url: '/admin/users/buyers',
      method: 'get',
      params
    })
  },

  getSellerList(params) {
    return request({
      url: '/admin/users/sellers',
      method: 'get',
      params
    })
  },

  getList(params) {
    return request({
      url: '/admin/users',
      method: 'get',
      params
    })
  },

  getDetail(id) {
    return request({
      url: `/admin/users/${id}`,
      method: 'get'
    })
  },

  create(data) {
    return request({
      url: '/admin/users',
      method: 'post',
      data
    })
  },

  update(id, data) {
    return request({
      url: `/admin/users/${id}`,
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/admin/users/${id}`,
      method: 'delete'
    })
  },

  updateProfile(data) {
    return request({
      url: '/admin/info',
      method: 'put',
      data
    })
  }
}
