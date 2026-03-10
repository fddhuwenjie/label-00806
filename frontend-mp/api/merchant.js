const { request } = require('../utils/request')

// 获取商家列表
function getMerchantList(params) {
  // 兼容字符串和对象两种参数形式
  let data = {}
  if (typeof params === 'string') {
    if (params && params.trim()) {
      data.keyword = params.trim()
    }
  } else if (params && typeof params === 'object') {
    if (params.keyword && params.keyword.trim()) {
      data.keyword = params.keyword.trim()
    }
    if (params.page) {
      data.page = params.page
    }
    if (params.pageSize) {
      data.pageSize = params.pageSize
    }
  }
  return request({
    url: '/merchant/list',
    data
  })
}

// 获取商家详情
function getMerchantDetail(id) {
  return request({ url: `/merchant/${id}` })
}

module.exports = { getMerchantList, getMerchantDetail }
