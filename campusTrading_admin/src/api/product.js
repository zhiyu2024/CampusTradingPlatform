import request from '@/utils/request'

export const productApi = {
  getList(params) {
    return request({
      url: '/admin/products',
      method: 'get',
      params
    })
  },

  getDetail(id) {
    return request({
      url: `/admin/products/${id}`,
      method: 'get'
    })
  },

  create(data) {
    return request({
      url: '/admin/products',
      method: 'post',
      data
    })
  },

  update(id, data) {
    return request({
      url: `/admin/products/${id}`,
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/admin/products/${id}`,
      method: 'delete'
    })
  },

  toggleStatus(id, status) {
    return request({
      url: `/admin/products/${id}/status`,
      method: 'put',
      data: { status }
    })
  },

  auditProduct(id, auditStatus, auditResult) {
    return request({
      url: `/admin/products/${id}/audit`,
      method: 'put',
      data: { auditStatus, auditResult }
    })
  },

  aiAuditProduct(id) {
    return request({
      url: `/admin/products/${id}/ai-audit`,
      method: 'post'
    })
  }
}
