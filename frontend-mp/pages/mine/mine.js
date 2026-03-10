const app = getApp()
const config = require('../../utils/config')
const { login, getUserInfo, updateUserInfo } = require('../../api/auth')

Page({
  data: {
    isLogin: false,
    userInfo: {}
  },

  _uploading: false,

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
    this.checkLogin()
  },

  checkLogin() {
    const isLogin = app.checkLogin()
    this.setData({ isLogin })
    if (isLogin) {
      this.loadUserInfo()
    } else {
      this.setData({ userInfo: {} })
    }
  },

  async loadUserInfo() {
    try {
      const userInfo = await getUserInfo()
      if (!this._uploading) {
        this.setData({ userInfo })
      }
      app.globalData.userInfo = userInfo
    } catch (e) {
      console.error('loadUserInfo error:', e)
    }
  },

  onLogin() {
    wx.login({
      success: async (res) => {
        if (res.code) {
          try {
            const data = await login(res.code)
            app.setToken(data.token)
            this.setData({ isLogin: true })
            this.loadUserInfo()
            wx.showToast({ title: '登录成功', icon: 'success' })
          } catch (e) {
            console.error('login error:', e)
          }
        }
      }
    })
  },

  // 选择头像
  onChooseAvatar(e) {
    const tempPath = e.detail.avatarUrl
    if (!tempPath) return

    this._uploading = true
    const that = this

    // 先读取为 base64 立即显示
    wx.getFileSystemManager().readFile({
      filePath: tempPath,
      encoding: 'base64',
      success(res) {
        const base64Src = 'data:image/png;base64,' + res.data
        that.setData({ 'userInfo.avatar': base64Src })
      },
      fail() {
        that.setData({ 'userInfo.avatar': tempPath })
      }
    })

    // 上传到服务器
    wx.uploadFile({
      url: config.baseUrl + '/file/upload',
      filePath: tempPath,
      name: 'file',
      header: {
        'Authorization': app.globalData.token ? 'Bearer ' + app.globalData.token : ''
      },
      success(res) {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200 && data.data) {
            updateUserInfo({ avatar: data.data }).then(() => {
              that.setData({ 'userInfo.avatar': data.data })
              wx.showToast({ title: '头像已更新', icon: 'success' })
            }).catch(() => {
              wx.showToast({ title: '保存失败', icon: 'none' })
            })
          }
        } catch (e) {
          console.error('upload parse error:', e)
        }
      },
      fail(err) {
        console.error('uploadFile fail:', err)
        wx.showToast({ title: '上传失败', icon: 'none' })
      },
      complete() {
        that._uploading = false
      }
    })
  },

  // 修改昵称
  async onNicknameBlur(e) {
    const nickname = (e.detail.value || '').trim()
    if (!nickname || nickname === this.data.userInfo.nickname) return
    try {
      await updateUserInfo({ nickname })
      this.setData({ 'userInfo.nickname': nickname })
      app.globalData.userInfo.nickname = nickname
      wx.showToast({ title: '昵称已更新', icon: 'success' })
    } catch (e) {
      console.error('update nickname error:', e)
    }
  },

  // 修改手机号
  editPhone() {
    if (!this.data.isLogin) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    wx.showModal({
      title: '修改手机号',
      editable: true,
      placeholderText: '请输入手机号',
      content: this.data.userInfo.phone || '',
      success: async (res) => {
        if (res.confirm && res.content) {
          const phone = res.content.trim()
          if (!/^1[3-9]\d{9}$/.test(phone)) {
            wx.showToast({ title: '手机号格式不正确', icon: 'none' })
            return
          }
          try {
            await updateUserInfo({ phone })
            this.setData({ 'userInfo.phone': phone })
            wx.showToast({ title: '手机号已更新', icon: 'success' })
          } catch (e) {
            console.error('update phone error:', e)
          }
        }
      }
    })
  },

  // 收货地址
  goAddress() {
    if (!this.data.isLogin) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/address/address' })
  },

  // 关于我们
  goAbout() {
    wx.showModal({
      title: '关于美味外卖',
      content: '美味外卖 v1.0.0\n\n让生活更美味，让点餐更便捷。\n\n© 2026 美味外卖',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearLogin()
          this.setData({
            isLogin: false,
            userInfo: {}
          })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  }
})
