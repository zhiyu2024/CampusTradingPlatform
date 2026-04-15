<template>
  <div>
    <div class="category-nav">
      <button
        :class="['category-item', { active: activeCategory === null }]"
        @click="switchCategory(null)"
      >
        全部商品
      </button>
      <button
        v-for="category in categories"
        :key="category.categoryId"
        :class="['category-item', { active: activeCategory === category.categoryId }]"
        @click="switchCategory(category.categoryId)"
      >
        {{ category.categoryName }}
      </button>
    </div>
    
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">校园二手交易市场</h2>
        <SearchCom />
      </div>
      
      <!-- ✅ 筛选条件：登录后显示，调用搜索接口 -->
      <div class="filter-options" v-if="isLogin" style="margin-bottom: 20px;">
        <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
          <input type="checkbox" v-model="filters.hasDiscount" @change="handleFilterChange" />
          只看有折扣
        </label>
        <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
          <input type="checkbox" v-model="filters.isBargainable" @change="handleFilterChange" />
          可砍价
        </label>
      </div>
      <div v-else style="margin-bottom: 20px;">
        <el-alert title="筛选功能需要登录后使用" type="warning" :closable="false" show-icon>
          <template #default>
            <el-link type="primary" @click="goLogin">立即登录</el-link>
          </template>
        </el-alert>
      </div>
      
      <div class="loading-tip" v-if="loading" style="text-align: center; padding: 40px;">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>
      
      <div class="empty-tip" v-else-if="goodsList.length === 0">
        <el-empty description="暂无商品" />
      </div>
      
      <div v-else class="product-grid">
        <div
          class="product-card"
          v-for="goods in goodsList"
          :key="goods.productId"
          @click="goToDetail(goods.productId)"
        >
          <div class="product-badge" v-if="goods.discountRate < 1 || goods.isBargainable">
            <span v-if="goods.discountRate < 1" style="background: #FF5722; color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 12px; margin-right: 5px;">
              {{ (goods.discountRate * 10).toFixed(1) }}折
            </span>
            <span v-if="goods.isBargainable" style="background: #4CAF50; color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 12px;">
              可砍价
            </span>
          </div>
          <div class="product-image">
            <img
              :src="getFirstImage(goods.images || goods.imageUrls)"
              :alt="goods.productName"
              @error="handleImageError"
            />
          </div>
          <div class="product-info-card">
            <h3 class="product-name">{{ goods.productName }}</h3>
            <p class="product-desc">{{ goods.description }}</p>
            <div class="product-price-card">
              <div class="price-left">
                <span class="price">¥{{ goods.price.toFixed(2) }}</span>
                <span v-if="goods.discountRate < 1" class="original">
                  ¥{{ (goods.price / goods.discountRate).toFixed(2) }}
                </span>
              </div>
              <div class="view-count">
                <span class="view-icon">👁</span>
                <span class="view-number">{{ safeViewCount(goods) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="pagination-wrapper" v-if="total > 0" style="display: flex; justify-content: center; margin-top: 40px;">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[16, 32, 48, 64]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import SearchCom from './SearchCom.vue'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const categories = ref([])
const goodsList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(16)
const activeCategory = ref(null)
const filters = ref({
  hasDiscount: false,
  isBargainable: false
})

const isLogin = computed(() => authUtils.isLogin())

// ✅ 统一图片处理函数
const getFirstImage = (images) => {
  if (!images || images.length === 0) {
    return '/placeholder.png'
  }
  
  // 兼容不同字段名
  if (Array.isArray(images)) {
    return images[0]
  }
  return images
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

const goLogin = () => {
  router.push({ 
    path: '/login', 
    query: { redirect: route.fullPath } 
  })
}

const getCategories = async () => {
  try {
    categories.value = [
      { categoryId: 1, categoryName: '教材资料' },
      { categoryId: 2, categoryName: '电子产品' },
      { categoryId: 3, categoryName: '生活用品' },
      { categoryId: 4, categoryName: '乐器运动' },
      { categoryId: 5, categoryName: '考研资料' },
      { categoryId: 6, categoryName: '手机平板' }
    ]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// ✅ ✅ ✅ 核心修复：根据登录状态调用不同接口
const getGoodsList = async () => {
  loading.value = true
  
  try {
    // ✅ 游客状态：调用公开接口
    // ✅ 登录状态：调用用户接口（带筛选）
    const url = isLogin.value 
      ? '/user/product/search'   // 登录用户接口
      : '/public/product'        // 游客接口
    
    console.log(`GoodsNew - 登录状态: ${isLogin.value}, 调用接口: ${url}`)
    
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: activeCategory.value
    }
    
    // ✅ 只有登录用户才传筛选参数
    if (isLogin.value) {
      params.hasDiscount = filters.value.hasDiscount ? true : null
      params.isBargainable = filters.value.isBargainable ? true : null
    }

    const response = await request.get(url, { params })
    
    console.log('GoodsNew - 接口返回:', response)

    if (response.code === 200) {
      const data = response.data
      goodsList.value = (data.records || []).map(item => ({
        ...item,
        images: item.images || item.imageUrls || []
      }))
      total.value = data.total || 0
    } else {
      ElMessage.error(response.msg || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    if (error.response?.status === 401) {
      ElMessage.warning('请先登录')
      router.push({ path: '/login', query: { redirect: route.fullPath } })
    } else {
      ElMessage.error('获取商品列表失败')
    }
  } finally {
    loading.value = false
  }
}

// ✅ 筛选条件变化：只有登录用户才调用
const handleFilterChange = () => {
  if (!isLogin.value) {
    ElMessage.warning('筛选功能需要登录')
    filters.value = { hasDiscount: false, isBargainable: false }
    goLogin()
    return
  }
  
  pageNum.value = 1
  getGoodsList()
}

const switchCategory = (categoryId) => {
  activeCategory.value = categoryId
  pageNum.value = 1
  getGoodsList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  getGoodsList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  getGoodsList()
}

const goToDetail = (productId) => {
  router.push({ path: '/detail', query: { id: productId } })
}

onMounted(() => {
  getCategories()
  getGoodsList()
})

watch(() => route.query, (newQuery) => {
  if (newQuery.category) {
    activeCategory.value = Number(newQuery.category)
    getGoodsList()
  }
}, { immediate: true })
</script>

<style scoped>
.category-nav {
  display: flex;
  gap: 15px;
  overflow-x: auto;
  padding: 15px 0;
  background: #fff;
  margin-bottom: 20px;
}

.category-item {
  flex-shrink: 0;
  padding: 10px 25px;
  background: #f5f5f5;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background: #e8f5e9;
  color: #4CAF50;
}

.category-item.active {
  background: #4CAF50;
  color: #fff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.product-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.product-image {
  width: 100%;
  height: 220px;
  background: #f5f5f5;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info-card {
  padding: 15px;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #999;
  line-height: 1.5;
  margin-bottom: 12px;
  height: 39px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-left {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-left .price {
  font-size: 20px;
  font-weight: bold;
  color: #FF5722;
}

.price-left .original {
  font-size: 13px;
  color: #999;
  text-decoration: line-through;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #929292;
}

.view-count .view-icon {
  font-size: 24px;
  line-height: 20px ;
  height: 20px;
   /* font-weight: 300; */
     margin-right:3px ;
}

.view-count .view-number {
  font-size: 15px;
  font-weight: 370;
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>