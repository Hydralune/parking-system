import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/LoginView.vue'),
    meta: { requiresGuest: true } // 需要未登录状态
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/RegisterView.vue'),
    meta: { requiresGuest: true } // 需要未登录状态
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { requiresAuth: true } // 需要登录
  },
  {
    path: '/parking-lots',
    name: 'ParkingLots',
    component: () => import('@/views/parking/ParkingLotsView.vue')
  },
  {
    path: '/parking-lots/:id',
    name: 'ParkingLotDetail',
    component: () => import('@/views/parking/ParkingLotDetailView.vue'),
    props: true
  },
  {
    path: '/spots/:parkingLotId',
    name: 'SpotVisualization',
    component: () => import('@/views/parking/SpotVisualizationView.vue'),
    props: true
  },
  {
    path: '/reservations',
    name: 'Reservations',
    component: () => import('@/views/reservation/ReservationsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reservations/create',
    name: 'CreateReservation',
    component: () => import('@/views/reservation/CreateReservationView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/views/reservation/PaymentView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminDashboardView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/parking-lots',
    name: 'AdminParkingLots',
    component: () => import('@/views/admin/ParkingLotManageView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/reservations',
    name: 'AdminReservations',
    component: () => import('@/views/admin/ReservationManageView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/members',
    name: 'AdminMembers',
    component: () => import('@/views/admin/MemberManageView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (userStore.isAuthenticated) {
      // 检查是否需要管理员权限
      if (to.meta.requiresAdmin && userStore.userType !== 1) {
        next('/') // 如果不是管理员，重定向到首页
      } else {
        next()
      }
    } else {
      next('/login')
    }
  } 
  // 需要游客状态的页面（如登录、注册）
  else if (to.meta.requiresGuest) {
    if (userStore.isAuthenticated) {
      next('/') // 已登录用户访问登录页面时重定向到首页
    } else {
      next()
    }
  } 
  // 其他页面
  else {
    next()
  }
})

export default router