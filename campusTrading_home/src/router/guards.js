import { ElMessage } from 'element-plus'
import { authUtils } from '@/utils/auth'

export function setupGuards(router) {
  router.beforeEach(async (to, from, next) => {
    // 检查是否需要登录
    const authRequired = to.matched.some(record => record.meta.requiresAuth)
    const isLogin = authUtils.isLogin()
    
    if (authRequired && !isLogin) {
      ElMessage.warning('请先登录')
      next({ path: "/login", query: { redirect: to.fullPath } })
    } else {
      next()
    }
  })
}