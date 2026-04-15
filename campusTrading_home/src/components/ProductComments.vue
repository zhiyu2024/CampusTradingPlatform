<template>
  <div class="comments-page">
    <!-- 顶部导航 -->
    <div class="page-nav">
      <div class="nav-item" @click="$router.back()">← 返回</div>
      <div class="nav-title">商品评价</div>
      <div class="nav-item"></div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="activeFilter" @change="filterComments">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="positive">好评</el-radio-button>
        <el-radio-button label="neutral">中评</el-radio-button>
        <el-radio-button label="negative">差评</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 评论列表 -->
    <div class="comments-container">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="filteredComments.length === 0" class="empty-state">
        <el-empty description="暂无评价" />
      </div>

      <div v-else class="comments-list">
        <div v-for="comment in filteredComments" :key="comment.commentId" class="comment-card">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()

const loading = ref(false)
const comments = ref([])
const activeFilter = ref('all')

// 获取评论列表
const getComments = async (productId, sentimentParam = null) => {
  loading.value = true
  try {
    const params = sentimentParam ? { sentiment: sentimentParam } : {}
    const res = await request.get(`/user/order/comment/list/${productId}`, { params })
    if (res.code === 200) {
      comments.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取评论失败')
    }
  } catch (err) {
    console.error('获取评论失败:', err)
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
  }
}

// 筛选评论
const filteredComments = computed(() => {
  return comments.value
})

const filterComments = () => {
  const productId = route.query.productId
  if (!productId) return
  
  let sentimentParam = null
  if (activeFilter.value === 'positive') {
    sentimentParam = 1
  } else if (activeFilter.value === 'neutral') {
    sentimentParam = 3
  } else if (activeFilter.value === 'negative') {
    sentimentParam = 2
  }
  
  getComments(productId, sentimentParam)
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

onMounted(() => {
  const productId = route.query.productId
  if (productId) {
    getComments(productId)
  }
})
</script>

<style scoped>
.comments-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20px;
}

.page-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-item {
  color: #4CAF50;
  cursor: pointer;
  font-size: 14px;
  width: 60px;
}

.nav-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.filter-tabs {
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: center;
}

.comments-container {
  padding: 15px 20px;
}

.loading-state,
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: #ddd;
}

.comment-user {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-nickname {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-score {
  margin-bottom: 12px;
}

.comment-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}
</style>
