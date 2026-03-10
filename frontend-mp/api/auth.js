const { request } = require('../utils/request')

// 微信登录
function login(code) {
  return request({
    url: '/auth/login',
    method: 'POST',
    data: { code }
  })
}

// 获取用户信息
function getUserInfo() {
  return request({ url: '/auth/info' })
}

// 更新用户信息
function updateUserInfo(data) {
  return request({
    url: '/auth/info',
    method: 'PUT',
    data
  })
}

module.exports = { login, getUserInfo, updateUserInfo }
