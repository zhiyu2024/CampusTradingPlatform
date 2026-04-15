<template>
  <div class="auth-container">
    <div class="auth-header">
      <div class="auth-logo">🛒 水院二手交易平台</div>
      <h2 class="auth-title">用户注册</h2>
    </div>
    
    <el-form 
      :model="form" 
      :rules="rules" 
      ref="formRef"
      class="auth-form publish-form"
    >
      <!-- 头像上传 -->
      <el-form-item prop="avatar" class="form-group">
        <label class="form-label">头像</label>
        <div class="form-upload">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            :show-file-list="false"
            :limit="1"
            accept="image/*"
          >
            <div class="upload-preview">
              <img v-if="avatarPreview" :src="avatarPreview" alt="头像" />
              <span v-else>+</span>
            </div>
          </el-upload>
          <span style="font-size: 13px; color: #999;">点击上传头像 (可选, 最大100MB)</span>
        </div>
      </el-form-item>

      <!-- 学号 -->
      <el-form-item prop="studentNo" class="form-group">
        <label class="form-label">学号</label>
        <el-input 
          v-model="form.studentNo" 
          placeholder="请输入学号 (6-20位数字)"
          maxlength="20"
          class="form-input"
        />
      </el-form-item>

      <!-- 登录账号 -->
      <el-form-item prop="username" class="form-group">
        <label class="form-label">登录账号</label>
        <el-input 
          v-model="form.username" 
          placeholder="请输入6-20位字母/数字组合"
          maxlength="20"
          class="form-input"
        />
        <div style="font-size: 12px; color: #999; margin-top: 5px;">账号需为6-20位字母/数字组合</div>
      </el-form-item>

      <!-- 昵称 -->
      <el-form-item prop="nickname" class="form-group">
        <label class="form-label">昵称</label>
        <el-input 
          v-model="form.nickname" 
          placeholder="请输入昵称"
          maxlength="20"
          class="form-input"
        />
      </el-form-item>

      <!-- 密码 -->
      <el-form-item prop="password" class="form-group">
        <label class="form-label">密码</label>
        <el-input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入6-20位密码"
          maxlength="20"
          show-password
          class="form-input"
        />
      </el-form-item>

      <!-- 确认密码 -->
      <el-form-item prop="confirmPassword" class="form-group">
        <label class="form-label">确认密码</label>
        <el-input 
          v-model="form.confirmPassword" 
          type="password" 
          placeholder="请再次输入密码"
          maxlength="20"
          show-password
          class="form-input"
        />
      </el-form-item>

      <!-- 手机号 -->
      <el-form-item prop="phone" class="form-group">
        <label class="form-label">手机号</label>
        <el-input 
          v-model="form.phone" 
          placeholder="请输入11位手机号"
          maxlength="11"
          class="form-input"
        />
      </el-form-item>

      <!-- 所在校区 -->
      <el-form-item prop="campus" class="form-group">
        <label class="form-label">所在校区</label>
        <el-select v-model="form.campus" placeholder="请选择校区" class="form-input" style="width: 100%; max-width: 400px;">
          <el-option label="主校区" value="主校区" />
          <el-option label="南校区" value="南校区" />
          <el-option label="北校区" value="北校区" />
          <el-option label="新校区" value="新校区" />
        </el-select>
      </el-form-item>

      <!-- 按钮 -->
      <el-form-item>
        <el-button type="primary" @click="handleRegister" :loading="registering" class="form-btn">
          立即注册
        </el-button>
        <el-button @click="resetForm" style="margin-left: 15px; background: #f0f0f0; border: none;">重置</el-button>
      </el-form-item>
      
      <div class="auth-footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
        <span style="margin: 0 10px;">|</span>
        <router-link to="/">返回首页</router-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const registering = ref(false)
const avatarFile = ref(null)
const avatarPreview = ref('')

// 表单数据
const form = reactive({
  studentNo: '',
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  phone: '',
  campus: '主校区'
})

// 表单验证规则
const rules = reactive({
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{6,20}$/, message: '学号需为6-20位数字', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{6,20}$/, message: '账号需为6-20位字母/数字组合', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  campus: [
    { required: true, message: '请选择校区', trigger: 'change' }
  ]
})

// 处理头像选择
const handleAvatarChange = (file) => {
  if (!file?.raw) return
  
  console.log('选择头像文件:', file.raw.name, '大小:', file.raw.size)
  
  // ✅ 允许最大100MB
  const maxSize = 100 * 1024 * 1024
  if (file.raw.size > maxSize) {
    ElMessage.error(`头像大小不能超过100MB`)
    avatarFile.value = null
    avatarPreview.value = ''
    return
  }
  
  avatarFile.value = file.raw
  
  // 预览
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 处理头像移除
const handleAvatarRemove = () => {
  avatarFile.value = null
  avatarPreview.value = ''
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  handleAvatarRemove()
}

// 提交注册
const handleRegister = async () => {
  // 表单验证
  await formRef.value?.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请检查表单填写是否正确')
      return
    }

    registering.value = true
    
    const formData = new FormData()
    // 添加所有字段
    formData.append('studentNo', form.studentNo)
    formData.append('username', form.username)
    formData.append('nickname', form.nickname)
    formData.append('password', form.password)
    formData.append('phone', form.phone)
    formData.append('campus', form.campus)
    
    // ✅ 添加头像文件（如果有）
    if (avatarFile.value) {
      formData.append('avatar', avatarFile.value)
      console.log('添加了头像文件:', avatarFile.value.name, '大小:', avatarFile.value.size)
    }

    try {
      console.log('发送注册请求到 /public/register')
      const res = await request.post('/public/register', formData, {
        headers: {
          // ✅ 不设置Content-Type，让浏览器自动生成
        }
      })
      
      console.log('注册响应:', res)
      
      if (res.code === 200) {
        ElMessage.success('注册成功！即将跳转到登录页...')
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } else {
        ElMessage.error(res.msg || '注册失败')
      }
    } catch (err) {
      console.error('注册失败:', err.response || err)
      ElMessage.error(err.response?.data?.msg || '注册失败，请重试')
    } finally {
      registering.value = false
    }
  })
}
</script>

<style scoped>
.auth-container {
  max-width: 800px;
  margin: 50px auto;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.auth-header {
  text-align: center;
  margin-bottom: 35px;
}

.auth-logo {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 10px;
}

.auth-title {
  font-size: 20px;
  color: #333;
  margin: 0;
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

.auth-form .form-input {
  width: 100%;
  max-width: 400px;
}

.auth-form :deep(.el-input__wrapper),
.auth-form :deep(.el-select .el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: none;
}

.auth-form :deep(.el-input__wrapper.is-focus),
.auth-form :deep(.el-select .el-input__wrapper.is-focus) {
  border-color: #4CAF50;
  box-shadow: 0 0 0 1px #4CAF50 inset;
}

.form-upload {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.upload-preview {
  width: 150px;
  height: 150px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  overflow: hidden;
  cursor: pointer;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-preview:hover {
  border-color: #4CAF50;
}

.form-btn {
  padding: 12px 40px;
  background: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.form-btn:hover {
  background: #45a049;
}

.auth-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #666;
}

.auth-footer a {
  color: #4CAF50;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
}
</style>