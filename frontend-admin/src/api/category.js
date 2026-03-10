import request from '@/utils/request'

export function getCategoryList(merchantId) {
  return request.get('/category/list', { params: { merchantId } })
}

export function saveCategory(data) {
  return request.post('/category', data)
}

export function updateCategory(data) {
  return request.put('/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/category/${id}`)
}
