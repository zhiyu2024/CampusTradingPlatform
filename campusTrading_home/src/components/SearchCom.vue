<template>
  <form @submit.prevent="goSearch">
    <input 
      type="text" 
      v-model="keyword"
      placeholder="搜索二手商品..."
    >
    <button type="submit">搜索</button>
  </form>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"

const keyword = ref("")
const router = useRouter()

const goSearch = function () {
  if (!keyword.value.trim()) {
    ElMessage.warning("请输入搜索内容！")
    return
  }
  router.push({
    path: "/search",
    query: { keyword: keyword.value }
  })
}
</script>

<style scoped>
form {
  display: flex;
  background: #fff;
  border-radius: 25px;
  overflow: hidden;
}

input {
  flex: 1;
  padding: 12px 20px;
  border: none;
  outline: none;
  font-size: 14px;
}

button {
  padding: 12px 30px;
  background: #4CAF50;
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

button:hover {
  background: #45a049;
}
</style>