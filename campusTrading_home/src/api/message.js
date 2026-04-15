import request from './request'

// 发送消息
export const sendMessage = (data) => {
  return request({
    url: '/user/message/send',
    method: 'post',
    data
  })
}

// 标记消息已读
export const markAsRead = (messageId) => {
  return request({
    url: '/user/message/read',
    method: 'post',
    params: { messageId }
  })
}

// 批量标记已读
export const markAllAsRead = (productId, otherUserId) => {
  return request({
    url: '/user/message/read/batch',
    method: 'post',
    params: { productId, otherUserId }
  })
}

// 获取未读消息数
export const getUnreadCount = () => {
  return request({
    url: '/user/message/unread/count',
    method: 'get'
  })
}

// 获取聊天会话列表
export const getChatSessionList = () => {
  return request({
    url: '/user/message/sessions',
    method: 'get'
  })
}

// 获取聊天记录
export const getChatRecord = (productId, otherUserId) => {
  return request({
    url: '/user/message/chat',
    method: 'get',
    params: { productId, otherUserId }
  })
}

// 删除单条消息
export const deleteMessage = (messageId) => {
  return request({
    url: '/user/message/delete',
    method: 'delete',
    params: { messageId }
  })
}

// 删除聊天记录
export const deleteChatRecord = (productId, otherUserId) => {
  return request({
    url: '/user/message/delete/chat',
    method: 'delete',
    params: { productId, otherUserId }
  })
}
