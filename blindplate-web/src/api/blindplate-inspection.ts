import request from './request'
import type { BlindPlateInspection } from '@/types'

export function getInspectionsByBlindPlate(blindPlateId: number) {
  return request.get(`/blindplates/${blindPlateId}/inspections`)
}

export function createBlindPlateInspection(blindPlateId: number, data: Partial<BlindPlateInspection>) {
  return request.post(`/blindplates/${blindPlateId}/inspections`, data)
}

export function updateBlindPlateInspection(blindPlateId: number, id: number, data: Partial<BlindPlateInspection>) {
  return request.put(`/blindplates/${blindPlateId}/inspections/${id}`, data)
}

export function deleteBlindPlateInspection(blindPlateId: number, id: number) {
  return request.delete(`/blindplates/${blindPlateId}/inspections/${id}`)
}