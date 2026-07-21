import request from './request'
import type { OperationOrder } from '@/types'

export function getOperations() {
  return request.get('/operations')
}

export function getOperation(id: number) {
  return request.get(`/operations/${id}`)
}

export function createOperation(data: Partial<OperationOrder>) {
  return request.post('/operations', data)
}

export function updateOperation(id: number, data: Partial<OperationOrder>) {
  return request.put(`/operations/${id}`, data)
}

export function deleteOperation(id: number) {
  return request.delete(`/operations/${id}`)
}

export function updateOperationStatus(id: number, status: string) {
  return request.patch(`/operations/${id}/status`, { status })
}
