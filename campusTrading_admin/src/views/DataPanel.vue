<template>
  <div>
    <h2>数据面板</h2>

    <el-row :gutter="20" class="dashboard-stats">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon size="40" color="#409EFF"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon size="40" color="#67C23A"><Goods /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalProducts }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon size="40" color="#E6A23C"><ShoppingCart /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOrders }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon size="40" color="#F56C6C"><DataLine /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ formatAmount(stats.totalTransactionAmount) }}</div>
              <div class="stat-label">总交易额(元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>用户增长趋势</span>
          </template>
          <div ref="userChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>商品分类统计</span>
          </template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div ref="orderChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>销售额趋势</span>
          </template>
          <div ref="revenueChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Goods, ShoppingCart, DataLine } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const stats = reactive({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalTransactionAmount: 0
})

const userChartRef = ref()
const categoryChartRef = ref()
const orderChartRef = ref()
const revenueChartRef = ref()

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

const loadStats = async () => {
  try {
    const res = await userApi.getDashboardStats()
    const dashboardData = res.data
    stats.totalUsers = dashboardData.total_users || 0
    stats.totalProducts = dashboardData.total_products || 0
    stats.totalOrders = dashboardData.total_orders || 0
    stats.totalTransactionAmount = dashboardData.total_transaction_amount || 0
  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  }
}

const loadDataBoard = async () => {
  try {
    const res = await userApi.getDashboardData()
    const data = res.data
    initUserChart(data.user_growth_list)
    initCategoryChart(data.category_stat_list)
    initOrderChart(data.order_status_stat_list)
    initRevenueChart(data.sales_trend_list)
  } catch (error) {
    console.error('加载数据分析数据失败', error)
    ElMessage.error('加载数据分析数据失败')
  }
}

const initUserChart = (dataList) => {
  if (!userChartRef.value) return
  const chart = echarts.init(userChartRef.value)
  const xData = dataList?.map(item => item.month) || ['1月', '2月', '3月', '4月', '5月', '6月']
  const yData = dataList?.map(item => Number(item.user_count)) || [820, 932, 901, 934, 1290, 1330]
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value' },
    series: [{
      data: yData,
      type: 'line',
      smooth: true,
      itemStyle: { color: '#409EFF' }
    }]
  }
  chart.setOption(option)
}

const initCategoryChart = (dataList) => {
  if (!categoryChartRef.value) return
  const chart = echarts.init(categoryChartRef.value)
  const data = dataList?.map(item => ({ name: item.category_name, value: Number(item.product_count) })) || [
    { value: 1048, name: '教材资料' },
    { value: 735, name: '电子产品' },
    { value: 580, name: '生活用品' },
    { value: 484, name: '运动乐器' }
  ]
  const option = {
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: '50%', data: data }]
  }
  chart.setOption(option)
}

const initOrderChart = (dataList) => {
  if (!orderChartRef.value) return
  const chart = echarts.init(orderChartRef.value)
  const data = dataList?.map(item => ({ name: item.status_name, value: Number(item.order_count) })) || [
    { value: 335, name: '待付款' },
    { value: 310, name: '已完成' },
    { value: 234, name: '已取消' }
  ]
  const option = {
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: ['40%', '70%'], data: data }]
  }
  chart.setOption(option)
}

const initRevenueChart = (dataList) => {
  if (!revenueChartRef.value) return
  const chart = echarts.init(revenueChartRef.value)
  const xData = dataList?.map(item => item.month) || ['1月', '2月', '3月', '4月', '5月', '6月']
  const yData = dataList?.map(item => Number(item.sales_amount)) || [150, 230, 224, 218, 135, 147, 260]
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value' },
    series: [{
      data: yData,
      type: 'bar',
      itemStyle: { color: '#67C23A' }
    }]
  }
  chart.setOption(option)
}

onMounted(() => {
  loadStats()
  loadDataBoard()
})
</script>

<style scoped>
.dashboard-stats {
  margin-top: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-card {
  height: 400px;
}

.chart {
  width: 100%;
  height: 350px;
}
</style>
