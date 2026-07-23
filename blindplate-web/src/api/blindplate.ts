import request from './request'
import type { BlindPlate } from '@/types'

export function getBlindPlates(params?: {
  keyword?: string
  modelType?: string
  material?: string
  status?: string
  lifecycleStatus?: string
  page?: number
  size?: number
}) {
  return request.get('/blindplates', { params })
}

export function getBlindPlate(id: number) {
  return request.get(`/blindplates/${id}`)
}

export function createBlindPlate(data: Partial<BlindPlate>) {
  return request.post('/blindplates', data)
}

export function updateBlindPlate(id: number, data: Partial<BlindPlate>) {
  return request.put(`/blindplates/${id}`, data)
}

export function deleteBlindPlate(id: number) {
  return request.delete(`/blindplates/${id}`)
}

export function getStatusHistory(id: number) {
  return request.get(`/blindplates/${id}/status-history`)
}

export function getInspectionAlerts() {
  return request.get('/blindplates/inspection-alerts')
}