<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  console.log('App.vue mounted, 当前URL:', window.location.href)
  
  const urlParams = new URLSearchParams(window.location.search)
  const tokenFromUrl = urlParams.get('token')
  const userInfoFromUrl = urlParams.get('userInfo')
  
  console.log('URL参数 - token:', tokenFromUrl ? '存在' : '不存在')
  console.log('URL参数 - userInfo:', userInfoFromUrl ? '存在' : '不存在')
  
  if (tokenFromUrl && userInfoFromUrl) {
    try {
      const decodedToken = decodeURIComponent(tokenFromUrl)
      const decodedUserInfo = decodeURIComponent(userInfoFromUrl)
      console.log('解码后的token:', decodedToken)
      console.log('解码后的userInfo:', decodedUserInfo)
      
      const parsedUserInfo = JSON.parse(decodedUserInfo)
      console.log('解析后的userInfo:', parsedUserInfo)
      
      if (parsedUserInfo.role !== 1) {
        ElMessage.error('该账号不是管理员账号，正在跳转到用户前台...')
        setTimeout(() => {
          window.location.href = 'http://localhost:8080'
        }, 1500)
        return
      }
      
      localStorage.setItem('token', decodedToken)
      localStorage.setItem('userInfo', JSON.stringify(parsedUserInfo))
      userStore.setToken(decodedToken)
      userStore.setUserInfo(parsedUserInfo)
      
      console.log('已保存到localStorage - token:', localStorage.getItem('token'))
      console.log('已保存到localStorage - userInfo:', localStorage.getItem('userInfo'))
      
      window.history.replaceState({}, document.title, window.location.pathname)
      
      ElMessage.success('自动登录成功')
      
      setTimeout(() => {
        router.replace('/')
      }, 300)
    } catch (e) {
      console.error('解析用户信息失败', e)
      ElMessage.error('登录信息解析失败')
    }
  } else {
    const existingToken = localStorage.getItem('token')
    console.log('检查现有token:', existingToken ? '存在' : '不存在')
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  height: 100vh;
}

/* 全局样式 */
.el-button + .el-button {
  margin-left: 8px;
}
</style>