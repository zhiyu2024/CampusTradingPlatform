<template>
  <div v-loading="loading">
    <div v-if="cartList.length > 0" class="cart-list">
      <div class="cart-header">
        <span>选择</span>
        <span>商品信息</span>
        <span>单价</span>
        <span>数量</span>
        <span>小计</span>
        <span>操作</span>
      </div>
      
      <div v-for="item in cartList" :key="item.cartId" class="cart-item">
        <div class="cart-check">
          <input type="checkbox" :checked="true" />
        </div>
        <div class="cart-img">
          <img :src="item.productImage || '/placeholder.png'" alt="商品" />
        </div>
        <div class="cart-info">
          <div class="cart-title">{{ item.productName }}</div>
        </div>
        <div class="cart-price">¥{{ item.price.toFixed(2) }}</div>
        <div class="cart-quantity">
          <button @click="updateQuantity(item.cartId, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
          <input type="text" :value="item.quantity" readonly />
          <button @click="updateQuantity(item.cartId, item.quantity + 1)">+</button>
        </div>
        <div class="cart-total-item">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        <div class="cart-delete" @click="removeFromCart(item.cartId)">删除</div>
      </div>
      
      <div class="cart-footer">
        <div class="cart-footer-left">
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
            <input type="checkbox" checked />
            <span>全选</span>
          </label>
          <button @click="clearCart" style="background: none; border: none; color: #666; cursor: pointer;">清空购物车</button>
        </div>
        <div class="cart-footer-right">
          <span class="cart-total-text">合计：</span>
          <span class="cart-total-price">¥{{ totalPrice.toFixed(2) }}</span>
          <button class="checkout-btn" @click="checkout">去结算</button>
        </div>
      </div>
    </div>
    
    <div v-else class="section" style="text-align: center; padding: 80px 20px;">
      <div style="font-size: 60px; margin-bottom: 20px;">🛒</div>
      <div style="font-size: 18px; color: #666; margin-bottom: 30px;">购物车是空的</div>
      <el-button type="primary" @click="$router.push('/new')">去逛逛</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/request'
import { authUtils } from '@/utils/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const cartList = ref([])

// 计算总价
const totalPrice = computed(() => {
  return cartList.value.reduce((total, item) => {
    return total + (item.price * item.quantity)
  }, 0)
})

// 获取购物车列表
const getCartList = async () => {
  if (!authUtils.isLogin()) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const response = await axios.get('/user/cart/list')
    
    if (response.code === 200) {
      cartList.value = (response.data || []).map(item => ({
        cartId: item.cartId,
        productId: item.productId,
        productName: item.productName || '未知商品',
        productImage: item.productImage || '/placeholder.png',
        price: item.price || 0,
        quantity: item.quantity || 1
      }))
    } else {
      ElMessage.error(response.msg || '获取购物车失败')
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

// 更新商品数量
const updateQuantity = async (cartId, newQuantity) => {
  if (newQuantity < 1) return
  
  try {
    const response = await axios.post('/user/cart/update', {
      cartId,
      quantity: newQuantity
    })
    
    if (response.code === 200) {
      ElMessage.success('更新成功')
      getCartList()
    } else {
      ElMessage.error(response.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 删除商品
const removeFromCart = async (cartId) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.post('/user/cart/remove', null, {
      params: { cartId }
    })
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getCartList()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清空购物车
const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.delete('/user/cart/clear')
    
    if (response.code === 200) {
      ElMessage.success('清空成功')
      cartList.value = []
    } else {
      ElMessage.error(response.msg || '清空失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 结算 - 逐个创建订单
const checkout = async () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  try {
    await ElMessageBox.confirm(`确定结算 ${cartList.value.length} 件商品吗？`, '结算确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    loading.value = true
    
    for (const item of cartList.value) {
      const response = await axios.post('/user/order/create', {
        productId: item.productId,
        quantity: item.quantity
      })
      
      if (response.code !== 200) {
        ElMessage.error(`商品《${item.productName}》下单失败: ${response.msg}`)
        return
      }
    }
    
    ElMessage.success('下单成功')
    await axios.delete('/user/cart/clear')
    router.push({ path: '/order' })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结算失败:', error)
      ElMessage.error('结算失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (authUtils.isLogin()) {
    getCartList()
  }
})
</script>

<style scoped>
.cart-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  gap: 15px;
  padding: 20px;
  background: #f9f9f9;
  font-size: 14px;
  color: #666;
}

.cart-item {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  gap: 15px;
  padding: 20px;
  border-bottom: 1px solid #eee;
  align-items: center;
}

.cart-check {
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-check input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.cart-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f5f5f5;
}

.cart-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.cart-info {
  padding-left: 10px;
}

.cart-title {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.cart-price {
  font-size: 18px;
  font-weight: bold;
  color: #FF5722;
}

.cart-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 18px;
}

.cart-quantity input {
  width: 50px;
  text-align: center;
  border: 1px solid #ddd;
  padding: 5px;
}

.cart-total-item {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.cart-delete {
  text-align: center;
  color: #999;
  cursor: pointer;
}

.cart-delete:hover {
  color: #FF5722;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
}

.cart-footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.cart-total-text {
  font-size: 16px;
}

.cart-total-price {
  font-size: 28px;
  font-weight: bold;
  color: #FF5722;
}

.checkout-btn {
  padding: 15px 50px;
  background: #FF5722;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
}

.checkout-btn:hover {
  background: #F4511E;
}
</style>