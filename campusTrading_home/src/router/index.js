import { createRouter, createWebHistory } from "vue-router"
import { setupGuards } from './guards'

const routes = [
  { path: "/", component: () => import('@/components/HomeCom.vue'), meta: { title: "首页" } },
  { path: "/login", component: () => import('@/components/UserLogin.vue'), meta: { title: "登录" } },
  { path: "/register", component: () => import('@/components/UserRegister.vue'), meta: { title: "注册" } },
  { path: "/detail", component: () => import('@/components/GoodsDetail.vue'), meta: { title: "商品详情" } },
  
  // ✅ 修复路径：与 PersonalCenter.vue 中的 goToPage 参数一致
  { path: '/cart', component: () => import('@/components/MyCart.vue'), meta: { title: "购物车" } },
  { path: '/orders', component: () => import('@/components/MyOrder.vue'), meta: { title: "我的订单", requiresAuth: true } },
  { path: '/my-publish', component: () => import('@/components/MyPublish.vue'), meta: { title: "我的发布", requiresAuth: true } },
  { path: '/user/info', component: () => import('@/components/MyInfo.vue'), meta: { title: "我的个人信息", requiresAuth: true } },
  { path: '/comments', component: () => import('@/components/ProductComments.vue'), meta: { title: "商品评价" } },
  
  { path: '/news', component: () => import('@/components/MyNews.vue'), meta: { title: "我的消息", requiresAuth: true } },
  { path: '/exchange', component: () => import('@/components/ExchangePage.vue'), meta: { title: "以物换物", requiresAuth: true } },
  { path: '/personal-center', component: () => import('@/components/PersonalCenter.vue'), meta: { title: "个人中心", requiresAuth: true } },
  { path: '/publish', component: () => import('@/components/GoodsPublish.vue'), meta: { title: "发布商品", requiresAuth: true } },
  { path: "/new", component: () => import('@/components/GoodsNew.vue'), meta: { title: "校园二手市场" } },
  { path: "/search", component: () => import('@/components/SearchGoods.vue'), meta: { title: "搜索结果", requiresAuth: true } },
]

const router = createRouter({
  routes,
  history: createWebHistory()
})

// 设置守卫
setupGuards(router)

export default router