import request from '@/utils/request'

// 加入购物车
export const addToCart = (params) => {
  return request({
    url: '/user/cart/add',
    method: 'post',
    params
  })
}

// 获取购物车列表
export const getCartList = () => {
  return request({
    url: '/user/cart/list',
    method: 'get'
  })
}

// 删除购物车商品
export const removeFromCart = (params) => {
  return request({
    url: '/user/cart/remove',
    method: 'post',
    params
  })
}

// 更新购物车数量
export const updateCartQuantity = (data) => {
  return request({
    url: '/user/cart/update',
    method: 'post',
    data
  })
}

// 清空购物车
export const clearCart = () => {
  return request({
    url: '/user/cart/clear',
    method: 'delete'
  })
}