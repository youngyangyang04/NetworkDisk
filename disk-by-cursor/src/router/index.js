import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'files',
        name: 'Files',
        component: () => import('@/views/Files.vue')
      },
      {
        path: 'shares',
        name: 'Shares',
        component: () => import('@/views/Shares.vue')
      },
      {
        path: 'recycle',
        name: 'Recycle',
        component: () => import('@/views/Recycle.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const { isLoggedIn } = storeToRefs(userStore)

  console.log('路由守卫检查:', {
    to: to.path,
    from: from.path,
    isLoggedIn: isLoggedIn.value,
    requiresAuth: to.meta.requiresAuth
  })

  if (to.meta.requiresAuth && !isLoggedIn.value) {
    ('需要认证但未登录，跳转到登录页')
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && isLoggedIn.value) {
    console.log('已登录但访问登录/注册页，跳转到首页')
    next('/')
  } else {
    console.log('路由检查通过')
    next()
  }
})

export default router 