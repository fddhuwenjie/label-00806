const { request } = require('../utils/request')

// 获取购物车列表
function getCartList(merchantId) {
  return request({
    url: '/cart/list',
    data: merchantId ? { merchantId } : {},
    showLoading: false
  })
}

// 添加购物车
function addCart(data) {
  return request({
    url: '/cart/add',
    method: 'POST',
    data,
    showLoading: false
  })
}

// 更新购物车
function updateCart(data) {
  return request({
    url: '/cart/update',
    method: 'PUT',
    data,
    showLoading: false
  })
}

// 删除购物车项
function deleteCart(id) {
  return request({
    url: `/cart/${id}`,
    method: 'DELETE',
    showLoading: false
  })
}

// 清空购物车
function clearCart(merchantId) {
  return request({
    url: '/cart/clear',
    method: 'DELETE',
    data: { merchantId },
    showLoading: false
  })
}

module.exports = { getCartList, addCart, updateCart, deleteCart, clearCart }
