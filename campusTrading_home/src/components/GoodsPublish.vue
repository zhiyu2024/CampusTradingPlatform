<template>
  <div class="publish-container">
    <div class="auth-container">
      <div class="auth-header">
        <div class="auth-logo">🛒 校园交易平台</div>
        <h2 class="auth-title">{{ isEdit ? '编辑商品' : '发布商品' }}</h2>
      </div>
      
      <div v-if="submitError" style="color: #f56c6c; text-align: center; margin-bottom: 20px; padding: 10px; background: #fef0f0; border-radius: 8px;">
        {{ submitError }}
      </div>
      
      <form class="auth-form publish-form" @submit.prevent="handlePublish">
        <!-- 商品名称 -->
        <div class="form-group">
          <label class="form-label">商品名称 <span style="color: #f56c6c;">*</span></label>
          <input v-model="form.productName" type="text" class="form-input" placeholder="请输入商品名称（2-30个字）" @blur="validateField('productName')" />
          <span v-if="errors.productName" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.productName }}</span>
        </div>

        <!-- 商品分类 -->
        <div class="form-group">
          <label class="form-label">商品分类 <span style="color: #f56c6c;">*</span></label>
          <select v-model.number="form.categoryId" class="form-input" style="width: 100%; max-width: 400px; padding: 12px 15px;" @change="validateField('categoryId')">
            <option value="">请选择分类</option>
            <option :value="1">图书教材</option>
            <option :value="2">电子产品</option>
            <option :value="3">生活用品</option>
            <option :value="4">服饰鞋帽</option>
            <option :value="5">其他</option>
          </select>
          <span v-if="errors.categoryId" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.categoryId }}</span>
        </div>

        <!-- 商品价格 -->
        <div class="form-group">
          <label class="form-label">商品价格（元） <span style="color: #f56c6c;">*</span></label>
          <input v-model.number="form.price" type="number" step="0.01" min="0.01" class="form-input" placeholder="请输入商品价格（≥0.01）" @blur="validateField('price')" />
          <span v-if="errors.price" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.price }}</span>
        </div>

        <!-- 库存数量 -->
        <div class="form-group">
          <label class="form-label">库存数量 <span style="color: #f56c6c;">*</span></label>
          <input v-model.number="form.stockQuantity" type="number" min="1" class="form-input" placeholder="请输入库存数量（≥1）" @blur="validateField('stockQuantity')" />
          <span v-if="errors.stockQuantity" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.stockQuantity }}</span>
        </div>

        <!-- 商品描述 -->
        <div class="form-group">
          <label class="form-label">商品描述 <span style="color: #f56c6c;">*</span></label>
          <div style="margin-bottom: 10px;">
            <button 
              type="button" 
              class="ai-btn" 
              @click="handleGenerateDesc"
              :disabled="isGenerating"
              style="padding: 8px 16px; background: #4CAF50; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 14px;"
            >
              <span v-if="!isGenerating">✨ AI优化描述</span>
              <span v-else>⏳ 生成中...</span>
            </button>
            <span style="font-size: 12px; color: #999; margin-left: 10px;">（需先填写商品名称）</span>
          </div>
          <textarea v-model="form.description" class="form-input" rows="5" placeholder="请详细描述商品信息（10-500个字）" style="resize: vertical; min-height: 120px;" @blur="validateField('description')"></textarea>
          <span v-if="errors.description" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.description }}</span>
        </div>

        <!-- 是否可砍价 -->
        <div class="form-group">
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
            <input type="checkbox" v-model="form.isBargainable" /> 
            支持砍价
          </label>
        </div>

        <!-- 折扣率（可选） -->
        <div class="form-group">
          <label class="form-label">折扣率（如0.9表示9折，不填默认为1）</label>
          <input v-model.number="form.discountRate" type="number" step="0.01" min="0.1" max="1" class="form-input" placeholder="请输入0.1-1之间的数字" />
        </div>

        <!-- 商品图片 -->
        <div class="form-group">
          <label class="form-label">商品图片 <span style="color: #f56c6c;">*</span></label>
          <div class="form-upload">
            <label class="upload-btn" style="display: inline-block; padding: 12px 30px; background: #4CAF50; color: #fff; border-radius: 8px; cursor: pointer;">
              <input type="file" accept="image/*" multiple @change="handleFileChange" hidden />
              选择图片
            </label>
            <span style="font-size: 13px; color: #999;">支持jpg/png格式，最多上传3张</span>
          </div>
          
          <div class="preview-list" style="display: flex; gap: 15px; flex-wrap: wrap; margin-top: 15px;">
            <div v-for="(img, index) in previewImages" :key="index" class="preview-item" style="position: relative; width: 120px; height: 120px; border-radius: 8px; overflow: hidden;">
              <img :src="img" alt="商品图片预览" style="width: 100%; height: 100%; object-fit: cover;" />
              <button type="button" class="preview-delete" @click="deleteImage(index)" style="position: absolute; top: 5px; right: 5px; width: 24px; height: 24px; background: rgba(0,0,0,0.5); color: #fff; border: none; border-radius: 50%; cursor: pointer; font-size: 18px;">×</button>
            </div>
          </div>
          <span v-if="errors.imageUrl" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">{{ errors.imageUrl }}</span>
        </div>

        <button type="submit" class="form-btn" :disabled="isSubmitting">
          <span v-if="!isSubmitting">{{ isEdit ? '更新商品' : '发布商品' }}</span>
          <span v-if="isSubmitting">{{ isEdit ? '更新中...' : '发布中...' }}</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const isEdit = ref(false)
const editProductId = ref(null)

// 表单数据（严格匹配后端ProductDto）
const form = reactive({
  productId: null,
  productName: '',
  categoryId: '',
  price: '',
  stockQuantity: '',
  description: '',
  isBargainable: false,
  discountRate: 1,
  imageUrl: [],
  existingImages: []  // 存储已有图片URL
})

const previewImages = ref([])
const errors = reactive({
  productName: '',
  categoryId: '',
  price: '',
  stockQuantity: '',
  description: '',
  imageUrl: ''
})
const submitError = ref('')
const isSubmitting = ref(false)
const isGenerating = ref(false)

// 获取商品详情用于编辑
const getProductDetail = async (productId) => {
  try {
    const res = await request.get('/public/product/detail', {
      params: { productId }
    })
    
    if (res.code === 200) {
      const data = res.data
      form.productId = data.productId
      form.productName = data.productName || ''
      form.categoryId = data.categoryId || ''
      form.price = data.price || ''
      form.description = data.description || ''
      form.isBargainable = data.isBargainable === 1 || data.isBargainable === true
      form.discountRate = data.discountRate || 1
      form.stockQuantity = data.stock || 1
      
      if (data.images && data.images.length > 0) {
        previewImages.value = data.images
        form.existingImages = [...data.images]
      }
    } else {
      ElMessage.error(res.msg || '获取商品详情失败')
    }
  } catch (err) {
    console.error('获取商品详情失败:', err)
    ElMessage.error('获取商品详情失败')
  }
}

// 验证规则
const handleGenerateDesc = async () => {
  if (!form.productName.trim()) {
    ElMessage.warning('请先填写商品名称')
    return
  }

  isGenerating.value = true
  try {
    const res = await request.post('/user/product/generate-desc', null, {
      params: { productName: form.productName }
    })
    if (res.code === 200 && res.data) {
      form.description = res.data
      ElMessage.success('描述生成成功！')
    } else {
      ElMessage.error(res.msg || '生成描述失败')
    }
  } catch (err) {
    console.error('生成描述失败:', err)
    ElMessage.error('生成描述失败，请稍后重试')
  } finally {
    isGenerating.value = false
  }
}

const validateField = (field) => {
  switch (field) {
    case 'productName':
      if (!form.productName.trim()) {
        errors.productName = '商品名称不能为空'
      } else if (form.productName.length < 2 || form.productName.length > 30) {
        errors.productName = '商品名称长度需在2-30个字之间'
      } else {
        errors.productName = ''
      }
      break
    case 'categoryId':
      if (!form.categoryId) {
        errors.categoryId = '请选择商品分类'
      } else {
        errors.categoryId = ''
      }
      break
    case 'price':
      if (!form.price && form.price !== 0) {
        errors.price = '商品价格不能为空'
      } else if (form.price < 0.01) {
        errors.price = '商品价格不能低于0.01元'
      } else {
        errors.price = ''
      }
      break
    case 'stockQuantity':
      if (!form.stockQuantity && form.stockQuantity !== 0) {
        errors.stockQuantity = '库存数量不能为空'
      } else if (!Number.isInteger(form.stockQuantity) || form.stockQuantity < 1) {
        errors.stockQuantity = '库存数量必须是大于0的整数'
      } else {
        errors.stockQuantity = ''
      }
      break
    case 'description':
      if (!form.description.trim()) {
        errors.description = '商品描述不能为空'
      } else if (form.description.length < 10 || form.description.length > 500) {
        errors.description = '商品描述长度需在10-500个字之间'
      } else {
        errors.description = ''
      }
      break
    case 'imageUrl':{
      const totalImages = form.imageUrl.length + form.existingImages.length
      if (totalImages === 0) {
        errors.imageUrl = '请至少上传一张商品图片'
      } else if (totalImages > 3) {
        errors.imageUrl = '最多只能上传3张商品图片'
      } else {
        errors.imageUrl = ''
      }
      break
  }
 }
}

// 表单整体验证
const validateForm = () => {
  Object.keys(errors).forEach(key => validateField(key))
  return !Object.values(errors).some(msg => msg)
}

// 处理文件选择
const handleFileChange = (e) => {
  const files = e.target.files
  if (!files.length) return

  const totalImages = form.imageUrl.length + form.existingImages.length
  const maxCount = 3 - totalImages
  const validFiles = Array.from(files).slice(0, maxCount)

  validFiles.forEach(file => {
    if (!file.type.startsWith('image/')) {
      submitError.value = '请选择图片格式的文件（jpg/png）'
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      submitError.value = '单张图片大小不能超过5MB'
      return
    }

    form.imageUrl.push(file)
    const reader = new FileReader()
    reader.onload = (e) => previewImages.value.push(e.target.result)
    reader.readAsDataURL(file)
  })

  e.target.value = ''
  validateField('imageUrl')
}

// 删除图片
const deleteImage = (index) => {
  if (index < form.existingImages.length) {
    form.existingImages.splice(index, 1)
  } else {
    const newIndex = index - form.existingImages.length
    form.imageUrl.splice(newIndex, 1)
  }
  previewImages.value.splice(index, 1)
  validateField('imageUrl')
}

// 发布/更新商品
const handlePublish = async () => {
  // 1. 验证登录
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录后再发布商品')
    router.push({ path: '/login', query: { redirect: '/publish' } })
    return
  }

  // 2. 验证表单
  if (!validateForm()) return

  // 3. 构建FormData（严格匹配后端字段名）
  const formData = new FormData()
  
  if (isEdit.value) {
    formData.append('productId', form.productId)
  }
  formData.append('productName', form.productName)
  formData.append('categoryId', form.categoryId)
  formData.append('price', form.price)
  formData.append('description', form.description)
  formData.append('isBargainable', form.isBargainable ? 1 : 0)
  formData.append('discountRate', form.discountRate || 1)
  
  // 上传新图片文件（如果有）
  form.imageUrl.forEach((file) => {
    formData.append('imageUrl', file)
  })

  isSubmitting.value = true
  submitError.value = ''

  try {
    const url = isEdit.value ? '/user/product/update' : '/user/product/add'
    const successMsg = isEdit.value ? '商品更新成功！' : '商品发布成功！'

    const res = await request.post(url, formData)

    if (res.code === 200) {
      ElMessage.success(successMsg)
      // 重置表单
      Object.assign(form, {
        productId: null,
        productName: '',
        categoryId: '',
        price: '',
        stockQuantity: '',
        description: '',
        isBargainable: false,
        discountRate: 1,
        imageUrl: [],
        existingImages: []
      })
      previewImages.value = []
      isEdit.value = false
      editProductId.value = null
      router.push('/')
    } else {
      submitError.value = res.msg || (isEdit.value ? '商品更新失败' : '商品发布失败')
    }
  } catch (err) {
    console.error(isEdit.value ? '更新商品失败：' : '发布商品失败：', err)
    submitError.value = err.response?.data?.msg || err.message || '网络错误'
  } finally {
    isSubmitting.value = false
  }
}

// 初始化
onMounted(() => {
  const productId = route.query.id
  if (productId) {
    isEdit.value = true
    editProductId.value = productId
    getProductDetail(productId)
  }
})
</script>

<style scoped>
.publish-container {
  padding-top: 30px;
  padding-bottom: 30px;
}

.auth-form .form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 15px;
  color: #333;
  margin-bottom: 10px;
}

.auth-form .form-input,
.auth-form select {
  width: 100%;
  max-width: 400px;
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 15px;
}

.auth-form .form-input:focus,
.auth-form select:focus {
  outline: none;
  border-color: #4CAF50;
}

.form-upload {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.preview-delete:hover {
  background: #FF5722;
}
</style>