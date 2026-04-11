import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/store/user'

NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/house/search.vue'),
        meta: { title: '房源搜索', requiresAuth: false }
      },
      {
        path: 'house/:id',
        name: 'HouseDetail',
        component: () => import('@/views/house/detail.vue'),
        meta: { title: '房源详情', requiresAuth: false }
      },
      {
        path: 'publish',
        name: 'PublishHouse',
        component: () => import('@/views/house/publish.vue'),
        meta: { title: '发布房源', requiresAuth: true, roles: ['LANDLORD', 'AGENT'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'my-houses',
        name: 'MyHouses',
        component: () => import('@/views/user/my-houses.vue'),
        meta: { title: '我的房源', requiresAuth: true, roles: ['LANDLORD', 'AGENT'] }
      },
      {
        path: 'my-favorites',
        name: 'MyFavorites',
        component: () => import('@/views/user/favorites.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/user/appointments.vue'),
        meta: { title: '我的预约', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  
  document.title = to.meta.title ? `${to.meta.title} - 房屋租赁平台` : '房屋租赁平台'
  
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (to.meta.roles && !to.meta.roles.includes(userStore.userType)) {
    next({ path: '/' })
    NProgress.done()
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
