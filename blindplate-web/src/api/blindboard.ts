import request from './request'

export interface BoardProject {
  id: number
  name: string
  svgJson: string
  thumbnail: string
  createdBy: number
  createdAt: string
  updatedAt: string
}

export function getBoardProjects() {
  return request.get('/blindboard/projects')
}

export function getBoardProject(id: number) {
  return request.get(`/blindboard/projects/${id}`)
}

export function createBoardProject(data: { name: string; svgJson: string; thumbnail?: string }) {
  return request.post('/blindboard/projects', data)
}

export function updateBoardProject(id: number, data: { name: string; svgJson: string; thumbnail?: string }) {
  return request.put(`/blindboard/projects/${id}`, data)
}

export function deleteBoardProject(id: number) {
  return request.delete(`/blindboard/projects/${id}`)
}