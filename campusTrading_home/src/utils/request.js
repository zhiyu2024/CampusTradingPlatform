import axios from 'axios'
import { ElMessage } from 'element-plus'
import { authUtils } from '@/utils/auth'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 60000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // ✅ 绝对路径判断：所有 /public/ 接口不携带Token
    const isPublicApi = config.url.includes('/public/')
    
    if (!isPublicApi) {
      const token = authUtils.getToken()
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }
    
    // ✅ FormData不设置Content-Type
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      if (res.code === 401) {
        authUtils.logout()
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.error('请求错误:', error.response || error)
    const errMsg = error.response?.data?.msg || error.message || '网络错误'
    ElMessage.error(errMsg)
    return Promise.reject(error)
  }
)

export default service