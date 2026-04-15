<template>
  <div class="message-page">
    <div class="chat-list">
      <div class="chat-list-header">
        <span>我的消息</span>
        <el-button 
          v-if="hasUnreadMessages" 
          type="primary" 
          size="small" 
          @click="markAllAsRead"
          class="mark-all-read-btn"
        >
          一键已读
        </el-button>
      </div>
      
      <div 
        class="chat-item" 
        v-for="session in chatSessions" 
        :key="session.productId + '-' + session.oppositeUserId"
        :class="{ active: isActiveSession(session) }"
        @click="switchChat(session)"
      >
        <img :src="getMsgImageUrl(session.productImage)" alt="商品图片" class="avatar" @error="handleMsgImageError" />
        
        <div class="chat-info">
          <div class="product-name">{{ session.productName }}</div>
          <div class="opposite-nickname">{{ session.oppositeNickname }}</div>
          <div class="last-msg" v-if="session.latestMessage">{{ session.latestMessage }}</div>
        </div>
        
        <div class="chat-right">
          <div class="last-time" v-if="session.latestMessageTime">{{ formatTime(session.latestMessageTime) }}</div>
          <div v-if="session.unreadCount > 0" class="unread-badge">{{ session.unreadCount }}</div>
        </div>
      </div>
      
      <div class="empty-chat" v-if="chatSessions.length === 0 && !loading">
        <el-empty description="暂无聊天会话">
          <el-button type="primary" @click="$router.push('/new')">去交易市场</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 给 chat-window 加 key，强制 Vue 完全销毁重建 DOM -->
    <div class="chat-window" v-if="currentSession" :key="activeSessionKey">
      <div class="chat-window-header">
        <img :src="getMsgImageUrl(currentSession.productImage)" alt="商品图片" class="avatar" @error="handleMsgImageError" />
        <div class="header-info">
          <div class="product-name">{{ currentSession.productName }}</div>
          <div class="opposite-nickname">{{ currentSession.oppositeNickname }}</div>
        </div>
        <el-button 
          v-if="currentSession.unreadCount > 0" 
          type="success" 
          size="small" 
          @click="markSessionAsRead"
        >
          标记已读
        </el-button>
      </div>

      <div class="chat-content" ref="chatContentRef">
        <!-- 核心修复：isLoadingChat 为 true 时，整个聊天内容区域显示加载状态，不显示任何消息 -->
        <template v-if="currentSession && !isLoadingChat">
          <div v-if="currentMsgList.length > 0">
            <div 
              class="msg-item" 
              v-for="msg in currentMsgList" 
              :key="msg.id"
              :class="msg.sender === 1 ? 'my-msg' : 'merchant-msg'"  
            >
              <img 
                v-if="msg.sender !== 1"
                :src="getMsgImageUrl(currentSession.oppositeAvatar)" 
                alt="对方头像" 
                class="avatar"
                @error="handleMsgImageError"
              />
              
              <div class="msg-wrapper">
                <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
                <div class="msg-bubble" :class="msg.sender === 1 ? 'my-bubble' : 'merchant-bubble'">
                  <div class="msg-content">{{ msg.content }}</div>
                </div>
                <div class="read-status" :class="msg.isRead ? 'read' : 'unread'">
                  {{ msg.isRead ? '已读' : '未读' }}
                </div>
              </div>
              
              <img 
                v-if="msg.sender === 1"
                :src="userAvatar" 
                alt="我的头像" 
                class="avatar"
                @error="handleMsgImageError"
              />
            </div>
          </div>
          
          <div v-else class="empty-messages">
            <el-empty description="暂无聊天记录，开始聊天吧" />
          </div>
        </template>
        
        <!-- 加载中状态 -->
        <div v-else class="loading-messages">
          <el-empty description="加载中..." />
        </div>
      </div>

      <div class="chat-input-area">
        <textarea 
          v-model="inputContent" 
          placeholder="请输入消息..." 
          class="msg-input"
          @keyup.enter="sendMsg"
        ></textarea>
        <button class="send-btn" @click="sendMsg" :disabled="!inputContent.trim()">发送</button>
      </div>
    </div>

    <div class="empty-tip" v-else>
      <el-empty description="请选择要聊天的商家" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useRouter, useRoute, onBeforeRouteLeave } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/request'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const chatSessions = ref([])
const activeSessionKey = ref('')
const inputContent = ref('')
const chatContentRef = ref(null)
const loading = ref(false)
const currentMsgList = ref([])
const currentSessionRef = ref(null)

// 核心：会话加载锁，true表示正在加载中，此时不显示聊天内容
const isLoadingChat = ref(false)
let lastProductId = null
let lastSellerId = null
let isOpeningChat = false

const CHAT_SESSIONS_API = '/user/message/sessions'
const CHAT_RECORD_API = '/user/message/chat'

const userAvatar = computed(() => {
  const info = authUtils.getUserInfo()
  let avatar = info?.avatar
  if (!avatar || avatar === 'null' || avatar === 'undefined') {
    return '/头像.png'
  }
  if (avatar.startsWith('http')) {
    return avatar
  }
  return `http://localhost:8090${avatar}`
})

const currentSession = computed(() => {
  return currentSessionRef.value
})

const isActiveSession = (session) => {
  return activeSessionKey.value === `${session.productId}-${session.oppositeUserId}`
}

const formatTime = (timeStr) => {
  if (!timeStr) return '刚刚'
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000 && date.toDateString() === now.toDateString()) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  return date.toLocaleString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getMsgImageUrl = (url) => {
  let imageUrl = url
  if (Array.isArray(url) && url.length > 0) {
    imageUrl = url[0]
  }
  if (!imageUrl || typeof imageUrl !== 'string') return '/placeholder.png'
  if (imageUrl.startsWith('http')) return imageUrl
  if (imageUrl.startsWith('/res/images/')) {
    return `http://localhost:8090${imageUrl}`
  }
  if (imageUrl.startsWith('/images/')) {
    return `http://localhost:8090${imageUrl}`
  }
  return `http://localhost:8090/res/images/${imageUrl}`
}

const handleMsgImageError = (e) => {
  e.target.src = '/头像.png'
}

const hasUnreadMessages = computed(() => {
  return chatSessions.value.some(s => s.unreadCount > 0)
})

// 重置状态（注意：不重置 isLoadingChat，由调用方控制）
const resetChatState = () => {
  currentMsgList.value = []
  activeSessionKey.value = ''
  currentSessionRef.value = null
  inputContent.value = ''
}

// 核心修复：openNewChat 先加锁再操作
const openNewChat = async (productId, sellerId) => {
  if (!productId || !sellerId) return
  if (isOpeningChat) return

  console.log('=== openNewChat 开始 ===', productId, sellerId)
  
  try {
    isOpeningChat = true
    
    // 第一步：立即加锁，显示加载状态，防止显示旧内容
    isLoadingChat.value = true
    await nextTick()
    
    // 第二步：清空旧数据
    resetChatState()
    
    await getChatSessionList()
    
    const session = chatSessions.value.find(s => 
      s.productId === productId && s.oppositeUserId === sellerId
    )

    if (session) {
      console.log('找到历史会话:', session)
      activeSessionKey.value = `${session.productId}-${session.oppositeUserId}`
      currentSessionRef.value = session
      // 加载数据（内部会解锁）
      await loadChatRecord(session)
    } else {
      console.log('创建临时会话')
      
      const tempSession = {
        productId: productId,
        productName: route.query.productName || '商品会话',
        productImage: route.query.productImage || '/placeholder.png',
        oppositeUserId: sellerId,
        oppositeNickname: route.query.sellerNickname || '卖家',
        oppositeAvatar: route.query.sellerAvatar || '/头像.png',
        latestMessage: '',
        latestMessageTime: '',
        unreadCount: 0,
        isTemporary: true
      }
      
      chatSessions.value = [tempSession, ...chatSessions.value]
      activeSessionKey.value = `${tempSession.productId}-${tempSession.oppositeUserId}`
      currentSessionRef.value = tempSession
      currentMsgList.value = []
      
      // 临时会话直接解锁，显示"暂无聊天记录"
      isLoadingChat.value = false
    }
    
    console.log('=== openNewChat 结束 ===')
  } finally {
    isOpeningChat = false
  }
}

const getChatSessionList = async () => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }

  loading.value = true
  try {
    const response = await axios.get(CHAT_SESSIONS_API)
    console.log('会话列表返回:', response)
    if (response.code === 200) {
      const realSessions = (response.data || []).map(item => ({
        productId: item.productId,
        productName: item.productName || '未知商品',
        productImage: item.productImage || '/placeholder.png',
        oppositeUserId: item.oppositeUserId,
        oppositeNickname: item.oppositeNickname || '匿名用户',
        oppositeAvatar: item.oppositeAvatar || '/头像.png',
        latestMessage: item.latestMessage,
        latestMessageTime: item.latestMessageTime,
        unreadCount: item.unreadCount || 0
      }))
      
      chatSessions.value = realSessions
      
      if (chatSessions.value.length > 0 && !activeSessionKey.value) {
        const firstSession = chatSessions.value[0]
        activeSessionKey.value = `${firstSession.productId}-${firstSession.oppositeUserId}`
        currentSessionRef.value = firstSession
        await loadChatRecord(firstSession)
      }
    }
  } catch (error) {
    console.error('加载会话异常:', error)
  } finally {
    loading.value = false
  }
}

// 核心修复：switchChat 先加锁，再清空，再切换
const switchChat = async (session) => {
  console.log('=== switchChat ===', session)
  
  // 第一步：立即加锁，隐藏聊天内容区域
  isLoadingChat.value = true
  await nextTick()
  
  // 第二步：清空旧数据（此时用户看不到，因为 isLoadingChat=true）
  currentMsgList.value = []
  inputContent.value = ''
  activeSessionKey.value = ''
  
  // 第三步：切换 session
  activeSessionKey.value = `${session.productId}-${session.oppositeUserId}`
  currentSessionRef.value = session
  
  // 第四步：加载新数据（内部会解锁）
  await loadChatRecord(session)
}

const loadChatRecord = async (session) => {
  console.log('=== loadChatRecord ===', session)
  if (!session) return
  
  try {
    // 确保是加载状态
    isLoadingChat.value = true
    
    if (session.isTemporary) {
      console.log('临时会话，清空聊天记录')
      currentMsgList.value = []
      return
    }
    
    const response = await axios.get(CHAT_RECORD_API, {
      params: {
        productId: session.productId,
        otherUserId: session.oppositeUserId
      }
    })
    
    if (response.code === 200) {
      const currentUserInfo = authUtils.getUserInfo()
      const currentUserId = currentUserInfo?.userId || currentUserInfo?.id
      
      currentMsgList.value = (response.data || []).map((msg) => {
        const messageId = msg.messageId || msg.message_id || msg.id || msg.messageid
        const senderId = msg.senderId || msg.sender_id || msg.sender
        const content = msg.content
        const createdAt = msg.createdAt || msg.created_at || msg.createTime
        const senderNickname = msg.senderNickname || msg.sender_nickname
        const receiverNickname = msg.receiverNickname || msg.receiver_nickname
        
        let isRead = false
        if (msg.isRead === 1 || msg.isRead === true || msg.isRead === '1' || msg.isRead === 'true') {
          isRead = true
        }
        if (msg.is_read === 1 || msg.is_read === true || msg.is_read === '1' || msg.is_read === 'true') {
          isRead = true
        }
        if (msg.isRead == 1) {
          isRead = true
        }
        if (msg.is_read == 1) {
          isRead = true
        }
        
        const currentUserNickname = currentUserInfo?.nickname
        let isSelf = false
        
        if (currentUserNickname) {
          if (receiverNickname === currentUserNickname) {
            isSelf = false
          } else if (senderNickname === currentUserNickname) {
            isSelf = true
          } else {
            isSelf = String(senderId) === String(currentUserId)
          }
        } else {
          isSelf = String(senderId) === String(currentUserId)
        }
        
        const senderValue = isSelf ? 1 : 2
        
        return {
          id: messageId,
          content: content,
          sender: senderValue,
          createTime: createdAt,
          isRead: isRead
        }
      })
      
      console.log('聊天记录处理后:', currentMsgList.value)
    } else {
      currentMsgList.value = []
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    currentMsgList.value = []
  } finally {
    // 无论成功失败，都解除加载状态，显示内容（或空状态）
    isLoadingChat.value = false
  }
}

const markSessionAsRead = async () => {
  if (!currentSession.value) return
  try {
    const response = await axios.post('/user/message/read/batch', null, {
      params: {
        productId: currentSession.value.productId,
        otherUserId: currentSession.value.oppositeUserId
      }
    })
    if (response.code === 200) {
      ElMessage.success('已标记为已读')
      currentSession.value.unreadCount = 0
      const sessionIndex = chatSessions.value.findIndex(s => 
        s.productId === currentSession.value.productId && 
        s.oppositeUserId === currentSession.value.oppositeUserId
      )
      if (sessionIndex !== -1) {
        chatSessions.value[sessionIndex].unreadCount = 0
      }
      loadChatRecord(currentSession.value)
    } else {
      ElMessage.error(response.msg || '标记已读失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsRead = async () => {
  try {
    for (const session of chatSessions.value) {
      if (session.unreadCount > 0) {
        await axios.post('/user/message/read/batch', null, {
          params: {
            productId: session.productId,
            otherUserId: session.oppositeUserId
          }
        })
        session.unreadCount = 0
      }
    }
    ElMessage.success('所有消息已标记为已读')
    if (currentSession.value) {
      loadChatRecord(currentSession.value)
    }
  } catch (error) {
    console.error('一键已读失败:', error)
    ElMessage.error('一键已读失败')
  }
}

const sendMsg = async () => {
  if (!inputContent.value.trim()) return
  if (!currentSession.value) return

  const targetProductId = currentSession.value.productId
  const targetOppositeUserId = currentSession.value.oppositeUserId

  console.log('=== sendMsg ===', targetProductId, targetOppositeUserId)

  try {
    const response = await axios.post('/user/message/send', {
      productId: targetProductId,
      receiverId: targetOppositeUserId,
      content: inputContent.value.trim()
    })
    
    if (response.code === 200) {
      inputContent.value = ''
      
      await getChatSessionList()
      await nextTick()
      
      const newSession = chatSessions.value.find(s => 
        s.productId === targetProductId && 
        s.oppositeUserId === targetOppositeUserId
      )
      
      console.log('发送后找到的新会话:', newSession)
      
      if (newSession) {
        activeSessionKey.value = `${newSession.productId}-${newSession.oppositeUserId}`
        currentSessionRef.value = newSession
        loadChatRecord(newSession)
      }
    } else {
      ElMessage.error('发送失败：' + response.msg)
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请重试')
  }
}

watch(currentMsgList, () => {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}, { deep: true })

onMounted(() => {
  console.log('MyNews - 组件挂载')
  resetChatState()
  
  const productId = route.query.productId
  const sellerId = route.query.sellerId

  if (productId && sellerId) {
    lastProductId = Number(productId)
    lastSellerId = Number(sellerId)
    openNewChat(Number(productId), Number(sellerId))
  } else {
    getChatSessionList()
  }
})

onBeforeRouteLeave(() => {
  resetChatState()
  isLoadingChat.value = false
})

watch(() => route.query, (newQuery) => {
  const productId = newQuery.productId
  const sellerId = newQuery.sellerId

  if (productId && sellerId) {
    const numericProductId = Number(productId)
    const numericSellerId = Number(sellerId)
    
    if (numericProductId !== lastProductId || numericSellerId !== lastSellerId) {
      lastProductId = numericProductId
      lastSellerId = numericSellerId
      openNewChat(numericProductId, numericSellerId)
    }
  }
})
</script>

<style scoped>
.message-page {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.chat-list {
  width: 300px;
  background: #fff;
  border-right: 1px solid #eee;
  overflow-y: auto;
}

.chat-list-header {
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mark-all-read-btn {
  font-size: 12px;
  padding: 6px 12px;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}

.chat-item:hover {
  background: #f8f9fa;
}

.chat-item.active {
  background: #e8f4ff;
}

.chat-item .avatar {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.chat-info {
  flex: 1;
  overflow: hidden;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.opposite-nickname {
  font-size: 12px;
  color: #999;
}

.last-msg {
  font-size: 11px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 60px;
}

.last-time {
  font-size: 10px;
  color: #ccc;
}

.unread-badge {
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 4px;
  min-width: 20px;
  text-align: center;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-window-header {
  padding: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.chat-window-header .avatar {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.header-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.opposite-nickname {
  font-size: 12px;
  color: #999;
}

.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #fafafa;
}

.loading-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.msg-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.merchant-msg {
  justify-content: flex-start;
}

.my-msg {
  justify-content: flex-end;
}

.msg-item .avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-top: 4px;
  object-fit: cover;
}

.merchant-msg .avatar {
  margin-right: 8px;
}

.my-msg .avatar {
  margin-left: 8px;
}

.msg-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.merchant-msg .msg-wrapper {
  align-items: flex-start;
}

.my-msg .msg-wrapper {
  align-items: flex-end;
}

.msg-bubble {
  display: flex;
  flex-direction: column;
}

.merchant-bubble {
  background: #e0e0e0;
  color: #333;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
}

.my-bubble {
  background: #c7edfc;
  color: #333;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
}

.msg-content {
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.msg-time {
  font-size: 11px;
  color: #999;
  margin-bottom: 6px;
}

.read-status {
  font-size: 12px;
  margin-top: 6px;
}

.read-status.read {
  color: #67c23a;
}

.read-status.unread {
  color: #999;
}

.merchant-msg .read-status {
  text-align: left;
}

.my-msg .read-status {
  text-align: right;
}

.chat-input-area {
  display: flex;
  padding: 16px;
  border-top: 1px solid #eee;
  gap: 12px;
  background: #fff;
}

.msg-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  height: 60px;
  font-family: inherit;
}

.send-btn {
  padding: 0 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.send-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.empty-tip {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 18px;
}

.empty-chat {
  padding: 50px 0;
  text-align: center;
}

.empty-messages {
  text-align: center;
  padding: 50px 0;
}
</style>