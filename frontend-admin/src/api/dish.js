import request from '@/utils/request'

export function getDishPage(params) {
  return request.get('/dish/page', { params })
}

export function getDish(id) {
  return request.get(`/dish/${id}`)
}

export function saveDish(data) {
  return request.post('/dish', data)
}

export function updateDish(data) {
  return request.put('/dish', data)
}

export function updateDishStatus(data) {
  return request.put('/dish/status', data)
}

export function deleteDish(id) {
  return request.delete(`/dish/${id}`)
}
