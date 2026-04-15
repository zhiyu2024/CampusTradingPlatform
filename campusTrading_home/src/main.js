// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'  // 导入 Pinia
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCN from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './router'
import './assets/trading-style.css'

const app = createApp(App)

// 创建并挂载 Pinia 实例（必须在最前面）
const pinia = createPinia()
app.use(pinia)

// 挂载路由
app.use(router)

// 挂载 Element Plus
app.use(ElementPlus, { locale: zhCN })

app.mount('#app')