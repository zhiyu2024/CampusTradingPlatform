import request from '@/utils/request'

export const categoryApi = {
  getList(params) {
    return request({
      url: '/admin/categories',
      method: 'get',
      params
    })
  },

  getAll() {
    return request({
      url: '/admin/categories/all',
      method: 'get'
    })
  },

  getDetail(id) {
    return request({
      url: `/admin/categories/${id}`,
      method: 'get'
    })
  },

  create(data) {
    return request({
      url: '/admin/categories',
      method: 'post',
      data
    })
  },

  update(id, data) {
    return request({
      url: `/admin/categories/${id}`,
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/admin/categories/${id}`,
      method: 'delete'
    })
  },

  getCategoryStats() {
    return request({
      url: '/admin/categories/stats',
      method: 'get'
    })
  }
}
