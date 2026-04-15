<template>
  <div>
    <h2>商品分类管理</h2>
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">添加分类</el-button>
      </div>
      
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        row-key="category_id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="category_name" label="分类名称" min-width="200" />
        <el-table-column prop="sort_order" label="排序" width="100" align="center" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAddChild(row)">
              添加子类
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="category_name">
          <el-input v-model="form.category_name" />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-tree-select
            v-model="form.parent_id"
            :data="categoryOptions"
            :props="{ label: 'category_name', value: 'category_id' }"
            :disabled="disableParentSelect"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort_order">
          <el-input-number v-model="form.sort_order" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '@/api/category'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const disableParentSelect = ref(false)

const form = reactive({
  category_id: null,
  category_name: '',
  parent_id: 0,
  sort_order: 0
})

const formRules = {
  category_name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const categoryOptions = computed(() => {
  return [{ category_id: 0, category_name: '顶级分类' }, ...tableData.value]
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await categoryApi.getAll()
    // 后端返回的是 FwResult，data 在 res.data 里
    const list = res.data || []
    tableData.value = buildTree(list)
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const buildTree = (data, parentId = 0) => {
  return data
    .filter(item => item.parent_id === parentId)
    .map(item => ({
      ...item,
      children: buildTree(data, item.category_id)
    }))
}

const handleAdd = () => {
  dialogTitle.value = '添加分类'
  isEdit.value = false
  disableParentSelect.value = false
  form.parent_id = 0
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '添加子分类'
  isEdit.value = false
  disableParentSelect.value = true
  form.parent_id = row.category_id
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  disableParentSelect.value = false
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      type: 'warning'
    })
    await categoryApi.delete(row.category_id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    category_id: null,
    category_name: '',
    parent_id: 0,
    sort_order: 0
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await categoryApi.update(form.category_id, form)
          ElMessage.success('更新成功')
        } else {
          await categoryApi.create(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>