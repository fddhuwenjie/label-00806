const app = getApp()
const { getAddressList } = require('../../api/address')

Page({
  data: { addresses: [], isSelect: false },

  onLoad(options) {
    this.setData({ isSelect: options.select === '1' })
  },

  onShow() {
    this.loadAddresses()
  },

  async loadAddresses() {
    try {
      const addresses = await getAddressList()
      this.setData({ addresses })
    } catch (e) {
      console.error(e)
    }
  },

  selectAddress(e) {
    if (this.data.isSelect) {
      const item = e.currentTarget.dataset.item
      app.globalData.selectedAddress = item
      wx.navigateBack()
    }
  },

  addAddress() {
    wx.navigateTo({ url: '/pages/address-edit/address-edit' })
  },

  editAddress(e) {
    const item = e.currentTarget.dataset.item
    wx.navigateTo({ url: `/pages/address-edit/address-edit?id=${item.id}` })
  }
})
