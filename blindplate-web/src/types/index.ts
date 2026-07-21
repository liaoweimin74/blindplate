export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface User {
  id: number
  username: string
  name: string
  phone: string
  email?: string
  role?: string
  status: number
}

export interface BlindPlate {
  id: number
  code: string
  name: string
  spec: string
  material: string
  diameter: number
  pressure: number
  manufacturer: string
  status: string
  remark: string
  createdAt: string
}

export interface Location {
  id: number
  parentId: number | null
  name: string
  code: string
  description: string
  type: string
  parentName?: string
  children?: Location[]
}

export interface OperationOrder {
  id: number
  orderNo: string
  type: string
  blindplateId: number
  locationId: number
  status: string
  plannedDate: string
  actualDate: string
  remark: string
  createdAt: string
}
