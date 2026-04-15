<template>
  <div class="my-order-page">
    <h2>我的订单</h2>
    
    <!-- 订单状态筛选 -->
    <div class="filter-tabs">
      <el-radio-group v-model="activeTab" @change="handleTabChange">
        <el-radio-button label="buyer">我买到的</el-radio-button>
        <el-radio-button label="seller">我卖出的</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 订单列表 -->
    <div class="order-list">
      <div v-if="loading" class="loading">加载中...</div>
      
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
              <p>{{ activeTab === 'buyer' ? '卖家' : '买家' }}：{{ order.oppositeName }}</p>
            </div>
          </div>
          
          <div class="order-actions">
            <el-button v-if="order.status === 0" size="small" type="primary" @click="payOrder(order.orderId)">
              去支付
            </el-button>
            <el-button v-if="order.status === 1 && activeTab === 'buyer'" size="small" type="success" @click="confirmReceipt(order.orderId)">
              确认收货
            </el-button>
            <el-button v-if="order.status === 0" size="small" @click="cancelOrder(order.orderId)">
              取消订单
            </el-button>
            <el-button size="small" @click="viewDetail(order.orderId)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadOrders"
      class="pagination"
    />
    
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()

// 状态
const activeTab = ref('buyer')
const orders = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const orderDetail = ref(null)

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const url = activeTab.value === 'buyer' 
      ? '/user/order/buyer/list' 
      : '/user/order/seller/list'
    
    const res = await request.get(url, {
      params: {
        status: null,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
     // ✅ 加这行日志，看后端返回的 status 到底是什么
    console.log('后端返回的订单列表:', res.data.records)

    if (res.code === 200) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
      
      // 如果有 orderId 参数，自动打开订单详情
      if (route.query.orderId) {
        viewDetail(route.query.orderId)
      }
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (err) {
    console.error('加载订单失败:', err)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 切换标签
const handleTabChange = () => {
  pageNum.value = 1
  loadOrders()
}

// 支付订单
const payOrder = async (orderId) => {
  try {
    const res = await request.post('/user/order/pay', null, {
      params: { orderId }
    })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      loadOrders()
    }
  } catch (err) {
    ElMessage.error('支付失败')
  }
}

// 确认收货
const confirmReceipt = async (orderId) => {
  await ElMessageBox.confirm('确认已收到商品？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await request.post('/user/order/confirm', null, {
      params: { orderId }
    })
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      loadOrders()
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// 取消订单
const cancelOrder = async (orderId) => {
  await ElMessageBox.confirm('确定取消订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await request.post('/user/order/cancel', null, {
      params: { orderId }
    })
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    }
  } catch (err) {
    ElMessage.error('取消失败')
  }
}

// 查看详情
const viewDetail = async (orderId) => {
  try {
    const res = await request.get('/user/order/detail', {
      params: { orderId }
    })
    if (res.code === 200) {
      orderDetail.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取订单详情失败')
    }
  } catch (err) {
    console.error('获取订单详情失败:', err)
    ElMessage.error('获取订单详情失败')
  }
}

// 状态文本
const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '已付款', 2: '已完成', 3: '已取消' }
  return map[status] || '未知状态'
}

// 状态标签类型
const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

// 初始化
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.my-order-page {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.filter-tabs {
  margin-bottom: 20px;
  text-align: center;
}

.order-list {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 40px;
}

.empty {
  text-align: center;
  padding: 40px;
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
</style>