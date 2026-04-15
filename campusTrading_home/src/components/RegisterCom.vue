<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="register-title">用户注册</h2>
      
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef"
        label-width="100px"
        class="register-form"
      >
        <!-- 头像上传 -->
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            :show-file-list="false"
            :limit="1"
            accept="image/*"
          >
            <img v-if="avatarPreview" :src="avatarPreview" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="el-upload__tip">点击上传头像 (可选, 最大100MB)</div>
        </el-form-item>

        <!-- 学号 -->
        <el-form-item label="学号" prop="studentNo">
          <el-input 
            v-model="form.studentNo" 
            placeholder="请输入学号 (6-20位数字)"
            maxlength="20"
          />
        </el-form-item>

        <!-- 登录账号 -->
        <el-form-item label="登录账号" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入6-20位字母/数字组合"
            maxlength="20"
          />
          <div class="input-tip">账号需为6-20位字母/数字组合</div>
        </el-form-item>

        <!-- 昵称 -->
        <el-form-item label="昵称" prop="nickname">
          <el-input 
            v-model="form.nickname" 
            placeholder="请输入昵称"
            maxlength="20"
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入6-20位密码"
            maxlength="20"
            show-password
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            maxlength="20"
            show-password
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入11位手机号"
            maxlength="11"
          />
        </el-form-item>

        <!-- 所在校区 -->
        <el-form-item label="所在校区" prop="campus">
          <el-select v-model="form.campus" placeholder="请选择校区">
            <el-option label="主校区" value="主校区" />
            <el-option label="南校区" value="南校区" />
            <el-option label="北校区" value="北校区" />
            <el-option label="新校区" value="新校区" />
          </el-select>
        </el-form-item>

        <!-- 按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="registering">
            立即注册
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
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
  
  // ✅ 修改：允许最大100MB
  const maxSize = 100 * 1024 * 1024  // 100MB
  if (file.raw.size > maxSize) {
    ElMessage.error(`头像大小不能超过100MB`)
    // 清空上传
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
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 100, 200, 0.15);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #1e293b;
  font-size: 24px;
  font-weight: 600;
}

.register-form {
  width: 100%;
}

.avatar-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 178px;
  height: 178px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 56px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  display: flex !important;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 178px;
  height: 178px;
  object-fit: cover;
  display: block;
}

.el-upload__tip {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.input-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>