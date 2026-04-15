import { createRouter, createWebHistory } from 'vue-router'
import LayoutIndex from '@/components/Layout/LayoutIndex.vue'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    component: LayoutIndex,
    redirect: '/data-panel',
    children: [
      {
        path: 'data-panel',
        name: 'DataPanel',
        component: () => import('@/views/DataPanel.vue'),
        meta: { title: '数据面板', requiresAuth: true, role: 1 }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/ProfileView.vue'),
        meta: { title: '个人信息', requiresAuth: true, role: 1 }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/Users/UserList.vue'),
        meta: { title: '用户管理', requiresAuth: true, role: 1 }
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/Categories/CategoryList.vue'),
        meta: { title: '商品分类', requiresAuth: true, role: 1 }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/Products/ProductList.vue'),
        meta: { title: '商品管理', requiresAuth: true, role: 1 }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/Orders/OrderList.vue'),
        meta: { title: '订单管理', requiresAuth: true, role: 1 }
      },
      {
        path: 'messages',
        name: 'MessageList',
        component: () => import('@/views/Messages/MessageList.vue'),
        meta: { title: '消息管理', requiresAuth: true, role: 1 }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  console.log('路由守卫 - 目标路径:', to.path)
  console.log('路由守卫 - token:', token ? '存在' : '不存在')
  
  if (to.path === '/login') {
    if (token) {
      console.log('路由守卫 - 已登录，跳转到首页')
      next('/')
    } else {
      console.log('路由守卫 - 未登录，允许访问登录页')
      next()
    }
  } else {
    if (!token) {
      console.log('路由守卫 - 未登录，跳转到登录页')
      ElMessage.warning('请先登录')
      next('/login')
    } else {
      console.log('路由守卫 - 已登录，允许访问')
      next()
    }
  }
})

export default router