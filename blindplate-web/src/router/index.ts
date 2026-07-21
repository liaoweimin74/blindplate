import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/',
    component: AppLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/report/Dashboard.vue'),
        meta: { requiresAuth: true, title: 'Dashboard', closable: true }
      },
      {
        path: 'blindplates',
        name: 'BlindPlateList',
        component: () => import('@/views/blindplate/BlindPlateList.vue'),
        meta: { requiresAuth: true, title: 'Blind Plates', closable: true }
      },
      {
        path: 'locations',
        name: 'LocationTree',
        component: () => import('@/views/location/LocationTree.vue'),
        meta: { requiresAuth: true, title: 'Locations', closable: true }
      },
      {
        path: 'operations',
        name: 'OperationList',
        component: () => import('@/views/operation/OperationList.vue'),
        meta: { requiresAuth: true, title: 'Operations', closable: true }
      },
      {
        path: 'inspections',
        name: 'InspectionList',
        component: () => import('@/views/inspection/InspectionList.vue'),
        meta: { requiresAuth: true, title: 'Inspections', closable: true }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/system/UserList.vue'),
        meta: { requiresAuth: true, title: 'Users', closable: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/system/Settings.vue'),
        meta: { requiresAuth: true, title: 'Settings', closable: true }
      }
    ]
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
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
