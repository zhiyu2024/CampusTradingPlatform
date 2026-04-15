<template>
  <ul>
    <li 
      v-for="item in navItems" 
      :key="item.path"
    >
      <router-link :to="item.path" :class="{ active: currentPath === item.path }">
        {{ item.label }}
      </router-link>
    </li>
  </ul>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

// 导航菜单数据
const navItems = [
  { path: '/', label: '首页' },
  { path: '/new', label: '交易市场' },
  { path: '/news', label: '我的消息' },
  { path: '/exchange', label: '以物换物' },
  { path: '/personal-center', label: '个人中心' }
]

// 当前路由路径
const route = useRoute()
const currentPath = ref(route.path)

// 监听路由变化更新选中状态
watch(
  () => route.path,
  (newPath) => {
    currentPath.value = newPath
  },
  { immediate: true }
)
</script>

<style scoped>
ul {
  display: flex;
  gap: 40px;
  margin: 0;
  padding: 0;
  list-style: none;
}

a {
  display: block;
  padding: 15px 0;
  font-size: 16px;
  color: #666;
  text-decoration: none;
  position: relative;
  transition: color 0.3s;
}

a:hover,
a.active {
  color: #4CAF50;
}

a.active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: #4CAF50;
}
</style>