
<template>
  <div class="personal-center-container">
    <!-- 左侧：用户信息 + 导航栏 -->
    <div class="sidebar">
      <!-- 用户信息卡片 -->
      <div class="user-sidebar-card">
        <div class="avatar-wrapper" @click="triggerFileUpload">
          <img :src="getAvatarUrl(userInfo.avatar)" alt="头像" class="sidebar-avatar" />
        </div>
        <div class="sidebar-user-info">
          <h3 class="sidebar-nickname">{{ userInfo.nickname || '未设置昵称' }}</h3>
          <p class="sidebar-meta">信用：★★★★☆ 交易：{{ userInfo.transactionCount || 0 }}次</p>
          <p class="sidebar-campus">{{ userInfo.campus || '未设置校区' }}</p>
        </div>
      </div>

      <!-- 导航菜单 -->
      <div class="sidebar-menu">
        <div 
          class="menu-item" 
          :class="{ active: activeMenu === 'info' }"
          @click="switchMenu('info')"
        >
          <el-icon><User /></el-icon>
          <span>我的信息</span>
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeMenu === 'cart' }"
          @click="switchMenu('cart')"
        >
          <el-icon><ShoppingCart /></el-icon>
          <span>我的购物车</span>
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeMenu === 'order' }"
          @click="switchMenu('order')"
        >
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeMenu === 'publish' }"
          @click="switchMenu('publish')"
        >
          <el-icon><Goods /></el-icon>
          <span>我的发布</span>
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeMenu === 'message' }"
          @click="switchMenu('message')"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span>我的消息</span>
        </div>
        <div class="menu-item logout" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </div>

    <!-- 右侧：内容区域 -->
    <div class="content-area">
      <div class="content-header">
        <el-button circle @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h2>{{ getMenuTitle() }}</h2>
      </div>

      <div class="content-body">
        <!-- 我的信息 -->
        <div v-if="activeMenu === 'info'" class="panel my-info-panel">
          <el-form label-width="100px" label-position="left">
            <el-form-item label="学号">
              <el-input v-model="userInfo.studentNo" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="校区">
              <el-input v-model="userInfo.campus" placeholder="请输入校区" />
            </el-form-item>
            <el-form-item label="头像">
              <div class="avatar-display">
                <img v-if="avatarPreview" :src="avatarPreview" alt="头像" class="avatar-preview" />
                <img v-else-if="userInfo.avatar" :src="getAvatarUrl(userInfo.avatar)" alt="头像" class="avatar-preview" />
                <div v-else class="avatar-empty">暂无头像</div>
              </div>
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :on-change="handleAvatarChange"
                :show-file-list="false"
                accept="image/*"
              >
                <el-button type="primary" size="small">更换头像</el-button>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveInfo" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 我的购物车 -->
        <div v-else-if="activeMenu === 'cart'" class="panel my-cart-panel">
          <div v-if="cartList.length > 0" class="cart-list">
            <div class="cart-header">
              <span>选择</span>
              <span>商品信息</span>
              <span>单价</span>
              <span>数量</span>
              <span>小计</span>
              <span>操作</span>
            </div>
            
          <div v-for="item in cartList" :key="item.cartId" class="cart-item">
            <div class="cart-check">
              <input type="checkbox" v-model="selectedCartIds" :value="item.cartId" />
            </div>
              <div class="cart-img">
                <img :src="getCartImageUrl(item.productImage)" alt="商品" @error="handleCartImageError" />
              </div>
              <div class="cart-info">
                <div class="cart-title">{{ item.productName }}</div>
              </div>
              <div class="cart-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="cart-quantity">
                <button @click="updateQuantity(item.cartId, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
                <input type="text" :value="item.quantity" readonly />
                <button @click="updateQuantity(item.cartId, item.quantity + 1)">+</button>
              </div>
              <div class="cart-total-item">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <div class="cart-delete" @click="removeFromCart(item.cartId)">删除</div>
            </div>

            <div class="cart-footer">
              <div class="cart-footer-left">
                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                  <!-- ✅ 全选框：绑定 isAllSelected + 触发 handleAllSelect -->
                  <input type="checkbox" v-model="isAllSelected" @change="handleAllSelect" />
                  <span>全选</span>
                </label>
                <button @click="clearCart" style="background: none; border: none; color: #666; cursor: pointer;">清空购物车</button>
              </div>
              <div class="cart-footer-right">
                <span class="cart-total-text">合计：</span>
                <span class="cart-total-price">¥{{ totalPrice.toFixed(2) }}</span>
                <button class="checkout-btn" @click="checkout">去结算</button>
              </div>
            </div>
          </div>
          
          <div v-else style="text-align: center; padding: 80px 20px;">
            <div style="font-size: 60px; margin-bottom: 20px;">🛒</div>
            <div style="font-size: 18px; color: #666; margin-bottom: 30px;">购物车是空的</div>
            <el-button type="primary" @click="$router.push('/new')">去逛逛</el-button>
          </div>
        </div>

        <!-- 我的订单 -->
        <div v-else-if="activeMenu === 'order'" class="panel my-order-panel">
          <div class="order-filter-tabs">
            <el-radio-group v-model="orderActiveTab" @change="loadOrders">
              <el-radio-button label="buyer">我买到的</el-radio-button>
              <el-radio-button label="seller">我卖出的</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="order-list">
            <div v-if="orderLoading" class="loading">加载中...</div>
            
            <div v-else-if="orders.length === 0" class="empty">
              <el-empty description="暂无订单" />
            </div>
            
            <div v-else class="order-items">
              <div v-for="order in orders" :key="order.orderId" class="order-item">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <el-tag :type="getStatusType(order.status)">
                    {{ getStatusText(order.status) }}
                  </el-tag>
                </div>
                
                <div class="order-body">
                  <div class="product-info">
                    <h4>{{ order.productName }}</h4>
                    <p class="amount">¥{{ order.totalAmount }}</p>
                  </div>
                  <div class="opposite-info">
                    <p>{{ orderActiveTab === 'buyer' ? '卖家' : '买家' }}：{{ order.oppositeName }}</p>
                  </div>
                </div>
                
                <div class="order-actions">
                  <el-button v-if="order.status === 0" size="small" type="primary" @click="payOrder(order.orderId)">
                    去支付
                  </el-button>
                  <el-button v-if="order.status === 0" size="small" @click="cancelOrder(order.orderId)">
                    取消订单
                  </el-button>
                  <el-button size="small" @click="viewDetail(order.orderId)">
                    查看详情
                  </el-button>
                  <el-button v-if="canComment(order)" size="small" type="warning" @click="openCommentDialog(order)">
                    去评价
                  </el-button>
                  <el-button v-if="hasCommented(order)" size="small" type="info" @click="viewOrderComment(order)">
                    查看评论
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <el-pagination
            v-if="orderTotal > 0"
            v-model:current-page="orderPageNum"
            v-model:page-size="orderPageSize"
            :total="orderTotal"
            layout="total, prev, pager, next"
            @current-change="loadOrders"
            class="pagination"
          />
        </div>

        <!-- 我的发布 -->
        <div v-else-if="activeMenu === 'publish'" class="panel my-publish-panel">
          <div class="product-list">
            <div v-if="publishLoading" class="loading-tip">
              <el-icon class="is-loading"><Loading /></el-icon> 加载中...
            </div>

            <div v-else-if="products.length === 0" class="empty-tip">
              <el-empty description="暂无发布商品" />
            </div>

            <div v-else v-for="product in products" :key="product.productId" class="product-item">
              <img :src="getPublishImageUrl(product.productImage)" alt="商品图片" class="product-img" />
              <div class="product-info">
                <h3 class="product-name">{{ product.productName }}</h3>
                <p class="product-price">¥{{ product.price }}</p>
                <p class="product-status">{{ getPublishStatusText(product.status) }}</p>
              </div>
              <div class="product-actions">
                <el-button size="small" @click="editProduct(product.productId)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteProduct(product.productId)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的消息 -->
        <div v-else-if="activeMenu === 'message'" class="panel my-message-panel">
          <div class="message-container">
            <div class="chat-list">
              <div class="chat-list-header">
                <span>会话列表</span>
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
                  <div class="last-msg" v-if="session.lastMsg">{{ session.lastMsg }}</div>
                </div>
                <div class="chat-right">
                  <div class="last-time" v-if="session.lastTime">{{ formatMsgTime(session.lastTime) }}</div>
                  <div v-if="session.unreadCount > 0" class="unread-badge">{{ session.unreadCount }}</div>
                </div>
              </div>
              
              <div class="empty-chat" v-if="chatSessions.length === 0 && !msgLoading">
                <el-empty description="暂无聊天会话" />
              </div>
            </div>

            <div class="chat-window" v-if="currentSession">
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
                <div v-if="currentMsgList.length > 0">
                  <div 
                    class="msg-item" 
                    v-for="msg in currentMsgList" 
                    :key="msg.id"
                    :class="msg.sender === 1 ? 'my-msg' : 'merchant-msg'"  
                  >
                    <img 
                      v-if="msg.sender !== 1"
                      :src="currentSession.oppositeAvatar" 
                      alt="对方头像" 
                      class="avatar"
                      @error="handleMsgImageError"
                    />
                    <div class="msg-wrapper">
                      <div class="msg-time">{{ formatMsgTime(msg.createTime) }}</div>
                      <div class="msg-bubble" :class="msg.sender === 1 ? 'my-bubble' : 'merchant-bubble'">
                        <div class="msg-content">{{ msg.content }}</div>
                      </div>
                      <div class="read-status" :class="msg.isRead ? 'read' : 'unread'">
                        {{ msg.isRead ? '已读' : '未读' }}
                      </div>
                    </div>
                    <img 
                      v-if="msg.sender === 1"
                      :src="userMsgAvatar" 
                      alt="我的头像" 
                      class="avatar"
                      @error="handleMsgImageError"
                    />
                  </div>
                </div>
                
                <div v-else class="empty-messages">
                  <el-empty description="暂无聊天记录，开始聊天吧" />
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
        </div>
      </div>
    </div>

    <!-- 订单详情弹框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <div v-if="orderDetail" class="order-detail">
        <div class="detail-item">
          <span class="label">订单号：</span>
          <span class="value">{{ orderDetail.orderNo }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商品名称：</span>
          <span class="value">{{ orderDetail.productName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">单价：</span>
          <span class="value">¥{{ orderDetail.price?.toFixed(2) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">数量：</span>
          <span class="value">{{ orderDetail.quantity }}</span>
        </div>
        <div class="detail-item">
          <span class="label">总价：</span>
          <span class="value price">¥{{ orderDetail.totalAmount?.toFixed(2) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">订单状态：</span>
          <el-tag :type="getStatusType(orderDetail.status)">{{ getStatusText(orderDetail.status) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="label">卖家：</span>
          <span class="value">{{ orderDetail.sellerNickname }}</span>
        </div>
        <div class="detail-item" v-if="orderDetail.buyerNote">
          <span class="label">买家备注：</span>
          <span class="value">{{ orderDetail.buyerNote }}</span>
        </div>
        <div class="detail-item">
          <span class="label">创建时间：</span>
          <span class="value">{{ orderDetail.createdAt }}</span>
        </div>
      </div>
    </el-dialog>

    <!-- 头像裁剪对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="更换头像" width="400px">
      <div class="avatar-preview">
        <img v-if="avatarPreview" :src="avatarPreview" alt="预览" />
      </div>
      <template #footer>
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="uploadAvatar" :loading="uploading">
          {{ uploading ? '上传中...' : '确认上传' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="commentDialogVisible"
      title="商品评价"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="commentForm" label-width="80px">
        <el-form-item label="商品">
          <span>{{ currentCommentOrder?.productName }}</span>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="commentForm.score" :max="5" show-score />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComment" :loading="commentSubmitting">
          {{ commentSubmitting ? '提交中...' : '提交评价' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看评论对话框 -->
    <el-dialog
      v-model="viewCommentDialogVisible"
      title="我的评价"
      width="500px"
    >
      <div v-if="currentOrderComment" class="order-comment">
        <div class="comment-header">
          <img :src="getUserAvatar(currentOrderComment)" class="comment-avatar" />
          <div class="comment-user">
            <span class="user-nickname">{{ getUserNickname(currentOrderComment) }}</span>
          </div>
        </div>
        <div class="comment-score">
          <el-rate v-model="currentOrderComment.score" disabled show-score />
        </div>
        <div class="comment-content">{{ currentOrderComment.content }}</div>
      </div>
      <div v-else class="no-comment">
        <el-empty description="暂无评价" />
      </div>
      <template #footer>
        <el-button @click="viewCommentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, ShoppingCart, Document, Goods, ChatDotRound, 
  SwitchButton, ArrowLeft, Loading 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const activeMenu = ref('info')

const isAllSelected = ref(false) // 全选框状态
const selectedCartIds = ref([])  // 已选中的商品ID数组

// 用户信息
const userInfo = reactive({
  studentNo: '',
  nickname: '',
  phone: '',
  avatar: '',
  campus: '',
  role: '',
  status: '',
  createdAt: ''
})

// 头像上传相关
const fileInput = ref(null)
const avatarDialogVisible = ref(false)
const avatarPreview = ref('')
const selectedFile = ref(null)
const uploading = ref(false)
const saving = ref(false)
const avatarFile = ref(null)


// 购物车相关
const cartList = ref([])
const cartLoading = ref(false)


// 订单相关
const orders = ref([])
const orderLoading = ref(false)
const orderPageNum = ref(1)
const orderPageSize = ref(10)
const orderTotal = ref(0)
const orderActiveTab = ref('buyer')
const detailDialogVisible = ref(false)
const orderDetail = ref(null)

// 评论相关
const commentDialogVisible = ref(false)
const commentSubmitting = ref(false)
const currentCommentOrder = ref(null)
const commentForm = reactive({
  orderId: null,
  productId: null,
  content: '',
  score: 5
})

// 查看评论相关
const viewCommentDialogVisible = ref(false)
const currentOrderComment = ref(null)

// 存储已评论的订单ID
// const commentedOrderIds = ref(new Set())

// 发布相关
const products = ref([])
const publishLoading = ref(false)

// 消息相关
const chatSessions = ref([])
const activeSessionKey = ref('')
const inputContent = ref('')
const chatContentRef = ref(null)
const msgLoading = ref(false)
const currentMsgList = ref([])

const userMsgAvatar = computed(() => {
  const info = authUtils.getUserInfo()
  return info?.avatar || '/头像.png'
})

const currentSession = computed(() => {
  if (!activeSessionKey.value) return null
  const [productId, oppositeUserId] = activeSessionKey.value.split('-')
  return chatSessions.value.find(s => 
    s.productId == productId && s.oppositeUserId == oppositeUserId
  )
})

// 获取菜单标题
const getMenuTitle = () => {
  const titles = {
    info: '我的信息',
    cart: '我的购物车',
    order: '我的订单',
    publish: '我的发布',
    message: '我的消息'
  }
  return titles[activeMenu.value] || ''
}

// 切换菜单
const switchMenu = (menu) => {
  activeMenu.value = menu
  if (menu === 'cart') getCartList()
  if (menu === 'order') loadOrders()
  if (menu === 'publish') getPublishList()
  if (menu === 'message') getChatSessionList()
}

const goBack = () => {
  router.back()
}

// 获取头像URL
const getAvatarUrl = (url) => {
  if (!url || typeof url !== 'string') return '/头像.png'
  if (url.startsWith('http')) return `${url}?t=${Date.now()}`
  return `${url}?t=${Date.now()}`
}

const getPublishImageUrl = (url) => {
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

const getCartImageUrl = (url) => {
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

// 获取用户头像
const getUserAvatar = (comment) => {
  const avatar = comment?.buyerAvatar || comment?.avatar
  if (!avatar || avatar === 'null' || avatar === 'undefined') {
    return '/头像.png'
  }
  if (avatar.startsWith('http')) {
    return avatar
  }
  return `http://localhost:8090${avatar}`
}

// 获取用户昵称
const getUserNickname = (comment) => {
  return comment?.buyerNickname || comment?.nickname || '匿名用户'
}

// 获取消息商品图片
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

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.code === 200 && res.data) {
      Object.assign(userInfo, res.data)
      // ✅ 同步更新到 sessionStorage！
      const token = authUtils.getToken()
      authUtils.setLogin(token, res.data)
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
    ElMessage.error('获取用户信息失败')
  }
}

const triggerFileUpload = () => {
  fileInput.value?.click()
}

const handleAvatarChange = (file) => {
  if (!file?.raw) return
  
  if (!file.raw.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  if (file.raw.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  avatarFile.value = file.raw
  
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const saveInfo = async () => {
  saving.value = true
  
  const formData = new FormData()
  formData.append('nickname', userInfo.nickname || '')
  formData.append('phone', userInfo.phone || '')
  formData.append('campus', userInfo.campus || '')
  
  if (avatarFile.value) {
    formData.append('avatar', avatarFile.value)
    console.log('添加了头像文件:', avatarFile.value.name)
  }

  console.log('📤 PersonalCenter 发送的请求数据:')
  for (let [key, value] of formData.entries()) {
    console.log(`  ${key}:`, value)
  }

  try {
    const res = await request.post('/user/update', formData)
    
    console.log('📥 PersonalCenter 后端返回:', res)
    
    if (res.code === 200) {
      ElMessage.success('保存成功！')
      await getUserInfo()
      avatarFile.value = null
      avatarPreview.value = ''
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (err) {
    console.error('保存失败:', err)
    ElMessage.error(err.response?.data?.msg || err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const uploadAvatar = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  const formData = new FormData()
  formData.append('avatar', selectedFile.value)
  formData.append('nickname', userInfo.nickname || '')
  formData.append('phone', userInfo.phone || '')
  formData.append('campus', userInfo.campus || '')

  try {
    const res = await request.post('/user/update', formData)
    
    if (res.code === 200) {
      ElMessage.success('头像更新成功！')
      avatarDialogVisible.value = false
      getUserInfo()
      fileInput.value.value = ''
    } else {
      ElMessage.error(res.msg || '上传失败')
    }
  } catch (err) {
    console.error('上传头像失败:', err)
    ElMessage.error('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

// 购物车功能
// ==================== 购物车核心变量 ====================

// ==================== 监听选中状态，自动更新全选框 ====================
watch(selectedCartIds, (newVal) => {
  if (cartList.value.length === 0) {
    isAllSelected.value = false
    return
  }
  // 只有当所有商品都被选中时，全选框才勾选
  isAllSelected.value = newVal.length === cartList.value.length
}, { deep: true })

// ==================== 全选/取消全选 事件 ====================
const handleAllSelect = () => {
  if (isAllSelected.value) {
    // 全选：把所有商品ID加入选中数组
    selectedCartIds.value = cartList.value.map(item => item.cartId)
  } else {
    // 取消全选：清空选中数组
    selectedCartIds.value = []
  }
}

// ==================== 已选中商品列表（核心计算属性） ====================
const selectedCartList = computed(() => {
  return cartList.value.filter(item => 
    selectedCartIds.value.includes(item.cartId)
  )
})

// ==================== 合计价格（只计算选中商品！） ====================
const totalPrice = computed(() => {
  return selectedCartList.value.reduce((total, item) => {
    return total + (item.price * item.quantity)
  }, 0)
})

// ==================== 获取购物车列表 ====================
const getCartList = async () => {
  if (!authUtils.isLogin()) return

  cartLoading.value = true
  try {
    const response = await request.get('/user/cart/list')
    console.log('🛒 购物车接口返回:', response)
    if (response.code === 200) {
      cartList.value = (response.data || []).map(item => {
        return {
          cartId: item.cartId,
          productId: item.productId,
          productName: item.productName || '未知商品',
          productImage: item.productImage || item.image || '',
          price: item.price || 0,
          quantity: item.quantity || 1
        }
      })
      // 加载完成后，清空选中状态，重置全选
      selectedCartIds.value = []
      isAllSelected.value = false
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
  } finally {
    cartLoading.value = false
  }
}

// ==================== 更新商品数量 ====================
const updateQuantity = async (cartId, newQuantity) => {
  if (newQuantity < 1) return
  try {
    const response = await request.post('/user/cart/update', {
      cartId,
      quantity: newQuantity
    })
    if (response.code === 200) getCartList()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// ==================== 删除购物车商品 ====================
const removeFromCart = async (cartId) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.post('/user/cart/remove', null, {
      params: { cartId }
    })
    if (response.code === 200) {
      ElMessage.success('删除成功')
      // 删除后，从选中数组中移除该商品ID
      selectedCartIds.value = selectedCartIds.value.filter(id => id !== cartId)
      getCartList()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// ==================== 清空购物车 ====================
const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.delete('/user/cart/clear')
    if (response.code === 200) {
      ElMessage.success('清空成功')
      cartList.value = []
      selectedCartIds.value = []
      isAllSelected.value = false
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('清空失败')
  }
}

// ==================== 去结算（只结算选中商品） ====================
const checkout = async () => {
  if (selectedCartIds.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定结算 ${selectedCartIds.value.length} 件商品吗？`,
      '结算确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
    )

    for (const item of selectedCartList.value) {
      const response = await request.post('/user/order/create', {
        productId: item.productId,
        quantity: item.quantity
      })
      if (response.code !== 200) {
        ElMessage.error(`商品《${item.productName}》下单失败: ${response.msg}`)
        return
      }
    }

    ElMessage.success('下单成功')

    try {
      await request.delete('/user/cart/clear')
    } catch (err) {
      console.warn('清空购物车失败，但订单已生成', err)
    }

    cartList.value = []
    selectedCartIds.value = []
    isAllSelected.value = false
    switchMenu('order')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结算失败:', error)
      ElMessage.error('结算失败')
    }
  }
}

// 订单功能
const loadOrders = async () => {
  orderLoading.value = true
  try {
    const url = orderActiveTab.value === 'buyer' 
      ? '/user/order/buyer/list' 
      : '/user/order/seller/list'
    
    console.log('📋 加载订单，URL:', url)
    const res = await request.get(url, {
      params: {
        status: null,
        pageNum: orderPageNum.value,
        pageSize: orderPageSize.value
      }
    })
    
    console.log('📋 订单接口返回:', res)
    if (res.code === 200) {
      orders.value = res.data.records || []
      orderTotal.value = res.data.total || 0
      console.log('📋 订单列表:', orders.value)
    } else {
      ElMessage.error(res.msg || '加载订单失败')
    }
  } catch (err) {
    console.error('加载订单失败:', err)
    ElMessage.error(err.response?.data?.msg || err.message || '加载订单失败')
  } finally {
    orderLoading.value = false
  }
}

const payOrder = async (orderId) => {
  try {
    console.log('💰 支付订单，orderId:', orderId)
    const res = await request.post('/user/order/pay', null, {
      params: { orderId }
    })
    console.log('💰 支付接口返回:', res)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      await loadOrders()
    } else {
      ElMessage.error(res.msg || '支付失败')
    }
  } catch (err) {
    console.error('支付失败:', err)
    ElMessage.error(err.response?.data?.msg || err.message || '支付失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定取消订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    console.log('❌ 取消订单，orderId:', orderId)
    const res = await request.post('/user/order/cancel', null, {
      params: { orderId }
    })
    console.log('❌ 取消订单接口返回:', res)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      await loadOrders()
    } else {
      ElMessage.error(res.msg || '取消失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('取消订单失败:', err)
      ElMessage.error(err.response?.data?.msg || err.message || '取消失败')
    }
  }
}

const viewDetail = async (orderId) => {
  try {
    const res = await request.get('/user/order/detail', {
      params: { orderId }
    })
    if (res.code === 200) {
      orderDetail.value = res.data
      detailDialogVisible.value = true
    }
  } catch (err) {
    console.error('获取订单详情失败:', err)
  }
}

// 判断是否可以评论
const canComment = (order) => {
  console.log('🎯 判断去评价按钮 - order:', order, 'status:', order?.status, 'tab:', orderActiveTab.value, 'hasComment:', order?.hasComment)
  
  const isCommented = order.hasComment === 1 || order.hasComment === true
  const result = order.status === 1 && orderActiveTab.value === 'buyer' && !isCommented
  console.log('🎯 去评价按钮显示:', result)
  return result
}

// 判断是否已评论
const hasCommented = (order) => {
  const isCommented = order.hasComment === 1 || order.hasComment === true
  return order.status === 1 && orderActiveTab.value === 'buyer' && isCommented
}

// 打开评论对话框
const openCommentDialog = (order) => {
  currentCommentOrder.value = order
  commentForm.orderId = order.orderId
  commentForm.productId = order.productId
  commentForm.content = ''
  commentForm.score = 5
  commentDialogVisible.value = true
}

// 提交评论
const submitComment = async () => {
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  if (commentForm.score === 0) {
    ElMessage.warning('请选择评分')
    return
  }
  
  commentSubmitting.value = true
  try {
    const res = await request.post('/user/order/comment/add', {
      orderId: commentForm.orderId,
      productId: commentForm.productId,
      content: commentForm.content,
      score: commentForm.score
    })
    
    if (res.code === 200) {
      ElMessage.success('评价成功！')
      commentDialogVisible.value = false
      
      // 刷新订单列表（后端会自动更新 hasComment 字段）
      await loadOrders()
    } else {
      ElMessage.error(res.msg || '评价失败')
    }
  } catch (err) {
    console.error('评价失败:', err)
    ElMessage.error(err.response?.data?.msg || err.message || '评价失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 查看订单评论
const viewOrderComment = async (order) => {
  try {
    const res = await request.get(`/user/order/comment/list/${order.productId}`)
    if (res.code === 200) {
      const comments = res.data || []
      const orderComment = comments.find(c => c.orderId === order.orderId)
      if (orderComment) {
        currentOrderComment.value = orderComment
      } else {
        currentOrderComment.value = null
      }
    }
  } catch (err) {
    console.error('获取评论失败:', err)
    currentOrderComment.value = null
  }
  viewCommentDialogVisible.value = true
}

const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '已完成', 2: '已取消' }
  return map[status] || '未知状态'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info' }
  return map[status] || 'info'
}

// 发布功能
const getPublishList = async () => {
  publishLoading.value = true
  try {
    const res = await request.get('/user/product/my/list', {
      params: { pageNum: 1, pageSize: 100 }
    })
    if (res.code === 200) {
      products.value = (res.data.records || []).map(item => ({
        ...item,
        productImage: item.imageUrls && item.imageUrls.length > 0 ? item.imageUrls[0] : null
      }))
    }
  } catch (err) {
    console.error('获取发布列表失败:', err)
    ElMessage.error('获取发布列表失败')
  } finally {
    publishLoading.value = false
  }
}

const editProduct = (productId) => {
  router.push({ path: '/publish', query: { id: productId } })
}

const deleteProduct = async (productId) => {
  await ElMessageBox.confirm('确定删除该商品？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await request.post('/user/product/delete', null, {
      params: { productId }
    })
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getPublishList()
    }
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

const getPublishStatusText = (status) => {
  const map = { 1: '在售', 2: '已售出', 3: '已下架' }
  return map[status] || '未知'
}

// 计算是否有未读消息
const hasUnreadMessages = computed(() => {
  return chatSessions.value.some(s => s.unreadCount > 0)
})

// 消息功能
const isActiveSession = (session) => {
  return activeSessionKey.value === `${session.productId}-${session.oppositeUserId}`
}

const formatMsgTime = (timeStr) => {
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

const handleMsgImageError = (e) => {
  e.target.src = '/头像.png'
}

const handleCartImageError = (e) => {
  e.target.src = '/placeholder.png'
}

const getChatSessionList = async () => {
  if (!authUtils.isLogin()) return

  msgLoading.value = true
  try {
    const response = await request.get('/user/message/sessions')
    if (response.code === 200) {
      chatSessions.value = (response.data || []).map(item => ({
        productId: item.productId,
        productName: item.productName || '未知商品',
        productImage: item.productImage || '/placeholder.png',
        oppositeUserId: item.oppositeUserId,
        oppositeNickname: item.oppositeNickname || '匿名用户',
        oppositeAvatar: item.oppositeAvatar || '/头像.png',
        lastMsg: item.lastMsg,
        lastTime: item.lastTime,
        unreadCount: item.unreadCount || 0
      }))
      
      if (chatSessions.value.length > 0 && !activeSessionKey.value) {
        const firstSession = chatSessions.value[0]
        activeSessionKey.value = `${firstSession.productId}-${firstSession.oppositeUserId}`
        loadChatRecord(firstSession)
      }
    }
  } catch (error) {
    console.error('加载会话列表异常:', error)
  } finally {
    msgLoading.value = false
  }
}

const switchChat = (session) => {
  activeSessionKey.value = `${session.productId}-${session.oppositeUserId}`
  loadChatRecord(session)
}

const loadChatRecord = async (session) => {
  try {
    const response = await request.get('/user/message/chat', {
      params: { productId: session.productId, otherUserId: session.oppositeUserId }
    })
    
    if (response.code === 200) {
      const currentUserInfo = authUtils.getUserInfo()
      const currentUserId = currentUserInfo?.userId || currentUserInfo?.id
      
      currentMsgList.value = (response.data || []).map((msg) => {
        // ✅ 修正字段映射
        const messageId = msg.messageId || msg.message_id || msg.id || msg.messageid
        const senderId = msg.senderId || msg.sender_id || msg.sender
        const content = msg.content
        const createdAt = msg.createdAt || msg.created_at || msg.createTime
        const senderNickname = msg.senderNickname || msg.sender_nickname
        const receiverNickname = msg.receiverNickname || msg.receiver_nickname
        
        // ✅ isRead 判断
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
        
        // ✅ 关键：通过 receiverNickname 判断！
        // 如果 receiverNickname == 当前用户昵称 → 是对方发给我们的消息
        // 如果 senderNickname == 当前用户昵称 → 是我们发的消息
        const currentUserNickname = currentUserInfo?.nickname
        let isSelf = false
        
        if (currentUserNickname) {
          if (receiverNickname === currentUserNickname) {
            // 接收者是我 → 对方发的
            isSelf = false
          } else if (senderNickname === currentUserNickname) {
            // 发送者是我 → 我发的
            isSelf = true
          } else {
            //  fallback 用 ID 判断
            isSelf = String(senderId) === String(currentUserId)
          }
        } else {
          // 没有昵称，用 ID 判断
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
      
      console.log('✅ PersonalCenter 聊天记录处理后:', currentMsgList.value)
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

// 标记当前会话已读
const markSessionAsRead = async () => {
  if (!currentSession.value) return
  try {
    const response = await request.post('/user/message/read/batch', null, {
      params: {
        productId: currentSession.value.productId,
        otherUserId: currentSession.value.oppositeUserId
      }
    })
    if (response.code === 200) {
      ElMessage.success('已标记为已读')
      currentSession.value.unreadCount = 0
      // 更新会话列表中的未读数
      const sessionIndex = chatSessions.value.findIndex(s => 
        s.productId === currentSession.value.productId && 
        s.oppositeUserId === currentSession.value.oppositeUserId
      )
      if (sessionIndex !== -1) {
        chatSessions.value[sessionIndex].unreadCount = 0
      }
      // 重新加载聊天记录，更新已读状态
      loadChatRecord(currentSession.value)
    } else {
      ElMessage.error(response.msg || '标记已读失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

// 一键全部已读
const markAllAsRead = async () => {
  try {
    // 逐个标记每个会话已读
    for (const session of chatSessions.value) {
      if (session.unreadCount > 0) {
        await request.post('/user/message/read/batch', null, {
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

  try {
    const response = await request.post('/user/message/send', {
      productId: currentSession.value.productId,
      receiverId: currentSession.value.oppositeUserId,
      content: inputContent.value.trim()
    })
    if (response.code === 200) {
      inputContent.value = ''
      loadChatRecord(currentSession.value)
    }
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

watch(currentMsgList, () => {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}, { deep: true })

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/user/logout')
      authUtils.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (err) {
      console.error('退出失败:', err)
      ElMessage.error('退出失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getUserInfo()
  
  // 检查 URL 参数，自动切换到指定菜单
  const menu = route.query.menu
  const productId = route.query.productId
  const sellerId = route.query.sellerId
  
  if (menu) {
    switchMenu(menu)
    
    if (menu === 'message' && productId && sellerId) {
      nextTick(() => {
        autoOpenChatSession(Number(productId), Number(sellerId))
      })
    }
  } else {
    getCartList()
  }
})

// 自动打开聊天会话
const autoOpenChatSession = async (productId, sellerId) => {
  try {
    await getChatSessionList()
    await nextTick()
    
    const targetSession = chatSessions.value.find(
      s => s.productId === productId && s.oppositeUserId === sellerId
    )
    
    if (targetSession) {
      switchChat(targetSession)
    } else {
      ElMessage.info('未找到相关会话，请从会话列表中选择')
    }
  } catch (err) {
    console.error('自动打开会话失败:', err)
  }
}
</script>

<style scoped>
.personal-center-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.sidebar {
  width: 300px;
  background: #fff;
  border-right: 1px solid #eee;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.user-sidebar-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  text-align: center;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  display: inline-block;
}

.sidebar-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.sidebar-user-info {
  margin-top: 15px;
}

.sidebar-nickname {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.sidebar-meta {
  font-size: 14px;
  color: #666;
  margin: 0 0 4px 0;
}

.sidebar-campus {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 10px;
  font-size: 16px;
}

.menu-item:hover {
  background-color: #f5f7fa;
}

.menu-item.active {
  background: linear-gradient(135deg, #67c23a 0%, #4CAF50 100%);
  color: #fff;
}

.menu-item.active .el-icon {
  color: #fff;
}

.menu-item.logout:hover {
  background-color: #fef0f0;
  color: #f56c6c;
}

.content-area {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.content-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.content-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.content-body {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.panel {
  min-height: 500px;
}

/* 购物车样式 */
.cart-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  gap: 15px;
  padding: 20px;
  background: #f9f9f9;
  font-size: 14px;
  color: #666;
}

.cart-item {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  gap: 15px;
  padding: 20px;
  border-bottom: 1px solid #eee;
  align-items: center;
}

.cart-check {
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f5f5f5;
}

.cart-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.cart-info {
  padding-left: 10px;
}

.cart-title {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.cart-price {
  font-size: 18px;
  font-weight: bold;
  color: #FF5722;
}

.cart-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 18px;
}

.cart-quantity input {
  width: 50px;
  text-align: center;
  border: 1px solid #ddd;
  padding: 5px;
}

.cart-total-item {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.cart-delete {
  text-align: center;
  color: #999;
  cursor: pointer;
}

.cart-delete:hover {
  color: #FF5722;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
}

.cart-footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.cart-total-text {
  font-size: 16px;
}

.cart-total-price {
  font-size: 28px;
  font-weight: bold;
  color: #FF5722;
}

.checkout-btn {
  padding: 15px 50px;
  background: #FF5722;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
}

.checkout-btn:hover {
  background: #F4511E;
}

/* 订单样式 */
.order-filter-tabs {
  margin-bottom: 20px;
  text-align: center;
}

.order-list {
  min-height: 400px;
}

.order-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.product-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.opposite-info {
  font-size: 14px;
  color: #666;
}

.order-actions {
  text-align: right;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
}

.order-detail {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item .label {
  width: 100px;
  color: #666;
  font-size: 14px;
}

.detail-item .value {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.detail-item .value.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

/* 发布样式 */
.product-list {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.loading-tip, .empty-tip {
  text-align: center;
  padding: 40px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 20px;
}

.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.product-price {
  margin: 0 0 4px 0;
  color: #f56c6c;
  font-weight: bold;
}

.product-status {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.product-actions {
  display: flex;
  gap: 10px;
}

/* 消息样式 */
.message-container {
  display: flex;
  height: 600px;
  background: #f5f5f5;
  border-radius: 12px;
  overflow: hidden;
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

.chat-info .product-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-info .opposite-nickname {
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

.header-info .product-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.header-info .opposite-nickname {
  font-size: 12px;
  color: #999;
}

.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #fafafa;
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

/* 消息容器：时间戳 + 气泡 */
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
  border-top-left-radius: 0;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
}

.my-bubble {
  background: #c7edfc;
  color: #333;
  border-radius: 12px;
  border-top-right-radius: 0;
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

/* ✅ 已读/未读状态 */
.read-status {
  font-size: 12px;
  margin-top: 6px;
}

/* 已读 - 绿色 */
.read-status.read {
  color: #67c23a;
}

/* 未读 - 灰色 */
.read-status.unread {
  color: #999;
}

/* 对方消息的状态左对齐 */
.merchant-msg .read-status {
  text-align: left;
}

/* 我的消息的状态右对齐 */
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

/* 我的信息样式 */
.avatar-display {
  margin-bottom: 10px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-empty {
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 50%;
  color: #999;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-preview {
  text-align: center;
}

.avatar-preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
}

/* 查看评论对话框样式 */
.order-comment {
  padding: 10px 0;
}

.order-comment .comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.order-comment .comment-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: #ddd;
}

.order-comment .comment-user {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-comment .user-nickname {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.order-comment .comment-score {
  margin-bottom: 12px;
}

.order-comment .comment-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.no-comment {
  padding: 30px 0;
}
</style>