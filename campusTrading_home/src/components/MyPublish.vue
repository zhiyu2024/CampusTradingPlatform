<template>
  <div class="my-publish-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-button @click="goBack" circle>
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>我的发布</h2>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div v-if="loading" class="loading-tip">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>

      <div v-else-if="products.length === 0" class="empty-tip">
        <el-empty description="暂无发布商品" />
      </div>

      <div v-else v-for="product in products" :key="product.productId" class="product-item">
        <img :src="getImageUrl(product.productImage)" alt="商品图片" class="product-img" />
        <div class="product-info">
          <h3 class="product-name">{{ product.productName }}</h3>
          <p class="product-price">¥{{ product.price }}</p>
          <p class="product-status">{{ getStatusText(product.status) }}</p>
        </div>
        <div class="product-actions">
          <el-button size="small" @click="editProduct(product.productId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteProduct(product.productId)">删除</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const products = ref([])

// ✅ 添加 goBack 方法
const goBack = () => {
  router.back()
}

// 获取发布的商品列表
const getPublishList = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/product/my/list', {
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    
    if (res.code === 200) {
      products.value = (res.data.records || []).map(item => ({
        ...item,
        productImage: item.imageUrls && item.imageUrls.length > 0 ? item.imageUrls[0] : null
      }))
    } else {
      ElMessage.error(res.msg || '获取失败')
    }
  } catch (err) {
    console.error('获取发布列表失败:', err)
    ElMessage.error('获取失败')
  } finally {
    loading.value = false
  }
}

// 获取图片 URL（修复相对路径问题）
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

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    1: '在售',
    2: '已售出',
    3: '已下架'
  }
  return map[status] || '未知'
}

// 编辑商品
const editProduct = (productId) => {
  router.push({ path: '/publish', query: { id: productId } })
}

// 删除商品
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

// 初始化
onMounted(() => {
  getPublishList()
})
</script>

<style scoped>
.my-publish-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.page-header h2 {
  margin: 0;
}

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
</style>