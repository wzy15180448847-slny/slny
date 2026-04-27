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
    path: '/tenant',
    component: () => import('@/layouts/TenantLayout.vue'),
    meta: { requiresAuth: true, roles: ['TENANT'] },
    children: [
      {
        path: '',
        name: 'TenantHome',
        component: () => import('@/views/tenant/index.vue'),
        meta: { title: '租客中心', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'profile',
        name: 'TenantProfile',
        component: () => import('@/views/tenant/profile.vue'),
        meta: { title: '个人中心', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'favorites',
        name: 'TenantFavorites',
        component: () => import('@/views/tenant/favorites.vue'),
        meta: { title: '我的收藏', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'appointments',
        name: 'TenantAppointments',
        component: () => import('@/views/tenant/appointments.vue'),
        meta: { title: '我的预约', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'contracts',
        name: 'TenantContracts',
        component: () => import('@/views/tenant/contracts.vue'),
        meta: { title: '我的合同', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'bills',
        name: 'TenantBills',
        component: () => import('@/views/tenant/bills.vue'),
        meta: { title: '账单支付', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'repairs',
        name: 'TenantRepairs',
        component: () => import('@/views/tenant/repairs.vue'),
        meta: { title: '报修管理', requiresAuth: true, roles: ['TENANT'] }
      },
      {
        path: 'wallet',
        name: 'TenantWallet',
        component: () => import('@/views/tenant/wallet.vue'),
        meta: { title: '我的钱包', requiresAuth: true, roles: ['TENANT'] }
      }
    ]
  },
  
  {
    path: '/landlord',
    component: () => import('@/layouts/LandlordLayout.vue'),
    meta: { requiresAuth: true, roles: ['LANDLORD'] },
    children: [
      {
        path: '',
        name: 'LandlordHome',
        component: () => import('@/views/landlord/index.vue'),
        meta: { title: '房东中心', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'profile',
        name: 'LandlordProfile',
        component: () => import('@/views/landlord/profile.vue'),
        meta: { title: '个人中心', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'houses',
        name: 'LandlordHouses',
        component: () => import('@/views/landlord/houses.vue'),
        meta: { title: '房源管理', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'houses/add',
        name: 'LandlordAddHouse',
        component: () => import('@/views/landlord/add-house.vue'),
        meta: { title: '发布房源', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'houses/edit/:id',
        name: 'LandlordEditHouse',
        component: () => import('@/views/landlord/edit-house.vue'),
        meta: { title: '编辑房源', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'appointments',
        name: 'LandlordAppointments',
        component: () => import('@/views/landlord/appointments.vue'),
        meta: { title: '预约处理', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'contracts',
        name: 'LandlordContracts',
        component: () => import('@/views/landlord/contracts.vue'),
        meta: { title: '合同管理', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'bills',
        name: 'LandlordBills',
        component: () => import('@/views/landlord/bills.vue'),
        meta: { title: '账单管理', requiresAuth: true, roles: ['LANDLORD'] }
      },
      {
        path: 'repairs',
        name: 'LandlordRepairs',
        component: () => import('@/views/landlord/repairs.vue'),
        meta: { title: '报修处理', requiresAuth: true, roles: ['LANDLORD'] }
      }
    ]
  },
  
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '数据大屏', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'dashboard',
        name: 'AdminDashboardView',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '数据大屏', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'houses',
        name: 'AdminHouses',
        component: () => import('@/views/admin/houses.vue'),
        meta: { title: '房源列表', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'houses/edit/:id',
        name: 'AdminEditHouse',
        component: () => import('@/views/admin/edit-house.vue'),
        meta: { title: '编辑房源', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'house-audit',
        name: 'AdminHouseAudit',
        component: () => import('@/views/admin/house-audit.vue'),
        meta: { title: '房源审核', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users.vue'),
        meta: { title: '用户管理', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'qualification',
        name: 'AdminQualification',
        component: () => import('@/views/admin/qualification.vue'),
        meta: { title: '资质审核', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'credit',
        name: 'AdminCredit',
        component: () => import('@/views/admin/credit.vue'),
        meta: { title: '信用管理', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'logs',
        name: 'AdminLogs',
        component: () => import('@/views/admin/logs.vue'),
        meta: { title: '系统日志', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'complaints',
        name: 'AdminComplaints',
        component: () => import('@/views/admin/complaints.vue'),
        meta: { title: '投诉仲裁', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/profile.vue'),
        meta: { title: '个人中心', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'test-chart',
        name: 'AdminTestChart',
        component: () => import('@/views/admin/test-chart.vue'),
        meta: { title: '图表测试', requiresAuth: true, roles: ['ADMIN'] }
      }
    ]
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
  
  console.log('=== Route Guard Start ===')
  console.log('to.path:', to.path, 'to.meta:', JSON.stringify(to.meta))
  console.log('from.path:', from.path)
  console.log('userStore.isLoggedIn:', userStore.isLoggedIn)
  console.log('userStore.userType:', userStore.userType)
  console.log('userStore.userInfo:', JSON.stringify(userStore.userInfo))
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('Route guard: Not logged in, redirect to login')
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  if (to.meta.roles && userStore.isLoggedIn && userStore.userInfo) {
    console.log('Route guard: Checking roles. Required:', to.meta.roles, 'UserType:', userStore.userType)
    
    const hasRole = (userStore.userInfo?.roles && userStore.userInfo.roles.some(r => to.meta.roles.includes(r))) || to.meta.roles.includes(userStore.userType);
    
    if (!hasRole) {
      console.log('Route guard: Role mismatch! Redirecting to appropriate dashboard')
      let redirectPath = '/'
      if (userStore.userType === 'ADMIN') {
        redirectPath = '/admin'
      } else if (userStore.userType === 'LANDLORD') {
        redirectPath = '/landlord'
      } else if (userStore.userType === 'TENANT') {
        redirectPath = '/tenant'
      }
      
      console.log('Route guard: Redirecting to:', redirectPath)
      next(redirectPath)
      return
    }
  }
  
  console.log('Route guard: Proceeding to:', to.path)
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
