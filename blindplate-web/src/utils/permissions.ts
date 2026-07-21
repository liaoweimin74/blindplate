// Permission utilities
export const PERMISSIONS = {
  BLINDPLATE_VIEW: 'blindplate:view',
  BLINDPLATE_CREATE: 'blindplate:create',
  BLINDPLATE_EDIT: 'blindplate:edit',
  BLINDPLATE_DELETE: 'blindplate:delete',
  LOCATION_VIEW: 'location:view',
  LOCATION_CREATE: 'location:create',
  LOCATION_EDIT: 'location:edit',
  LOCATION_DELETE: 'location:delete',
  OPERATION_VIEW: 'operation:view',
  OPERATION_CREATE: 'operation:create',
  OPERATION_EDIT: 'operation:edit',
  OPERATION_DELETE: 'operation:delete',
  INSPECTION_VIEW: 'inspection:view',
  INSPECTION_CREATE: 'inspection:create',
  INSPECTION_EDIT: 'inspection:edit',
  INSPECTION_DELETE: 'inspection:delete',
  USER_VIEW: 'user:view',
  USER_CREATE: 'user:create',
  USER_EDIT: 'user:edit',
  USER_DELETE: 'user:delete',
  SETTINGS_VIEW: 'settings:view',
  SETTINGS_EDIT: 'settings:edit'
} as const

export type Permission = typeof PERMISSIONS[keyof typeof PERMISSIONS]

const ROLE_PERMISSIONS: Record<string, Permission[]> = {
  admin: Object.values(PERMISSIONS),
  manager: [
    PERMISSIONS.BLINDPLATE_VIEW, PERMISSIONS.BLINDPLATE_CREATE, PERMISSIONS.BLINDPLATE_EDIT,
    PERMISSIONS.LOCATION_VIEW, PERMISSIONS.LOCATION_CREATE, PERMISSIONS.LOCATION_EDIT,
    PERMISSIONS.OPERATION_VIEW, PERMISSIONS.OPERATION_CREATE, PERMISSIONS.OPERATION_EDIT,
    PERMISSIONS.INSPECTION_VIEW, PERMISSIONS.INSPECTION_CREATE, PERMISSIONS.INSPECTION_EDIT,
    PERMISSIONS.USER_VIEW
  ],
  operator: [
    PERMISSIONS.BLINDPLATE_VIEW,
    PERMISSIONS.LOCATION_VIEW,
    PERMISSIONS.OPERATION_VIEW, PERMISSIONS.OPERATION_CREATE,
    PERMISSIONS.INSPECTION_VIEW, PERMISSIONS.INSPECTION_CREATE
  ]
}

export function hasPermission(userRole: string, permission: Permission): boolean {
  const permissions = ROLE_PERMISSIONS[userRole] || []
  return permissions.includes(permission)
}

export function getPermissions(userRole: string): Permission[] {
  return ROLE_PERMISSIONS[userRole] || []
}
