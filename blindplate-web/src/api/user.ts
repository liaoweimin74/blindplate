import request from './request'
import type { User } from '@/types'

export function login(username: string, password: string) {
  return request.post('/auth/login', { username, password })
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get('/auth/me')
}

export function getUsers() {
  return request.get('/users')
}

export function getUser(id: number) {
  return request.get(`/users/${id}`)
}

export function createUser(data: Partial<User>) {
  return request.post('/users', data)
}

export function updateUser(id: number, data: Partial<User>) {
  return request.put(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/users/${id}`)
}

export function resetPassword(id: number) {
  return request.post(`/users/${id}/reset-password`)
}
