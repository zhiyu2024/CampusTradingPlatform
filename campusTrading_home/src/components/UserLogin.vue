<template>
  <div class="auth-container">
    <div class="auth-header">
      <div class="auth-logo">🛒 水院二手交易平台</div>
      <h2 class="auth-title">登录</h2>
    </div>
    
    <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="auth-form">
      <el-form-item class="form-group">
        <label class="form-label">登录类型</label>
        <el-radio-group v-model="loginType" class="login-type-radio">
          <el-radio value="user">用户登录</el-radio>
          <el-radio value="admin">管理员登录</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item prop="username" class="form-group">
        <label class="form-label">用户名</label>
        <el-input 
          v-model="loginForm.username" 
          placeholder="请输入用户名"
          class="form-input"
          clearable
        />
      </el-form-item>
      
      <el-form-item prop="password" class="form-group">
        <label class="form-label">密码</label>
        <el-input 
          type="password"
          v-model="loginForm.password" 
          placeholder="请输入密码"
          class="form-input"
          clearable
          show-password
        />
      </el-form-item>
      
      <el-form-item>
        <el-button 
          type="primary" 
          class="auth-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form-item>
      
      <div class="auth-footer" v-if="loginType === 'user'">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
        <span style="margin: 0 10px;">|</span>
        <router-link to="/">返回首页</router-link>
      </div>
    </el-form>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/request'
import { authUtils } from '@/utils/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loginFormRef = ref(null)
const loading = ref(false)
const loginType = ref('user')

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      let response
      
      if (loginType.value === 'user') {
        response = await axios.post('/public/login', {
          username: loginForm.username,
          password: loginForm.password
        })
        
        console.log('用户登录响应:', response)
        
        if (response.code === 200) {
          const userData = response.data
          
          // 用户登录接口只返回普通用户数据，直接设置 role=0
          authUtils.setLogin(userData.token, {
            userId: userData.userId,
            nickname: userData.nickname,
            avatar: userData.avatar,
            role: 0
          })
          
          authStore.login(userData.token, {
            userId: userData.userId,
            nickname: userData.nickname,
            avatar: userData.avatar,
            role: 0
          })
          
          ElMessage.success('登录成功')
          const redirect = route.query.redirect || '/'
          router.replace(redirect).then(() => {
            window.location.reload()
          })
        }
      } else {
        response = await axios.post('/AdminLogin', {
          username: loginForm.username,
          password: loginForm.password
        })
        
        console.log('管理员登录响应:', response)
        
        if (response.code === 200) {
          const adminInfo = response.data
          
          // 如果后端没有返回 role，默认设置为 1（因为是管理员登录接口）
          if (!adminInfo.role && adminInfo.role !== 0) {
            adminInfo.role = 1
          }
          
          // 检查后端返回的 role 是否为 1
          if (adminInfo.role !== 1) {
            ElMessage.error('该账号不是管理员账号，请选择用户登录')
            return
          }
          
          localStorage.setItem('token', adminInfo.token)
          localStorage.setItem('userInfo', JSON.stringify(adminInfo))
          
          ElMessage.success('管理员登录成功，正在跳转到管理后台...')
          
          setTimeout(() => {
            window.location.href = 'http://localhost:3000/login?token=' + encodeURIComponent(adminInfo.token) + '&userInfo=' + encodeURIComponent(JSON.stringify(adminInfo))
          }, 500)
        }
      }
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.response?.data?.msg || '用户名或密码错误')
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  const username = route.query.username
  const password = route.query.password
  
  if (username && password) {
    loginForm.username = decodeURIComponent(username)
    loginForm.password = decodeURIComponent(password)
    handleLogin()
  }
})
</script>



<style scoped>
.auth-container {
  max-width: 450px;
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
  max-width: 100%;
}

.auth-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: none;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  border-color: #4CAF50;
  box-shadow: 0 0 0 1px #4CAF50 inset;
}

.login-type-radio {
  display: flex;
  gap: 20px;
}

.auth-btn {
  width: 100%;
  padding: 14px;
  background: #4CAF50;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  color: #fff;
  cursor: pointer;
  margin-top: 10px;
}

.auth-btn:hover {
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