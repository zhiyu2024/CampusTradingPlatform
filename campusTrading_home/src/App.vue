<template>
  <div class="app">
    <!-- 顶部头部 -->
    <header class="header-fixed">
      <div class="header-top">
        <div class="header-container">
          <!-- Logo -->
          <div class="logo">
            <div class="logo-icon">
              <img class="logopic" src="./assets/logoLast.png">
            </div>
            <span>水院二手交易平台</span>
          </div>

          <!-- 搜索框 -->
          <div class="search-bar">
            <SearchCom />
          </div>

          <!-- 右侧功能区 -->
          <div class="header-right">
            <!-- 购物车 -->
            <a href="javascript:void(0)" class="header-btn" @click="goCart">
              <span>🛒</span>
              <span>购物车</span>
              <span v-if="cartCount > 0" style="background: #FF5722; color: #fff; padding: 2px 8px; border-radius: 10px; font-size: 12px; margin-left: 5px;">{{ cartCount }}</span>
            </a>

            <!-- 消息 -->
            <a href="javascript:void(0)" class="header-btn" @click="goNews" style="position: relative;">
              <span>🔔</span>
              <span>消息</span>
              <span v-if="unreadMsgCount > 0" style="background: #FF5722; color: #fff; padding: 2px 8px; border-radius: 10px; font-size: 12px; margin-left: 5px; position: absolute; top: -8px; right: -8px;">{{ unreadMsgCount }}</span>
            </a>

            <!-- 发布商品 -->
            <a href="javascript:void(0)" class="header-btn publish" @click="goPublish">
              <span>📝</span>
              <span>发布</span>
            </a>

            <!-- 用户菜单 -->
            <div class="user-menu" ref="userMenu">
              <div class="user-avatar" @click="toggleDropdown">
                <img 
                  :src="userAvatar" 
                  alt="用户头像"
                  @error="handleAvatarError"
                />
              </div>

              <div class="dropdown" v-show="dropdownOpen" style="position: absolute; top: 50px; right: 0; background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); min-width: 180px; z-index: 1001;">
                <div v-if="!isLogin" style="padding: 10px 0;">
                  <router-link to="/login" @click="closeDropdown" style="display: block; padding: 12px 20px; color: #333; text-decoration: none; transition: background 0.3s;">账号登录</router-link>
                  <router-link to="/register" @click="closeDropdown" style="display: block; padding: 12px 20px; color: #333; text-decoration: none; transition: background 0.3s;">注册新账号</router-link>
                </div>
                <div v-else style="padding: 10px 0;">
                  <div style="padding: 12px 20px; font-weight: bold; color: #333; border-bottom: 1px solid #eee;">欢迎回来，{{ userInfo?.nickname }}</div>
                  <router-link to="/personal-center" @click="closeDropdown" style="display: block; padding: 12px 20px; color: #333; text-decoration: none; transition: background 0.3s;">个人中心</router-link>
                  <button @click="logout" style="display: block; width: 100%; padding: 12px 20px; color: #FF5722; background: none; border: none; text-align: left; cursor: pointer; transition: background 0.3s;">退出登录</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 导航栏 -->
      <nav class="nav-bar">
        <div class="nav-container">
          <MyMenus />
        </div>
      </nav>
    </header>

    <!-- 主体内容 -->
    <main style="padding-top: 120px;">
      <div class="container">
        <router-view />
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-links">
          <a href="javascript:void(0)">关于我们</a>
          <a href="javascript:void(0)">帮助中心</a>
          <a href="javascript:void(0)">联系我们</a>
          <a href="javascript:void(0)">服务协议</a>
          <a href="javascript:void(0)">隐私政策</a>
        </div>
        <div class="footer-copyright">
          校园交易平台 © 2024 版权所有 | 联系我们：service@campusmarket.com | 地址：水汽大学闪电大楼
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import axios from '@/utils/request'
import SearchCom from './components/SearchCom.vue'
import MyMenus from './components/MyMenus.vue'
import useShopStore from './store/cart'
import { authUtils } from '@/utils/auth'

export default {
  name: 'App',
  components: {
    SearchCom,
    MyMenus,
  },
  setup() {
    const router = useRouter()
    const shopStore = useShopStore()
    
    const { items } = storeToRefs(shopStore)
    const dropdownOpen = ref(false)
    const userMenu = ref(null)
    const unreadMsgCount = ref(0)

    // ✅ 修复：使用 computed + watchEffect 确保响应式
    const isLogin = computed(() => authUtils.isLogin())
    
    const userInfo = ref(authUtils.getUserInfo())
    
    // ✅ 监听authUtils变化
    watchEffect(() => {
      const info = authUtils.getUserInfo()
      userInfo.value = info || {}
    })

    // ✅ 修复：强制刷新头像
    const userAvatar = computed(() => {
      const avatar = userInfo.value?.avatar
      if (avatar && avatar !== 'null' && avatar !== 'undefined') {
        return `${avatar.includes('http') ? '' : 'http://localhost:8090'}${avatar}?t=${Date.now()}`
      }
      return '/头像.png'
    })

    // 购物车数量
    const cartCount = computed(() => items.value.length)

    // 获取未读消息数量
    const getUnreadCount = async () => {
      if (!authUtils.isLogin()) {
        unreadMsgCount.value = 0
        return
      }
      try {
        const response = await axios.get('/user/message/sessions')
        if (response.code === 200) {
          const sessions = response.data || []
          unreadMsgCount.value = sessions.reduce((total, session) => total + (session.unreadCount || 0), 0)
        }
      } catch (error) {
        console.error('获取未读消息失败:', error)
      }
    }

    // 切换下拉菜单
    const toggleDropdown = () => {
      dropdownOpen.value = !dropdownOpen.value
    }

    // 关闭下拉菜单
    const closeDropdown = () => {
      dropdownOpen.value = false
    }

    // 点击外部关闭
    const handleClickOutside = (e) => {
      if (userMenu.value && !userMenu.value.contains(e.target)) {
        closeDropdown()
      }
    }

    // 路由跳转
    const goCart = () => {
      if (!isLogin.value) {
        router.push({ path: '/login', query: { redirect: '/personal-center' } })
        return
      }
      router.push({ path: '/personal-center', query: { menu: 'cart' } })
    }
    
    const goNews = () => {
      if (!isLogin.value) {
        router.push({ path: '/login', query: { redirect: '/news' } })
        return
      }
      router.push('/news')
    }
    
    const goPublish = () => {
      if (!isLogin.value) {
        router.push({ path: '/login', query: { redirect: '/publish' } })
        return
      }
      router.push('/publish')
    }

    // ✅ 修复：退出登录后强制刷新
    const logout = () => {
      closeDropdown()
      authUtils.logout()
      // 强制重置用户信息
      userInfo.value = {}
      unreadMsgCount.value = 0
      router.push('/')
      ElMessage.success('退出成功')
      // 延迟刷新确保状态清除
      setTimeout(() => {
        window.location.reload()
      }, 300)
    }

    // ✅ 头像加载失败处理
    const handleAvatarError = (e) => {
      console.log('头像加载失败，使用默认头像:', e)
      e.target.src = '/src/assets/头像.png'
    }

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
      getUnreadCount()
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      cartCount,
      unreadMsgCount,
      isLogin,
      userInfo,
      userAvatar,
      dropdownOpen,
      userMenu,
      toggleDropdown,
      closeDropdown,
      goCart,
      goNews,
      goPublish,
      logout,
      handleAvatarError
    }
  }
}
</script>


<style>
/* 下拉菜单链接悬停效果 */
.dropdown a:hover,
.dropdown button:hover {
  background: #f0f9f0 !important;
  color: #4CAF50 !important;
}

.logo span{
  font-size: 24px;
}
/* .logo-icon {
 width: 100px;
  height: 100px;
} */
.logopic{
  width: 56px;
  height: 50px;
  margin-right: 5px;
}
</style>