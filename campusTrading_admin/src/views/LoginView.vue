<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">二手商品管理系统</h2>
      <p class="login-hint">请从用户前台（http://localhost:8080）登录</p>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="info" @click="goToUserFront" style="width: 100%">
            前往用户前台登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await userApi.adminLogin(loginForm)
        const adminInfo = res.data
        
        if (adminInfo.role !== 1) {
          ElMessage.error('该账号不是管理员账号，请前往用户前台登录')
          return
        }
        
        userStore.setToken(adminInfo.token)
        userStore.setUserInfo(adminInfo)
        localStorage.setItem('token', adminInfo.token)
        localStorage.setItem('userInfo', JSON.stringify(adminInfo))
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToUserFront = () => {
  window.location.href = 'http://localhost:8080'
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 30px;
}

.login-title {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
}

.login-hint {
  text-align: center;
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-input__prefix) {
  display: flex;
  align-items: center;
}
</style>