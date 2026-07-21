import request from './request'

export interface InspectionPlan {
  id: number
  name: string
  frequency: string
  status: string
  nextDate: string
  description: string
  createdAt: string
}

export function getInspections() {
  return request.get('/inspections')
}

export function getInspection(id: number) {
  return request.get(`/inspections/${id}`)
}

export function createInspection(data: Partial<InspectionPlan>) {
  return request.post('/inspections', data)
}

export function updateInspection(id: number, data: Partial<InspectionPlan>) {
  return request.put(`/inspections/${id}`, data)
}

export function deleteInspection(id: number) {
  return request.delete(`/inspections/${id}`)
}