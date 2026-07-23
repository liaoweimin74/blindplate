import request from './request'

export function getStocktakes(params?: { page?: number; size?: number }) {
  return request.get('/stocktakes', { params })
}

export function getStocktake(id: number) {
  return request.get(`/stocktakes/${id}`)
}

export function createStocktake(data: { batchName: string; operator: string }) {
  return request.post('/stocktakes', data)
}

export function getStocktakeItems(id: number) {
  return request.get(`/stocktakes/${id}/items`)
}

export function scanStocktakeCodes(id: number, codes: string[]) {
  return request.post(`/stocktakes/${id}/scan`, { codes })
}

export function closeStocktake(id: number) {
  return request.post(`/stocktakes/${id}/close`)
}