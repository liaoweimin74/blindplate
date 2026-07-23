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
  modelType: string
  material: string
  diameter: number
  pressure: number
  thickness: number
  manufacturer: string
  factoryCode: string
  purchaseDate: string
  qrCode: string
  rfidTag: string
  currentLocationId: number
  installCount: number
  totalUsageDays: number
  status: string
  lifecycleStatus: string
  nextInspectionDate: string
  remark: string
  createdAt: string
}

export interface BlindPlateStatusHistory {
  id: number
  blindPlateId: number
  previousStatus: string
  newStatus: string
  operator: string
  changedAt: string
}

export interface BlindPlateInspection {
  id: number
  blindPlateId: number
  inspectionDate: string
  result: string
  nextInspectionDate: string
  inspector: string
  remark: string
  createdAt: string
}

export interface BlindPlateScrapRecord {
  id: number
  blindPlateId: number
  applicant: string
  reason: string
  status: string
  applyTime: string
  approver: string
  approveTime: string
  approveComment: string
}

export interface BlindPlateStocktake {
  id: number
  batchNo: string
  batchName: string
  operator: string
  status: string
  createdAt: string
  closedAt: string
}

export interface BlindPlateStocktakeItem {
  id: number
  batchId: number
  blindPlateCode: string
  scannedAt: string
  matchStatus: string
}

export interface Location {
  id: number
  parentId: number | null
  name: string
  code: string
  description: string
  type: string
  level?: number
  parentName?: string
  children?: Location[]
  isolationPointDetail?: IsolationPointDetail
}

export interface IsolationPointDetail {
  id?: number
  locationId?: number
  medium: string
  hazardLevel: string
  isolationType: string
  pressure?: number
  temperature?: number
}

export interface LocationChangeRecord {
  id: number
  locationId: number
  changeType: string
  fieldChanged: string
  oldValue: string
  newValue: string
  applicantId: number
  approverId?: number
  status: string
  appliedAt: string
  approvedAt?: string
  createdAt: string
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