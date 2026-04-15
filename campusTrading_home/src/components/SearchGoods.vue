<template>
  <div class="search-results-container">
    <header class="search-header">
      <h2>搜索结果</h2>
      <div class="search-info" v-if="keyword">
        搜索关键词：<span class="keyword">{{ keyword }}</span>
        <span class="result-count">（共 {{ total }} 条结果）</span>
      </div>
    </header>

    <!-- 筛选条件（需要登录） -->
    <div class="filter-bar" v-if="isLogin">
      <el-checkbox v-model="filters.hasDiscount" @change="handleSearch">只看有折扣</el-checkbox>
      <el-checkbox v-model="filters.isBargainable" @change="handleSearch">可砍价</el-checkbox>
    </div>

    <!-- 未登录提示 -->
    <el-alert v-else title="搜索功能需要登录" type="warning" :closable="false">
      <el-button type="text" @click="$router.push('/login')">去登录</el-button>
    </el-alert>

    <!-- ✅ 商品列表：改为4x4网格布局 -->
    <main class="goods-list">
      <div class="loading-tip" v-if="loading">
        <i class="el-icon-loading"></i> 搜索中...
      </div>

      <div class="empty-tip" v-else-if="goodsList.length === 0">
        <el-empty description="没有找到相关商品" />
      </div>

      <!-- ✅ 使用与GoodsNew相同的布局 -->
      <div
        class="goods-item"
        v-for="goods in goodsList"
        :key="goods.productId"
        @click="goToDetail(goods.productId)"
      >
        <img :src="getFirstImage(goods.images)" class="goods-img" @error="handleImageError"/>
        
        <div class="goods-tags">
          <el-tag v-if="goods.discountRate < 1" type="danger" size="small">
            {{ (goods.discountRate * 10).toFixed(1) }}折
          </el-tag>
          <el-tag v-if="goods.isBargainable" type="success" size="small">
            可砍价
          </el-tag>
        </div>
        
        <div class="goods-info">
          <h3 class="goods-name">{{ goods.productName }}</h3>
          <p class="goods-desc">{{ goods.description }}</p>
          <div class="goods-price-wrapper">
            <span class="goods-price">¥{{ goods.price.toFixed(2) }}</span>
            <span v-if="goods.discountRate < 1" class="original-price">
              ¥{{ (goods.price / goods.discountRate).toFixed(2) }}
            </span>
          </div>
        </div>
      </div>
    </main>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0 && isLogin">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[16, 32, 48]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/request'
import { authUtils } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const keyword = computed(() => route.query.keyword || '')
const isLogin = computed(() => authUtils.isLogin())
const goodsList = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(16)

const filters = ref({
  hasDiscount: false,
  isBargainable: false
})

// ✅ 获取第一张图片
const getFirstImage = (images) => {
  console.log('SearchGoods - 处理图片:', images)
  if (!images || images.length === 0) {
    return '/placeholder.png'
  }
  return images[0]
}

// ✅ 图片加载失败处理
const handleImageError = (e) => {
  console.log('SearchGoods - 图片加载失败:', e.target.src)
  e.target.src = '/placeholder.png'
}

const handleSearch = async () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }

  if (!keyword.value) return

  loading.value = true
  console.log('SearchGoods - 开始搜索，关键词:', keyword.value) // 调试
  
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      hasDiscount: filters.value.hasDiscount || null,
      isBargainable: filters.value.isBargainable || null
    }

    const response = await axios.get('/user/product/search', { params })
    console.log('SearchGoods - 搜索结果:', response) // 调试
    
    if (response.code === 200) {
      const data = response.data
      goodsList.value = (data.records || []).map(item => ({
        ...item,
        images: item.imageUrls || [], // 注意后端返回的字段名
        productName: item.productName || '',
        price: item.price || 0,
        discountRate: item.discountRate || 1,
        isBargainable: item.isBargainable || false,
        productId: item.productId
      }))
      total.value = data.total || 0
      console.log('SearchGoods - 处理后的商品列表:', goodsList.value) // 调试
    } else {
      ElMessage.error(response.msg || '搜索失败')
    }
  } catch (error) {
    console.error('SearchGoods - 搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (productId) => {
  console.log('SearchGoods - 跳转到详情:', productId)
  router.push({ path: '/detail', query: { id: productId } })
}

// 样式与GoodsNew.vue完全相同
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  handleSearch()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  handleSearch()
}

watch(() => route.query.keyword, () => {
  pageNum.value = 1
  if (isLogin.value) {
    handleSearch()
  }
})

onMounted(() => {
  console.log('SearchGoods - 组件挂载，登录状态:', isLogin.value)
  if (isLogin.value && keyword.value) {
    handleSearch()
  }
})
</script>

<style scoped>
/* ✅ 与GoodsNew.vue完全一致的样式 */
.search-results-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.search-header {
  margin-bottom: 30px;
  padding: 0 20px;
}

.keyword {
  color: #f56c6c;
  font-weight: bold;
}

.filter-bar {
  margin-bottom: 20px;
  padding: 10px 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

/* ✅ 商品列表4x4布局 */
.goods-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 0 20px;
  margin-top: 20px;
}

/* ✅ 响应式处理 */
@media (max-width: 1200px) {
  .goods-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .goods-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .goods-list {
    grid-template-columns: 1fr;
    padding: 0 10px;
  }
}

/* ✅ 商品卡片样式 */
.goods-item {
  position: relative;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.3s;
  background: #fff;
}

.goods-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.goods-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.goods-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 5px;
}

.goods-info {
  padding: 15px;
}

.goods-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.goods-price-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.goods-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 0 20px;
}
</style>