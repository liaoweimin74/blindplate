import request from './request'
import type { Location, LocationChangeRecord } from '@/types'

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

export function getIsolationPointDetail(locationId: number) {
  return request.get(`/locations/${locationId}/isolation-point-detail`)
}

export function updateIsolationPointDetail(locationId: number, data: any) {
  return request.put(`/locations/${locationId}/isolation-point-detail`, data)
}

export function getChangeRecords(status?: string) {
  const params = status ? { status } : {}
  return request.get('/locations/change-records', { params })
}

export function approveChangeRecord(id: number, data: { approved: boolean; comment: string }) {
  return request.put(`/locations/change-records/${id}/approve`, data)
}

export function importIsolationPoints(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/isolation-points/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
