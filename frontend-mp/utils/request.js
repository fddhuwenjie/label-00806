const config = require('./config')

/**
 * 发起请求
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const app = getApp()
    const method = options.method || 'GET'
    
    // 显示加载提示
    if (options.showLoading !== false) {
      wx.showLoading({ title: '加载中...', mask: true })
    }
    
    wx.request({
      url: config.baseUrl + options.url,
      method: method,
      data: options.data,
      timeout: options.timeout || 15000,
      header: {
        'Content-Type': 'application/json',
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : ''
      },
      success(res) {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data.data)
          } else {
            wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          app.clearLogin()
          wx.showToast({ title: '请先登录', icon: 'none' })
          reject(res)
        } else if (res.statusCode === 403) {
          app.clearLogin()
          wx.showModal({
            title: '提示',
            content: '账号已被禁用',
            showCancel: false,
            success() {
              wx.reLaunch({ url: '/pages/index/index' })
            }
          })
          reject(res)
        } else {
          wx.showToast({ title: '请求失败', icon: 'none' })
          reject(res)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
      complete() {
        if (options.showLoading !== false) {
          wx.hideLoading()
        }
      }
    })
  })
}

module.exports = { request }
