<template>
  <div>
    <h2>订单管理</h2>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="searchForm.order_no"
          placeholder="搜索订单号"
          style="width: 200px; margin-right: 10px"
          clearable
        />
        <el-select
          v-model="searchForm.status"
          placeholder="订单状态"
          style="width: 130px; margin-right: 10px"
          clearable
        >
          <el-option label="待付款" :value="0" />
          <el-option label="已完成" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="order_id" label="ID" width="80" />
        <el-table-column prop="order_no" label="订单号" min-width="180" />
        <el-table-column prop="product_name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="订单金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.total_amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'info'">
              {{ row.status === 0 ? '待付款' : row.status === 1 ? '已完成' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="买家留言" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.buyer_note || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, sizes"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.order_no }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="currentOrder.status === 0 ? 'warning' : currentOrder.status === 1 ? 'success' : 'info'">
            {{ currentOrder.status === 0 ? '待付款' : currentOrder.status === 1 ? '已完成' : '已取消' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder.product_name }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span style="color: #f56c6c; font-weight: bold">¥{{ currentOrder.total_amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="买家">{{ currentOrder.buyer_nickname }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ currentOrder.seller_nickname }}</el-descriptions-item>
        <el-descriptions-item label="买家留言" :span="2">
          {{ currentOrder.buyer_note || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.created_at }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentOrder.updated_at }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  order_no: '',
  status: ''
})

const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const { data } = await orderApi.getList(params)
    tableData.value = data.records || data.list
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    order_no: '',
    status: ''
  })
  handleSearch()
}

const handleDetail = (row) => {
  currentOrder.value = { ...row }
  detailDialogVisible.value = true
}

const handleUpdateStatus = async (row, status) => {
  const statusText = status === 1 ? '完成' : '取消'
  try {
    await ElMessageBox.confirm(`/>确定要将订单${statusText}吗？`, '提示', {
      type: 'warning'
    })
    await orderApi.updateStatus(row.order_id, status)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  page.value = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>