import { createRouter, createWebHistory } from 'vue-router'
import { store } from './store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('./views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('./views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/routes',
    name: 'Routes',
    component: () => import('./views/RoutesManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/routes/:id',
    name: 'RouteDetail',
    component: () => import('./views/RouteDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/health',
    name: 'Health',
    component: () => import('./views/HealthMonitor.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/audit',
    name: 'Audit',
    component: () => import('./views/AuditLog.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('./views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth !== false
  const isLoggedIn = store.state.auth.isLoggedIn

  if (requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router

