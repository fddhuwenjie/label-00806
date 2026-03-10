import request from '@/utils/request'

export function getMerchantPage(params) {
  return request.get('/merchant/page', { params })
}

export function getMerchant(id) {
  return request.get(`/merchant/${id}`)
}

export function saveMerchant(data) {
  return request.post('/merchant', data)
}

export function updateMerchant(data) {
  return request.put('/merchant', data)
}

export function updateMerchantStatus(data) {
  return request.put('/merchant/status', data)
}

export function deleteMerchant(id) {
  return request.delete(`/merchant/${id}`)
}
