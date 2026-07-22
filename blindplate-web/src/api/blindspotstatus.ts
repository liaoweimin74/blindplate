import request from './request'
import type { BlindSpotStatusFilter } from '@/types'

export function getBlindSpotStatusList(filter?: BlindSpotStatusFilter) {
  return request.get('/blind-spot-status', { params: filter })
}

export function getBlindSpotStatusHistory(locationId: number) {
  return request.get(`/blind-spot-status/${locationId}/history`)
}
