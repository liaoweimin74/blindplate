import request from './request'

export function getScraps(params?: { status?: string; applicant?: string; page?: number; size?: number }) {
  return request.get('/scraps', { params })
}

export function submitScrap(data: { blindPlateId: number; applicant: string; reason: string }) {
  return request.post('/scraps', data)
}

export function getScrapsByBlindPlate(blindPlateId: number) {
  return request.get(`/scraps/by-plate/${blindPlateId}`)
}

export function approveScrap(id: number, data: { approved: boolean; approver: string; comment?: string }) {
  return request.post(`/scraps/${id}/approve`, data)
}