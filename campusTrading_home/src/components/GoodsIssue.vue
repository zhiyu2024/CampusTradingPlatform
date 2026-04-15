<template>
  <div class="publish-goods-page">
    <div class="container">
      <h2 class="page-title">发布二手商品</h2>

      <!-- 基础信息表单 -->
      <div class="form-section">
        <h3 class="section-title">基础信息</h3>
        <el-form
          ref="goodsFormRef"
          :model="goodsForm"
          :rules="formRules"
          label-width="100px"
          class="base-form"
        >
          <!-- 商品名称 -->
          <el-form-item label="商品名称" prop="name">
            <el-input
              v-model="goodsForm.name"
              maxlength="100"
              placeholder="如：高等数学教材（第七版）九成新"
              show-word-limit
            />
          </el-form-item>

          <!-- 商品分类（二级联动） -->
          <el-form-item label="商品分类" prop="categoryId">
            <el-cascader
              v-model="goodsForm.categoryId"
              :options="categoryOptions"
              placeholder="选择商品分类"
              @change="handleCategoryChange"
              :props="{ label: 'category_name', value: 'category_id', children: 'children' }"
            />
          </el-form-item>

          <!-- 分类热门标签 -->
          <div v-if="hotTags.length > 0" class="hot-tags">
            <span class="tag-label">该分类热门标签：</span>
            <el-tag
              v-for="tag in hotTags"
              :key="tag"
              size="small"
              type="info"
              @click="addTagToDesc(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>

          <!-- 商品价格 -->
          <el-form-item label="商品价格" prop="price">
            <el-input
              v-model="goodsForm.price"
              type="number"
              placeholder="单位：元，如 25.50"
              @input="formatPrice"
            />
            <div class="price-tip">输入后自动保留2位小数，如输入255将转为25.50</div>
          </el-form-item>

          <!-- 库存数量 -->
          <el-form-item label="库存数量" prop="stock">
            <el-input-number
              v-model="goodsForm.stock"
              :min="1"
              placeholder="默认 1 件，多件可修改"
              controls-position="right"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品描述表单 -->
      <div class="form-section">
        <h3 class="section-title">商品描述</h3>
        <el-form-item
          label="商品描述"
          prop="description"
          class="desc-form-item"
        >
          <el-input
            v-model="goodsForm.description"
            type="textarea"
            maxlength="500"
            placeholder="描述商品成色、细节、交易方式（如 “支持校园面交”）"
            show-word-limit
            rows="6"
          />
          <!-- 常用描述模板 -->
          <div class="template-group">
            <span class="template-label">常用模板：</span>
            <el-button
              v-for="template in descTemplates"
              :key="template.key"
              size="small"
              type="text"
              @click="fillDescTemplate(template.content)"
            >
              {{ template.name }}
            </el-button>
          </div>
        </el-form-item>
      </div>

      <!-- 图片上传模块 -->
      <div class="form-section">
        <h3 class="section-title">商品图片</h3>
        <div class="upload-section">
          <!-- 上传区域 -->
          <div class="upload-area">
            <el-upload
              ref="uploadRef"
              drag
              action="/api/upload/image"
              :headers="{ 'Content-Type': 'multipart/form-data' }"
              :data="{ type: 'product' }"
              :limit="9"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemoveImage"
              :on-exceed="handleExceed"
              :file-list="fileList"
              list-type="picture-card"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">
                点击或拖拽文件到此处上传<br>
                <span class="upload-tip">最多上传 9 张图片，首图为封面</span>
              </div>
              <div class="compress-tip">省流量模式：自动压缩图片</div>
            </el-upload>
          </div>

          <!-- 预览区（标注封面） -->
          <div v-if="fileList.length > 0" class="preview-area">
            <div class="preview-title">
              预览区
              <el-button
                size="small"
                type="text"
                @click="sortImages"
                v-if="fileList.length > 1"
              >
                调整排序
              </el-button>
            </div>
            <div class="preview-list">
              <div
                v-for="(file, index) in fileList"
                :key="file.uid"
                class="preview-item"
              >
                <img :src="file.url" alt="商品图片" class="preview-img" />
                <div v-if="index === 0" class="cover-tag">封面图</div>
                <el-button
                  size="mini"
                  type="text"
                  @click="handleRemoveImage(file)"
                  class="delete-btn"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部按钮组 -->
      <div class="btn-group">
        <el-button
          type="default"
          @click="handleCancel"
          class="cancel-btn"
        >
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :disabled="!isFormValid"
          class="submit-btn"
        >
          提交
        </el-button>
      </div>
    </div>
  </div>
  <router-link ></router-link>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

// 路由实例
const router = useRouter()
const elRef = ref(null)
// 表单引用
const goodsFormRef = ref(null)
const uploadRef = ref(null)

// 分类数据（模拟接口返回，实际替换为GET /categories/tree）
const categoryOptions = ref([])
// 分类热门标签映射
const categoryHotTags = {
  7: ['含答案', '无笔记', '真题卷', '考点总结'], // 考研资料
  8: ['无涂鸦', '附习题册', '教师用书', '全新未拆'], // 课本教材
  5: ['无拆修', '原装配件', '保修期内', '九成新'], // 电脑数码
  6: ['电池健康90%+', '无划痕', '全网通', '原装充电器'] // 手机平板
}
const hotTags = ref([])

// 常用描述模板
const descTemplates = ref([
  { key: 1, name: '九成新面交', content: '九成新，无笔记，无破损，支持本校校区面交' },
  { key: 2, name: '全新包邮', content: '全新未拆封，支持邮寄（邮费自理），可小刀' },
  { key: 3, name: '配件齐全', content: '八成新，配件齐全，功能正常，支持当面验货' }
])

// 表单数据
const goodsForm = reactive({
  name: '', // 商品名称
  categoryId: [], // 分类ID（级联选择值：[一级ID, 二级ID]）
  price: '', // 商品价格
  stock: 1, // 库存数量
  description: '' // 商品描述
})

// 表单校验规则
const formRules = reactive({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', message: '价格必须为数字', trigger: 'blur' }
  ],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }]
})

// 上传文件列表
const fileList = ref([])

// 格式化价格（自动保留2位小数）
const formatPrice = () => {
  if (!goodsForm.price) return
  // 转数字并保留2位小数
  let price = Number(goodsForm.price)
  if (isNaN(price)) {
    goodsForm.price = ''
    return
  }
  // 处理整数输入（如255 → 25.50，25.5 → 25.50）
  goodsForm.price = (price / 100).toFixed(2)
}

// 监听分类变化，加载热门标签
const handleCategoryChange = (val) => {
  if (!val || val.length < 2) return
  const secondCategoryId = val[1] // 二级分类ID
  hotTags.value = categoryHotTags[secondCategoryId] || []
}

// 填充描述模板
const fillDescTemplate = (content) => {
  goodsForm.description = content
}

// 标签添加到描述
const addTagToDesc = (tag) => {
  goodsForm.description = `${goodsForm.description} ${tag}`.trim()
}

// 上传成功回调
const handleUploadSuccess = (response, file) => {
  // 假设接口返回 { code: 200, data: { url: '图片地址' } }
  if (response.code === 200) {
    file.url = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

// 移除图片
const handleRemoveImage = (file, fileList) => {
  ElMessageBox.confirm('确定删除该图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
    }
    ElMessage.success('图片已删除')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 上传数量超出限制
const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传9张图片，当前已选择${fileList.length}张`)
}

// 调整图片排序（简化版，实际可结合拖拽插件）
const sortImages = () => {
  ElMessage.info('请拖拽图片调整顺序（此处可集成vuedraggable实现拖拽排序）')
  // 实际项目可引入vuedraggable：
  // import { draggable } from 'vuedraggable'
  // 对fileList进行拖拽排序
}

// 表单是否有效（控制提交按钮禁用）
const isFormValid = computed(() => {
  if (!goodsFormRef.value) return false
  let valid = true
  Object.keys(formRules).forEach(key => {
    const result = goodsFormRef.value.validateField(key)
    if (result) valid = false
  })
  return valid && fileList.value.length > 0 // 图片至少上传1张
})

// 取消发布（返回交易市场）
const handleCancel = () => {
  ElMessageBox.confirm('确定取消发布吗？已填写内容将丢失', '提示', {
    type: 'warning'
  }).then(() => {
    router.push('/market') // 跳转到交易市场页面
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单校验
    await goodsFormRef.value.validate()
    // 构造提交数据
    const submitData = {
      name: goodsForm.name,
      category_id: goodsForm.categoryId[1], // 取二级分类ID
      price: Number(goodsForm.price),
      stock: goodsForm.stock,
      description: goodsForm.description,
      images: fileList.value.map(file => file.url), // 图片地址列表
      cover_image: fileList.value[0]?.url // 封面图（首图）
    }
    // 提交接口（POST /products）
    const response = await fetch('/api/products', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(submitData)
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('商品发布成功！等待审核通过后展示')
      router.push('/market')
    } else {
      ElMessage.error(`发布失败：${data.message}`)
    }
  } catch (error) {
    ElMessage.error('表单校验失败，请检查必填项')
    console.error('提交失败：', error)
  }
}

// 初始化加载分类数据
const fetchCategories = async () => {
  try {
    // 模拟GET /categories/tree接口
    const mockCategories = [
      {
        category_id: 1,
        category_name: '教材资料',
        children: [
          { category_id: 7, category_name: '考研资料' },
          { category_id: 8, category_name: '课本教材' }
        ]
      },
      {
        category_id: 2,
        category_name: '电子产品',
        children: [
          { category_id: 5, category_name: '电脑数码' },
          { category_id: 6, category_name: '手机平板' }
        ]
      },
      { category_id: 3, category_name: '生活用品', children: [] },
      { category_id: 4, category_name: '乐器运动', children: [] }
    ]
    categoryOptions.value = mockCategories
    // 实际项目替换为：
    // const response = await fetch('/api/categories/tree')
    // const data = await response.json()
    // categoryOptions.value = data
  } catch (error) {
    ElMessage.error('分类数据加载失败')
    console.error('加载分类失败：', error)
  }
}

// 页面挂载时加载分类
onMounted(() => {
  fetchCategories()
    if (elRef.value) {
    elRef.value.parentNode.style.display = 'none'
  }
})
</script>

<style scoped>
.publish-goods-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
}

.form-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 18px;
  color: #409eff;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.base-form {
  width: 100%;
}

.hot-tags {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-label {
  font-size: 12px;
  color: #666;
}

.price-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.desc-form-item {
  width: 100%;
}

.template-group {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.template-label {
  font-size: 12px;
  color: #666;
}

.upload-section {
  width: 100%;
}

.upload-area {
  margin-bottom: 20px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.compress-tip {
  font-size: 12px;
  color: #409eff;
  margin-top: 8px;
  text-align: center;
}

.preview-area {
  width: 100%;
}

.preview-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-list {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.cover-tag {
  position: absolute;
  top: 0;
  left: 0;
  background: #409eff;
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 2px 0 4px 0;
}

.delete-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.cancel-btn, .submit-btn {
  width: 120px;
  height: 40px;
  font-size: 16px;
}
</style>