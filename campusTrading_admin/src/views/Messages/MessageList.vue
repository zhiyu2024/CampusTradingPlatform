<template>
  <div>
    <h2>消息管理</h2>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="searchForm.content"
          placeholder="搜索消息内容"
          style="width: 200px; margin-right: 10px"
          clearable
        />
        <el-select
          v-model="searchForm.message_type"
          placeholder="消息类型"
          style="width: 130px; margin-right: 10px"
          clearable
        >
          <el-option label="普通留言" :value="1" />
          <el-option label="砍价请求" :value="2" />
          <el-option label="回复" :value="3" />
          <el-option label="系统通知" :value="4" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="message_id" label="ID" width="80" />
        <el-table-column prop="product_name" label="相关商品" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sender_nickname" label="发送者" width="120" />
        <el-table-column prop="receiver_nickname" label="接收者" width="120" />
        <el-table-column prop="content" label="消息内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="消息类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMessageTypeTag(row.message_type)">
              {{ getMessageTypeText(row.message_type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否已读" width="80">
          <template #default="{ row }">
            <el-tag :type="row.is_read === 1 ? 'success' : 'warning'">
              {{ row.is_read === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="发送时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDelete(row)">
              删除
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { messageApi } from '@/api/message'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  content: '',
  message_type: ''
})

const getMessageTypeText = (type) => {
  const types = {
    1: '普通留言',
    2: '砍价请求',
    3: '回复',
    4: '系统通知'
  }
  return types[type] || '未知'
}

const getMessageTypeTag = (type) => {
  const tags = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger'
  }
  return tags[type] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await messageApi.getList({
      page: page.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    
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
    content: '',
    message_type: ''
  })
  handleSearch()
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
      type: 'warning'
    })
    await messageApi.delete(row.message_id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
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