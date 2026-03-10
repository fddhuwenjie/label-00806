import request from '@/utils/request'

export function getUserPage(params) {
  return request.get('/user/page', { params })
}

export function updateUserStatus(data) {
  return request.put('/user/status', data)
}
