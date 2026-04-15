<template>
  <div class="sidebar-logo">
    <el-icon size="30" color="#409EFF"><Shop /></el-icon>
    <span v-show="!isCollapse">二手交易系统</span>
  </div>
  <el-menu
    :default-active="$route.path"
    class="sidebar-menu"
    :collapse="isCollapse"
    router
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
  >
    <el-menu-item index="/data-panel">
      <el-icon><DataAnalysis /></el-icon>
      <span>数据面板</span>
    </el-menu-item>
    
    <el-menu-item index="/users">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
    
    <el-menu-item index="/categories">
      <el-icon><Collection /></el-icon>
      <span>商品分类</span>
    </el-menu-item>
    
    <el-menu-item index="/products">
      <el-icon><Goods /></el-icon>
      <span>商品管理</span>
    </el-menu-item>
    
    <el-menu-item index="/orders">
      <el-icon><ShoppingCart /></el-icon>
      <span>订单管理</span>
    </el-menu-item>
    
    <el-menu-item index="/messages">
      <el-icon><Message /></el-icon>
      <span>消息管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
// ✅ defineProps 必须在第一行，import 之前
const props = defineProps({
  isCollapse: {
    type: Boolean,
    required: true
  }
})
</script>

<style scoped>
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

.sidebar-logo .el-icon {
  margin-right: 10px;
}

.sidebar-menu {
  border-right: none;
}
</style>