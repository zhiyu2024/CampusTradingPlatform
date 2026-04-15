<template>
  <div class="product-detail" v-if="!loading && goods.productId">
    <!-- 顶部导航 -->
    <div class="product-nav">
      <div class="product-nav-item" @click="$router.back()">← 返回</div>
      <div class="product-nav-title">商品详情</div>
      <div class="product-nav-item" @click="toggleCollect">
        {{ isCollected ? '★ 已收藏' : '☆ 收藏' }}
      </div>
    </div>

    <!-- 商品主体 -->
    <div class="product-main">
      <!-- 图片展示 -->
      <div class="product-gallery">
        <!-- 主图轮播 -->
        <div class="main-image-carousel">
          <el-carousel 
            v-model:current-index="currentImageIndex"
            :interval="3000" 
            :autoplay="true"
            arrow="hover"
            height="400px"
            @change="handleCarouselChange"
          >
            <el-carousel-item v-for="(img, index) in goods.images" :key="index">
              <img :src="getImageUrl(img)" :alt="`${goods.productName} - ${index + 1}`" class="carousel-image" />
            </el-carousel-item>
          </el-carousel>
          <div v-if="!goods.images?.length" class="placeholder-image">
            <img src="/placeholder.png" alt="暂无图片" />
          </div>
        </div>
        
        <!-- 缩略图 -->
        <div class="thumbnails" v-if="goods.images?.length > 0">
          <div 
            v-for="(img, index) in goods.images" 
            :key="index" 
            class="thumbnail" 
            :class="{ active: index === currentImageIndex }"
            @click="switchToImage(index)"
          >
            <img :src="getImageUrl(img)" alt="缩略图" />
          </div>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="product-info">
        <h1 class="product-title">{{ goods.productName }}</h1>
        
        <div class="product-price">
          <span class="price">¥{{ goods.price.toFixed(2) }}</span>
          <span v-if="goods.discountRate < 1" class="original">原价：¥{{ (goods.price / goods.discountRate).toFixed(2) }}</span>
        </div>

        <div class="product-meta">
          <div class="meta-item">
            <span>库存：</span>
            <span :class="goods.stock > 0 ? 'in-stock' : 'out-of-stock'">
              {{ goods.stock > 0 ? `${goods.stock}件` : '已售罄' }}
            </span>
          </div>
          <div v-if="goods.isBargainable" class="meta-item">
            <span style="color: #FF5722; font-weight: bold;">支持砍价</span>
          </div>
        </div>

        <div class="product-description">
          <h3>商品介绍</h3>
          <p>{{ goods.description }}</p>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section" v-if="seller">
          <div class="seller-avatar">
            <img :src="seller.avatar || '/头像.png'" :alt="seller.nickname" />
          </div>
          <div class="seller-details">
            <div class="seller-name">{{ seller.nickname }}</div>
            <div v-if="seller.campus" class="seller-campus">{{ seller.campus }}</div>
          </div>
          <button class="contact-btn" @click="contactSeller">联系卖家</button>
        </div>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <div class="section-header">
        <h3>商品评价 ({{ comments.length }})</h3>
        <el-button type="text" @click="viewAllComments" v-if="comments.length > 0">
          查看全部 →
        </el-button>
      </div>

      <div v-if="comments.length === 0" class="empty-comments">
        <el-empty description="暂无评价" />
      </div>

      <div v-else class="comments-list">
        <div v-for="comment in displayComments" :key="comment.commentId" class="comment-item">
          <div class="comment-header">
            <img :src="getUserAvatar(comment)" class="comment-avatar" />
            <div class="comment-user">
              <span class="user-nickname">{{ getUserNickname(comment) }}</span>
              <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
            </div>
          </div>
          <div class="comment-score">
            <el-rate v-model="comment.score" disabled show-score />
          </div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="product-actions">
      <button class="action-btn add-cart" :disabled="goods.stock <= 0" @click="addToCart">
        加入购物车
      </button>
      <button class="action-btn buy-now" :disabled="goods.stock <= 0" @click="buyNow">
        立即购买
      </button>
    </div>
  </div>

  <!-- 加载状态 -->
  <div class="loading-container" v-else>
    <el-skeleton :rows="10" animated v-if="loading" />
    <el-empty v-else description="商品不存在或已下架" />
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/request'
import { authUtils } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const goods = ref({})
const seller = ref(null)
const isCollected = ref(false)
const comments = ref([])
const currentImageIndex = ref(0)

// 只显示前3条评论
const displayComments = computed(() => {
  return comments.value.slice(0, 3)
})

// 获取图片URL
const getImageUrl = (url) => {
  if (!url || typeof url !== 'string') return '/placeholder.png'
  if (url.startsWith('http')) return url
  if (url.startsWith('/res/images/')) {
    return `http://localhost:8090${url}`
  }
  if (url.startsWith('/images/')) {
    return `http://localhost:8090${url}`
  }
  return `http://localhost:8090/res/images/${url}`
}

// 切换到指定图片
const switchToImage = (index) => {
  currentImageIndex.value = index
}

// 监听轮播变化，同步索引（双向绑定保障）
const handleCarouselChange = (index) => {
  currentImageIndex.value = index
}

// 获取商品详情
const getGoodsDetail = async () => {
  const productId = route.query.id
  if (!productId) {
    ElMessage.error('商品ID不存在')
    return
  }

  loading.value = true
  try {
    const response = await axios.get('/public/product/detail', {
      params: { productId }
    })
    
    if (response.code === 200) {
      const data = response.data
      
      let imagesArray = []
      if (data.images && Array.isArray(data.images)) {
        imagesArray = data.images
      } else if (data.imageList && Array.isArray(data.imageList)) {
        imagesArray = data.imageList
      } else if (data.productImage) {
        imagesArray = [data.productImage]
      }
      
      goods.value = {
        ...data,
        images: imagesArray,
        productName: data.productName || '',
        price: data.price || 0,
        discountRate: data.discountRate || 1,
        isBargainable: data.isBargainable || false,
        stock: data.stock || 0,
        sellerId: data.sellerId || data.userId
      }
      
      seller.value = {
        nickname: data.sellerNickname || '匿名用户',
        avatar: data.sellerAvatar || '/头像.png',
        campus: data.sellerCampus || ''
      }
      
      currentImageIndex.value = 0
      
      await getComments(productId)
    } else {
      ElMessage.error(response.msg || '获取商品详情失败')
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

// 获取评论列表
const getComments = async (productId) => {
  try {
    const res = await axios.get(`/user/order/comment/list/${productId}`)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (err) {
    console.error('获取评论失败:', err)
  }
}

// 查看全部评论
const viewAllComments = () => {
  router.push({
    path: '/comments',
    query: { productId: goods.value.productId }
  })
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

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 加入购物车（需要登录）
const addToCart = async () => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }

  try {
    const response = await axios.post('/user/cart/add', null, {
      params: { productId: goods.value.productId }
    })
    
    if (response.code === 200) {
      ElMessage.success('加入购物车成功')
    } else {
      ElMessage.error(response.msg || '加入购物车失败')
    }
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

// 立即购买（需要登录）
const buyNow = async () => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }

  ElMessageBox.confirm('确定要购买该商品吗？', '购买确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      const response = await axios.post('/user/order/create', {
        productId: goods.value.productId,
        quantity: 1,
      })
      
      if (response.code === 200) {
        ElMessage.success('下单成功')
        router.push({ path: '/personal-center', query: { menu: 'order' } })
      } else {
        ElMessage.error(response.msg || '下单失败')
      }
    } catch (error) {
      ElMessage.error('下单失败')
    }
  }).catch(() => {
    ElMessage.info('已取消购买')
  })
}

// 收藏/取消收藏（占位实现，可根据后端接口完善）
const toggleCollect = () => {
  isCollected.value = !isCollected.value
  ElMessage.success(isCollected.value ? '收藏成功' : '已取消收藏')
}

// 联系卖家
const contactSeller = () => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  
  if (!goods.value.sellerId) {
    ElMessage.error('卖家信息不完整')
    return
  }
  
  const productImage = goods.value.images && goods.value.images.length > 0 
    ? goods.value.images[0] 
    : goods.value.productImage

  router.push({
    path: '/news',
    query: {
      menu: 'message',
      productId: goods.value.productId,
      sellerId: goods.value.sellerId,
      productName: goods.value.productName,
      productImage: productImage,
      sellerNickname: seller.value?.nickname,
      sellerAvatar: seller.value?.avatar
    }
  })
}

onMounted(() => {
  getGoodsDetail()
})
</script>


<style scoped>
/* 整体布局 */
.product-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 100px;
}

/* 顶部导航 */
.product-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.product-nav-item {
  color: #4CAF50;
  cursor: pointer;
  font-size: 14px;
}

.product-nav-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* 主体布局：左侧图片 + 右侧信息 */
.product-main {
  display: grid;
  grid-template-columns: 55% 45%;
  gap: 30px;
  margin-top: 20px;
  align-items: start;
}

/* 图片区域：核心修复！缩小gap，消除空白 */
.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 8px; /* 从15px改为8px，消除主图和缩略图之间的大片空白 */
}

/* 主图轮播：放大主图，消除左右灰色留白 */
.main-image-carousel {
  width: 100%;
  /* height: 520px; 从480px拉高，放大主图 */
  background: #f5f5f5;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 改为cover，占满容器，消除左右灰色留白 */
  background: #f5f5f5;
}

.placeholder-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-image img {
  max-width: 50%;
  max-height: 50%;
}

/* 缩略图：紧贴主图，无空白 */
.thumbnails {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 0 0 5px 0; /* 移除上下padding，紧贴主图 */
  width: 100%;
  box-sizing: border-box;
}

.thumbnail {
  flex-shrink: 0;
  width: 72px;
  height: 72px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail:hover {
  border-color: #ccc;
}

.thumbnail.active {
  border-color: #4CAF50;
  transform: scale(1.05);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 轮播箭头样式：缩小不占空间 */
:deep(.el-carousel__arrow) {
  width: 36px !important;
  height: 36px !important;
  border-radius: 50% !important;
  background: rgba(0, 0, 0, 0.25) !important;
}

:deep(.el-carousel__arrow--left) {
  left: 10px !important;
}

:deep(.el-carousel__arrow--right) {
  right: 10px !important;
}

:deep(.el-carousel__arrow i) {
  font-size: 18px !important;
}

/* 商品信息 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 28px;
  color: #333;
  font-weight: bold;
  line-height: 1.4;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.product-price .price {
  font-size: 36px;
  font-weight: bold;
  color: #FF5722;
}

.product-price .original {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 15px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.meta-item {
  font-size: 14px;
  color: #666;
}

.in-stock {
  color: #4CAF50;
  font-weight: 500;
}

.out-of-stock {
  color: #999;
  font-weight: 500;
}

.product-description h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
}

.product-description p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

/* 卖家信息 */
.seller-section {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
}

.seller-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  background: #ddd;
}

.seller-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.seller-campus {
  font-size: 13px;
  color: #999;
  margin-top: 5px;
}

.contact-btn {
  padding: 10px 25px;
  background: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.contact-btn:hover {
  background: #45a049;
}

/* 底部操作栏 */
.product-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 15px;
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #eee;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 99;
}

.action-btn {
  flex: 1;
  padding: 15px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.add-cart {
  background: #FF9800;
  color: #fff;
}

.add-cart:hover:not(:disabled) {
  background: #F57C00;
}

.buy-now {
  background: #FF5722;
  color: #fff;
}

.buy-now:hover:not(:disabled) {
  background: #F4511E;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 评论区 */
.comments-section {
  padding: 30px 20px;
  background: #fff;
  margin-top: 20px;
  border-radius: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.section-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.empty-comments {
  padding: 40px 0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.comment-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ddd;
}

.comment-user {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-nickname {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-score {
  margin-bottom: 10px;
}

.comment-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

/* 加载 */
.loading-container {
  padding: 40px 20px;
  text-align: center;
}
</style>