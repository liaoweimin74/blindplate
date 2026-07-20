import request from './request'
import type { BlindPlate, Page } from '@/types'

export function getBlindPlates() {
  return request.get('/blindplates')
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
