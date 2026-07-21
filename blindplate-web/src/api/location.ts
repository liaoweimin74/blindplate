import request from './request'
import type { Location } from '@/types'

export function getLocations() {
  return request.get('/locations')
}

export function getLocationTree() {
  return request.get('/locations/tree')
}

export function getLocation(id: number) {
  return request.get(`/locations/${id}`)
}

export function createLocation(data: Partial<Location>) {
  return request.post('/locations', data)
}

export function updateLocation(id: number, data: Partial<Location>) {
  return request.put(`/locations/${id}`, data)
}

export function deleteLocation(id: number) {
  return request.delete(`/locations/${id}`)
}
