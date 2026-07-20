import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/',
    redirect: '/blindplates'
  },
  {
    path: '/blindplates',
    name: 'BlindPlateList',
    component: () => import('@/views/blindplate/BlindPlateList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/locations',
    name: 'LocationTree',
    component: () => import('@/views/location/LocationTree.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/operations',
    name: 'OperationList',
    component: () => import('@/views/operation/OperationList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/inspections',
    name: 'InspectionList',
    component: () => import('@/views/inspection/InspectionList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reports',
    name: 'Dashboard',
    component: () => import('@/views/report/Dashboard.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
