<template>
  <div class="header-left">
    <el-button @click="emit('toggle-sidebar')" link>
      <el-icon size="20"><Fold /></el-icon>
    </el-button>
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
  
  <div class="header-right">
    <el-dropdown @command="handleCommand">
      <el-avatar :src="userStore.userInfo?.avatar" :size="40">
        {{ userStore.userInfo?.nickname?.charAt(0) || 'A' }}
      </el-avatar>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon> 个人信息
          </el-dropdown-item>
          <el-dropdown-item command="logout" divided>
            <el-icon><SwitchButton /></el-icon> 退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
// ✅ defineEmits 必须在第一行，import 之前
const emit = defineEmits(['toggle-sidebar'])

import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-right {
  margin-left: auto;
}
</style>