<template>
  <div>
    <h2>商品管理</h2>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="searchForm.product_name"
          placeholder="搜索商品名称"
          style="width: 200px; margin-right: 10px"
          clearable
        />
        <el-select
          v-model="searchForm.status"
          placeholder="商品状态"
          style="width: 130px; margin-right: 10px"
          clearable
        >
          <el-option label="在售" :value="1" />
          <el-option label="已售出" :value="2" />
          <el-option label="下架" :value="3" />
        </el-select>
        <el-select
          v-model="searchForm.audit_status"
          placeholder="审核状态"
          style="width: 130px; margin-right: 10px"
          clearable
        >
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="疑似违规" :value="2" />
          <el-option label="已拒绝" :value="3" />
        </el-select>
        <el-select
          v-model="searchForm.category_id"
          placeholder="商品分类"
          style="width: 150px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="item in categories"
            :key="item.category_id"
            :label="item.category_name"
            :value="item.category_id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="product_id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.images?.length"
              :src="row.images[0].image_url"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <el-avatar v-else :size="60" icon="Picture" />
          </template>
        </el-table-column>
        <el-table-column prop="product_name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category_name" label="分类" width="120" />
        <el-table-column prop="seller_nickname" label="卖家" width="120" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <div style="color: #f56c6c; font-weight: bold">
              ¥{{ row.price }}
              <span v-if="row.discount_rate < 1" style="color: #909399; text-decoration: line-through; font-size: 12px">
                ¥{{ (row.price / row.discount_rate).toFixed(2) }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.audit_status)">
              {{ getAuditStatusText(row.audit_status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'warning'">
              {{ row.status === 1 ? '在售' : row.status === 2 ? '已售出' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button link type="success" @click="handleAudit(row)">
              审核
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
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="商品详情"
      width="800px"
    >
      <div v-if="currentProduct" class="product-detail">
        <div class="detail-header">
          <div v-if="currentProduct.images?.length" class="product-images">
            <el-image
              v-for="(img, index) in currentProduct.images"
              :key="index"
              :src="img"
              style="width: 100px; height: 100px; margin-right: 10px"
              fit="cover"
            />
          </div>
        </div>
        
        <el-divider />
        
        <div class="detail-item">
          <span class="label">商品名称：</span>
          <span>{{ currentProduct.productName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商品描述：</span>
          <span>{{ currentProduct.description || '无' }}</span>
        </div>
        <div class="detail-row">
          <div class="detail-item">
            <span class="label">价格：</span>
            <span style="color: #f56c6c; font-weight: bold">¥{{ currentProduct.price }}</span>
          </div>
          <div class="detail-item" v-if="currentProduct.discountRate < 1">
            <span class="label">折扣：</span>
            <span>{{ (currentProduct.discountRate * 10).toFixed(1) }}折</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-item">
            <span class="label">库存：</span>
            <span>{{ currentProduct.stock }}</span>
          </div>
          <div class="detail-item">
            <span class="label">是否可砍价：</span>
            <span>{{ currentProduct.isBargainable ? '是' : '否' }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-item">
            <span class="label">分类：</span>
            <span>{{ currentProduct.categoryName || '无' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">卖家：</span>
            <span>{{ currentProduct.sellerNickname || '匿名' }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-item">
            <span class="label">校区：</span>
            <span>{{ currentProduct.sellerCampus || '无' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">浏览量：</span>
            <span>{{ currentProduct.viewCount || 0 }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-item">
            <span class="label">创建时间：</span>
            <span>{{ currentProduct.createdAt || '无' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">更新时间：</span>
            <span>{{ currentProduct.updatedAt || '无' }}</span>
          </div>
        </div>
        <div class="detail-item">
          <span class="label">审核状态：</span>
          <el-tag :type="getAuditStatusType(currentProduct.auditStatus)">
            {{ getAuditStatusText(currentProduct.auditStatus) }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="currentProduct.auditResult">
          <span class="label">审核结果：</span>
          <span>{{ currentProduct.auditResult }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="商品审核"
      width="600px"
    >
      <div v-if="currentProduct" class="audit-content">
        <div class="product-info">
          <h4>商品信息</h4>
          <p><strong>商品名称：</strong>{{ currentProduct.productName }}</p>
          <p><strong>商品描述：</strong>{{ currentProduct.description || '无' }}</p>
          <p><strong>卖家：</strong>{{ currentProduct.sellerNickname || '匿名' }}</p>
        </div>
        
        <div class="audit-result" v-if="currentProduct.auditStatus">
          <h4>AI 审核结果</h4>
          <el-tag :type="getAuditStatusType(currentProduct.auditStatus)">
            {{ getAuditStatusText(currentProduct.auditStatus) }}
          </el-tag>
          <p v-if="currentProduct.auditResult" style="margin-top: 10px; color: #666">
            {{ currentProduct.auditResult }}
          </p>
        </div>
        
        <el-divider />
        
        <el-form :model="auditForm" label-width="100px">
          <el-form-item label="审核结果">
            <el-radio-group v-model="auditForm.auditStatus">
              <el-radio :label="1">通过</el-radio>
              <el-radio :label="2">疑似违规</el-radio>
              <el-radio :label="3">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核原因">
            <el-input
              v-model="auditForm.auditResult"
              type="textarea"
              :rows="3"
              placeholder="请输入审核原因（通过时可选填）"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="handleAiAudit" :loading="aiAuditLoading">
          AI 审核
        </el-button>
        <el-button type="primary" @click="handleAuditSubmit" :loading="auditLoading">
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/product'
import { categoryApi } from '@/api/category'

const loading = ref(false)
const tableData = ref([])
const categories = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  product_name: '',
  status: '',
  category_id: '',
  audit_status: ''
})

const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const currentProduct = ref(null)

const auditForm = reactive({
  auditStatus: 1,
  auditResult: ''
})

const aiAuditLoading = ref(false)
const auditLoading = ref(false)

const getAuditStatusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '疑似违规'
    case 3: return '已拒绝'
    default: return '待审核'
  }
}

const getAuditStatusType = (status) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

const loadCategories = async () => {
  try {
    const { data } = await categoryApi.getAll()
    const buildTree = (list, parentId = 0) => {
      return list
        .filter(item => item.parent_id === parentId)
        .map(item => ({
          ...item,
          children: buildTree(list, item.category_id)
        }))
    }
    categories.value = buildTree(data)
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const { data } = await productApi.getList(params)
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
    product_name: '',
    status: '',
    category_id: '',
    audit_status: ''
  })
  handleSearch()
}

const handleViewDetail = async (row) => {
  try {
    const { data } = await productApi.getDetail(row.product_id)
    currentProduct.value = data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleAudit = async (row) => {
  try {
    const { data } = await productApi.getDetail(row.product_id)
    currentProduct.value = data
    auditForm.auditStatus = data.auditStatus || 1
    auditForm.auditResult = data.auditResult || ''
    auditDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取商品信息失败')
  }
}

const handleAiAudit = async () => {
  if (!currentProduct.value) return
  
  aiAuditLoading.value = true
  try {
    const res = await productApi.aiAuditProduct(currentProduct.value.product_id)
    const data = res.data
    currentProduct.value = data
    auditForm.auditStatus = data.audit_status !== undefined ? data.audit_status : 1
    auditForm.auditResult = data.audit_result || ''
    
    // 根据审核结果显示不同的消息
    if (data.audit_result && data.audit_result.includes('审核失败')) {
      ElMessage.error('AI 审核失败：' + data.audit_result)
    } else if (data.audit_status === 0) {
      ElMessage.warning('AI 审核异常，请稍后重试或手动审核')
    } else {
      ElMessage.success('AI 审核完成：' + (data.audit_result || ''))
    }
  } catch (error) {
    // 错误信息已经在请求拦截器中显示
    console.error('AI 审核失败：', error)
  } finally {
    aiAuditLoading.value = false
  }
}

const handleAuditSubmit = async () => {
  if (!currentProduct.value) return
  
  auditLoading.value = true
  try {
    await productApi.auditProduct(
      currentProduct.value.product_id,
      auditForm.auditStatus,
      auditForm.auditResult
    )
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
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
  loadCategories()
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

.product-detail {
  padding: 10px;
}

.detail-header {
  margin-bottom: 15px;
}

.product-images {
  display: flex;
}

.detail-row {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
}

.detail-item {
  margin-bottom: 15px;
  flex: 1;
}

.detail-item .label {
  font-weight: bold;
  color: #666;
}

.audit-content {
  padding: 10px;
}

.product-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.product-info h4 {
  margin-top: 0;
  margin-bottom: 10px;
}

.audit-result {
  margin-top: 20px;
  padding: 15px;
  background: #fff0f0;
  border-radius: 4px;
}

.audit-result h4 {
  margin-top: 0;
  margin-bottom: 10px;
}
</style>
