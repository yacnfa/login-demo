import { createRouter, createWebHashHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import store from '../store'
import Layout from '../views/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'House', menu: true, roles: ['admin', 'manager', 'viewer'] }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { title: '用户管理', icon: 'User', menu: true, roles: ['admin', 'manager'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach(to => {
  const token = store.state.token
  if (to.path === '/login') return token ? '/dashboard' : true
  if (!token) return '/login'

  const role = store.state.userInfo?.role
  const allowed = to.meta.roles
  if (Array.isArray(allowed) && !allowed.includes(role)) {
    ElMessage.warning('您没有权限访问该页面')
    return '/dashboard'
  }
  return true
})

export default router
