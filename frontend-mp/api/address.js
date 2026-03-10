const { request } = require('../utils/request')

// 获取地址列表
function getAddressList() {
  return request({ url: '/address/list' })
}

// 获取默认地址
function getDefaultAddress() {
  return request({ url: '/address/default' })
}

// 新增地址
function saveAddress(data) {
  return request({
    url: '/address',
    method: 'POST',
    data
  })
}

// 更新地址
function updateAddress(data) {
  return request({
    url: '/address',
    method: 'PUT',
    data
  })
}

// 删除地址
function deleteAddress(id) {
  return request({
    url: `/address/${id}`,
    method: 'DELETE'
  })
}

module.exports = { getAddressList, getDefaultAddress, saveAddress, updateAddress, deleteAddress }
