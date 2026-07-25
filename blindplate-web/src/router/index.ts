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
        meta: { requiresAuth: true, titleKey: 'menu.dashboard', closable: true }
      },
      {
        path: 'blindplates',
        name: 'BlindPlateList',
        component: () => import('@/views/blindplate/BlindPlateList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.blindplates', closable: true }
      },
      {
        path: 'stocktakes',
        name: 'StocktakeList',
        component: () => import('@/views/blindplate/StocktakeList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.stocktakes', closable: true }
      },
      {
        path: 'locations',
        name: 'LocationTree',
        component: () => import('@/views/location/LocationTree.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.locations', closable: true }
      },
      {
        path: 'isolation-point-import',
        name: 'IsolationPointImport',
        component: () => import('@/views/location/IsolationPointImport.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.isolationPointImport', closable: true }
      },
      {
        path: 'change-approval',
        name: 'ChangeApproval',
        component: () => import('@/views/location/ChangeApproval.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.changeApproval', closable: true }
      },
      {
        path: 'blind-spot-status',
        name: 'BlindSpotStatusList',
        component: () => import('@/views/blindspotstatus/BlindSpotStatusList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.blindSpotStatus', closable: true }
      },
      {
        path: 'operations',
        name: 'OperationList',
        component: () => import('@/views/operation/OperationList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.operations', closable: true }
      },
      {
        path: 'inspections',
        name: 'InspectionList',
        component: () => import('@/views/inspection/InspectionList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.inspections', closable: true }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/system/UserList.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.users', closable: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/system/Settings.vue'),
meta: { requiresAuth: true, titleKey: 'menu.settings', closable: true }
      },
      {
        path: 'blindplate-editor',
        name: 'BlindBoardEditor',
        component: () => import('@/views/blindboard/BlindBoardEditor.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.blindplateEditor', closable: true }
      },
      {
        path: 'blindplate-preview/:id',
        name: 'BlindBoardPreview',
        component: () => import('@/views/blindboard/BlindBoardPreview.vue'),
        meta: { requiresAuth: true, titleKey: 'menu.blindplatePreview', closable: true }
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
