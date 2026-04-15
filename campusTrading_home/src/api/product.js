// src/api/product.js
import request from '@/utils/request' // 统一从utils导入

// 获取商品列表（交易市场）
export const getProductList = (params = {}) => {
  return request({
    url: '/public/product',
    method: 'get',
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 16,
      ...params
    }
  })
}

// 获取商品详情
export const getProductDetail = (productId) => {
  return request({
    url: '/public/product/detail',
    method: 'get',
    params: { productId }
  })
}

// 搜索商品（需要登录）
export const searchProducts = (params = {}) => {
  return request({
    url: '/user/product/search',
    method: 'get',
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 16,
      ...params
    }
  })
}

// 添加商品
export const addProduct = (formData) => {
  return request({
    url: '/user/product/add',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新商品
export const updateProduct = (formData) => {
  return request({
    url: '/user/product/update',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除商品
export const deleteProduct = (productId) => {
  return request({
    url: '/user/product/delete',
    method: 'post',
    params: { productId }
  })
}