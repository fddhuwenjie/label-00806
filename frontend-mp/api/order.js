const { request } = require('../utils/request')

// 提交订单
function submitOrder(data) {
  return request({
    url: '/order/submit',
    method: 'POST',
    data
  })
}

// 获取订单列表
function getOrderList(status, page = 1, pageSize = 10) {
  const data = { page, pageSize }
  // 只有当 status 不为 null/undefined 时才传递
  if (status !== null && status !== undefined) {
    data.status = status
  }
  return request({
    url: '/order/list',
    data
  })
}

// 获取订单详情
function getOrderDetail(id) {
  return request({ url: `/order/${id}` })
}

// 取消订单
function cancelOrder(id) {
  return request({
    url: '/order/cancel',
    method: 'PUT',
    data: { id }
  })
}

// 确认收货
function confirmOrder(id) {
  return request({
    url: '/order/confirm',
    method: 'PUT',
    data: { id }
  })
}

// 模拟支付
function payOrder(id) {
  return request({
    url: '/order/pay',
    method: 'PUT',
    data: { id }
  })
}

// 删除订单
function deleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'DELETE'
  })
}

module.exports = { submitOrder, getOrderList, getOrderDetail, cancelOrder, confirmOrder, payOrder, deleteOrder }
