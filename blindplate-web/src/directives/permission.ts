import type { Directive, DirectiveBinding } from 'vue'
import { hasPermission, type Permission } from '@/utils/permissions'

function getUserRole(): string {
  const userStr = localStorage.getItem('user')
  if (!userStr) return ''
  try {
    const user = JSON.parse(userStr)
    return user.role || ''
  } catch {
    return ''
  }
}

export const permissionDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<Permission>) {
    const role = getUserRole()
    const requiredPermission = binding.value
    if (!role || !hasPermission(role, requiredPermission)) {
      el.style.display = 'none'
    }
  },
  updated(el: HTMLElement, binding: DirectiveBinding<Permission>) {
    const role = getUserRole()
    const requiredPermission = binding.value
    if (!role || !hasPermission(role, requiredPermission)) {
      el.style.display = 'none'
    } else {
      el.style.display = ''
    }
  }
}

export function setupPermissionDirective(app: any) {
  app.directive('permission', permissionDirective)
}
