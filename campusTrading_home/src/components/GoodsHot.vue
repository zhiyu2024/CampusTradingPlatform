<template>
  <div class="section">
    <div class="section-header">
      <h2 class="section-title">🔥 热门推荐</h2>
      <div class="section-more" @click="$router.push('/new')">
        更多商品 →
      </div>
    </div>
    <div class="hot-grid">
      <div
        class="hot-card"
        v-for="goods in hotGoods"
        :key="goods.productId"
        @click="goToDetail(goods.productId)"
      >
        <div class="hot-image">
          <img
            :src="getFirstImage(goods.images || goods.imageUrls)"
            :alt="goods.productName"
            @error="handleImageError"
          />
        </div>
        <div class="hot-info">
          <h3 class="hot-name">{{ goods.productName }}</h3>
          <p class="hot-desc">{{ goods.description }}</p>
          <div class="hot-bottom">
            <span class="price">¥{{ goods.price.toFixed(2) }}</span>
            <div class="view-count">
              <span class="view-icon">👁</span>
              <span class="view-number">{{ safeViewCount(goods) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const hotGoods = ref([])
const loading = ref(false)

const getFirstImage = (images) => {
  if (!images || images.length === 0) return '/placeholder.png'
  return Array.isArray(images) ? images[0] : images
}

const handleImageError = (e) => {
  e.target.src = '/placeholder.png'
}

const safeViewCount = (goods) => {
  const count = goods.viewCount || goods.view_count || 0
  if (isNaN(count) || !isFinite(count)) {
    return 0
  }
  return count
}

const getHotGoods = async () => {
  loading.value = true
  try {
    const response = await request.get('/public/product', {
      params: { pageNum: 1, pageSize: 6 }
    })
    if (response.code === 200) {
      hotGoods.value = (response.data.records || []).map(item => ({
        ...item,
        images: item.images || item.imageUrls || []
      }))
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (productId) => {
  router.push({ path: '/detail', query: { id: productId } })
}

onMounted(() => {
  getHotGoods()
})
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.section-more {
  color: #4CAF50;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s;
}

.section-more:hover {
  color: #388E3C;
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}

.hot-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  gap: 15px;
  padding: 15px;
}

.hot-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.hot-image {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.hot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.hot-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.hot-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0;
}

.hot-bottom .price {
  font-size: 20px;
  font-weight: bold;
  color: #FF5722;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
}

.view-count .view-icon {
  font-size: 18px;
}

.view-count .view-number {
  font-size: 16px;
  font-weight: 500;
}

@media (max-width: 992px) {
  .hot-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hot-grid {
    grid-template-columns: 1fr;
  }
}
</style>
