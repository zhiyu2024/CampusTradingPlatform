import request from '@/utils/request'

export const orderApi = {
  getList(params) {
    return request({
      url: '/admin/orders',
      method: 'get',
      params
    })
  },

  getDetail(id) {
    return request({
      url: `/admin/orders/${id}`,
      method: 'get'
    })
  },

  updateStatus(id, status) {
    return request({
      url: `/admin/orders/${id}/status`,
      method: 'put',
      data: { status }
    })
  },

  getOrderStats() {
    return request({
      url: '/admin/orders/stats',
      method: 'get'
    })
  }
}
