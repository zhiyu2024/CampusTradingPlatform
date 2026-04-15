import request from './request'

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/user/order/create',
    method: 'post',
    data
  })
}

// 支付订单
export const payOrder = (orderId) => {
  return request({
    url: '/user/order/pay',
    method: 'post',
    params: { orderId }
  })
}

// 确认收货
export const confirmReceipt = (orderId) => {
  return request({
    url: '/user/order/confirm',
    method: 'post',
    params: { orderId }
  })
}

// 取消订单
export const cancelOrder = (orderId) => {
  return request({
    url: '/user/order/cancel',
    method: 'post',
    params: { orderId }
  })
}

// 获取买家订单列表
export const getBuyerOrderList = (params = {}) => {
  return request({
    url: '/user/order/buyer/list',
    method: 'get',
    params
  })
}

// 获取卖家订单列表
export const getSellerOrderList = (params = {}) => {
  return request({
    url: '/user/order/seller/list',
    method: 'get',
    params
  })
}

// 获取订单详情
export const getOrderDetail = (orderId) => {
  return request({
    url: '/user/order/detail',
    method: 'get',
    params: { orderId }
  })
}
