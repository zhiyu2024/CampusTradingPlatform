<template>
  <div class="exchange-page">
    <div class="container">
      <!-- 标题 -->
      <div class="page-header">
        <h1>🔍 AI智能以物换物匹配</h1>
        <p>轻松交换，校园好物带回家</p>
      </div>

      <!-- 我的换物需求 -->
      <div class="my-exchange-card">
        <h2>📦 我的换物需求</h2>
        <div class="exchange-form">
          <div class="form-group">
            <label>【我出】</label>
            <input v-model="myItem" type="text" placeholder="请输入你要交换的物品" />
          </div>
          <div class="form-group">
            <label>【我想换】</label>
            <input v-model="myWant" type="text" placeholder="请输入你想要的物品" />
          </div>
          <div class="form-actions">
            <button @click="handleSave" class="btn-primary" :disabled="isSaving">
              {{ isSaving ? '保存中...' : '保存/发布' }}
            </button>
            <button @click="handleMatch" class="btn-secondary" :disabled="isMatching">
              {{ isMatching ? 'AI匹配中...' : '✨ 重新匹配' }}
            </button>
          </div>
        </div>
      </div>

      <!-- AI匹配结果 -->
      <div class="match-results" v-if="matchResults.length > 0">
        <h2>✨ AI为你匹配到这些好物</h2>
        <div class="match-list">
          <div v-for="item in matchResults" :key="item.productId" class="match-card">
            <div class="match-header">
              <span class="product-name">📚 {{ item.productName }}</span>
              <span class="seller-name">👤 {{ item.sellerName }}</span>
            </div>
            <div class="match-score">
              <span class="score-label">🎯 匹配度：</span>
              <span class="score-value" :class="{ high: item.matchScore >= 80, medium: item.matchScore >= 60 }">
                {{ item.matchScore }}%
              </span>
            </div>
            <div class="match-reason">
              <span class="reason-label">💡 匹配理由：</span>
              <span class="reason-text">{{ item.matchReason }}</span>
            </div>
            <div class="match-other-want" v-if="item.otherWant">
              <span>对方想换：{{ item.otherWant }}</span>
            </div>
            <div class="match-actions">
              <button @click="handleContact(item)" class="btn-contact">
                📩 联系对方
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="hasLoaded && myExchange">
        <div class="empty-icon">🔎</div>
        <p>还没有匹配到合适的物品</p>
        <p>试试点击「重新匹配」，或者稍后再来看看~</p>
      </div>

      <!-- 我的换物申请历史 -->
      <div class="history-section" v-if="myExchangeHistory.length > 0">
        <h2>📋 我的换物申请</h2>
        <div class="history-list">
          <div v-for="item in myExchangeHistory" :key="item.productId" class="history-card">
            <div class="history-info">
              <span class="history-item">📦 我出：{{ item.productName }}</span>
              <span class="history-want" v-if="item.exchangeWant">🎯 我想换：{{ item.exchangeWant }}</span>
            </div>
            <span class="history-time">{{ formatTime(item.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { authUtils } from '@/utils/auth'

const router = useRouter()

const myItem = ref('')
const myWant = ref('')
const myExchange = ref(null)
const myExchangeHistory = ref([])
const matchResults = ref([])
const isSaving = ref(false)
const isMatching = ref(false)
const hasLoaded = ref(false)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const loadMyExchange = async () => {
  try {
    const res = await request.get('/user/exchange/my')
    if (res.code === 200 && res.data) {
      myExchange.value = res.data
      myItem.value = res.data.productName || ''
      myWant.value = res.data.exchangeWant || ''
    }
  } catch (err) {
    console.error('获取我的换物需求失败:', err)
  } finally {
    hasLoaded.value = true
  }
}

const loadMyExchangeHistory = async () => {
  try {
    const res = await request.get('/user/exchange/my/list')
    if (res.code === 200) {
      myExchangeHistory.value = res.data || []
    }
  } catch (err) {
    console.error('获取换物申请历史失败:', err)
  }
}

const handleSave = async () => {
  if (!myItem.value.trim()) {
    ElMessage.warning('请填写你要交换的物品')
    return
  }

  isSaving.value = true
  try {
    const res = await request.post('/user/exchange/publish', {
      myItem: myItem.value,
      myWant: myWant.value
    })
    if (res.code === 200) {
      myExchange.value = res.data
      ElMessage.success('发布成功！你可以继续发布其他换物需求~')
      await loadMyExchangeHistory()
      await loadMyExchange()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (err) {
    console.error('保存失败:', err)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    isSaving.value = false
  }
}

const handleMatch = async () => {
  if (!myExchange.value) {
    ElMessage.warning('请先保存你的换物需求')
    return
  }

  isMatching.value = true
  matchResults.value = []
  try {
    const res = await request.post('/user/exchange/match')
    if (res.code === 200) {
      matchResults.value = res.data || []
      if (matchResults.value.length > 0) {
        ElMessage.success(`匹配成功！找到 ${matchResults.value.length} 个好物~`)
      } else {
        ElMessage.info('暂未匹配到合适的物品，稍后再试试吧')
      }
    } else {
      ElMessage.error(res.msg || '匹配失败')
    }
  } catch (err) {
    console.error('匹配失败:', err)
    ElMessage.error('匹配失败，请稍后重试')
  } finally {
    isMatching.value = false
  }
}

const handleContact = (item) => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: '/exchange' } })
    return
  }
  
  if (!item.sellerId) {
    ElMessage.error('对方信息不完整')
    return
  }
  
  router.push({
    path: '/news',
    query: {
      menu: 'message',
      productId: item.productId,
      sellerId: item.sellerId,
      productName: item.productName,
      productImage: '/placeholder.png',
      sellerNickname: item.sellerName,
      sellerAvatar: '/头像.png'
    }
  })
}

onMounted(() => {
  loadMyExchange()
  loadMyExchangeHistory()
})
</script>

<style scoped>
.exchange-page {
  padding: 20px 0;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 100%);
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.page-header p {
  color: #7f8c8d;
  font-size: 14px;
}

.my-exchange-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-bottom: 30px;
}

.my-exchange-card h2 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #4CAF50;
}

.exchange-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #555;
  font-weight: 600;
}

.form-group input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76,175,80,0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn-primary,
.btn-secondary,
.btn-contact {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: #4CAF50;
  color: #fff;
}

.btn-primary:hover {
  background: #45a049;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  background: #a5d6a7;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #2196F3;
  color: #fff;
}

.btn-secondary:hover {
  background: #1976D2;
  transform: translateY(-1px);
}

.btn-secondary:disabled {
  background: #90CAF9;
  cursor: not-allowed;
  transform: none;
}

.match-results h2 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 20px;
}

.match-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.match-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border-left: 4px solid #4CAF50;
  transition: all 0.3s;
}

.match-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.12);
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.product-name {
  font-size: 17px;
  font-weight: 600;
  color: #2c3e50;
}

.seller-name {
  font-size: 14px;
  color: #7f8c8d;
}

.match-score {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.score-label {
  font-size: 14px;
  color: #555;
}

.score-value {
  font-size: 20px;
  font-weight: 700;
}

.score-value.high {
  color: #4CAF50;
}

.score-value.medium {
  color: #FF9800;
}

.match-reason {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  line-height: 1.6;
}

.reason-label {
  font-size: 14px;
  color: #555;
  font-weight: 600;
  flex-shrink: 0;
}

.reason-text {
  font-size: 14px;
  color: #666;
}

.match-other-want {
  font-size: 13px;
  color: #888;
  margin-bottom: 16px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 6px;
}

.match-actions {
  margin-top: 12px;
}

.btn-contact {
  background: #FF5722;
  color: #fff;
  width: 100%;
}

.btn-contact:hover {
  background: #E64A19;
  transform: translateY(-1px);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #7f8c8d;
  font-size: 15px;
  margin: 8px 0;
}

.history-section {
  margin-top: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.history-section h2 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #2196F3;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f9ff;
  border-radius: 8px;
  border-left: 4px solid #2196F3;
}

.history-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.history-item {
  font-size: 15px;
  color: #2c3e50;
  font-weight: 600;
}

.history-want {
  font-size: 13px;
  color: #7f8c8d;
}

.history-time {
  font-size: 12px;
  color: #999;
}
</style>
