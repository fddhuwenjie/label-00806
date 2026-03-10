App({
  globalData: {
    userInfo: null,
    token: ''
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
    }
  },

  // 检查登录状态
  checkLogin() {
    return !!this.globalData.token
  },

  // 检查登录，未登录则跳转到我的页面
  requireLogin() {
    if (!this.globalData.token) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/mine/mine' })
      }, 1000)
      return false
    }
    return true
  },

  // 设置Token
  setToken(token) {
    this.globalData.token = token
    wx.setStorageSync('token', token)
  },

  // 清除登录信息
  clearLogin() {
    this.globalData.token = ''
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
  }
})
