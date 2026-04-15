import request from '@/utils/request'

export const messageApi = {
  getList(params) {
    return request({
      url: '/admin/messages',
      method: 'get',
      params
    })
  },

  getDetail(id) {
    return request({
      url: `/admin/messages/${id}`,
      method: 'get'
    })
  },

  send(data) {
    return request({
      url: '/admin/messages',
      method: 'post',
      data
    })
  },

  delete(id) {
    return request({
      url: `/admin/messages/${id}`,
      method: 'delete'
    })
  },

  markAsRead(id) {
    return request({
      url: `/admin/messages/${id}/read`,
      method: 'put'
    })
  }
}
