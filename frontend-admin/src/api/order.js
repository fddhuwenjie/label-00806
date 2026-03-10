import request from '@/utils/request'

export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function getOrder(id) {
  return request.get(`/order/${id}`)
}

export function updateOrderStatus(data) {
  return request.put('/order/status', data)
}
