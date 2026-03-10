const app = getApp()
const { getCartList } = require('../../api/cart')
const { getDefaultAddress } = require('../../api/address')
const { submitOrder } = require('../../api/order')
const { getMerchantDetail } = require('../../api/merchant')

Page({
  data: {
    merchantId: null,
    merchantName: '',
    cartItems: [],
    address: null,
    remark: '',
    totalAmount: '0.00',
    deliveryFee: '0.00',
    actualAmount: '0.00'
  },

  onLoad(options) {
    this.setData({ merchantId: options.merchantId })
    this.loadData()
  },

  onShow() {
    // 从地址页面返回时刷新地址
    const selectedAddress = app.globalData.selectedAddress
    if (selectedAddress) {
      this.setData({ address: selectedAddress })
      app.globalData.selectedAddress = null
    }
  },

  async loadData() {
    wx.showLoading({ title: '加载中' })
    try {
      // 获取商家信息
      const merchantData = await getMerchantDetail(this.data.merchantId)
      
      // 获取购物车
      const cartItems = await getCartList(this.data.merchantId)
      
      // 获取默认地址
      let address = null
      try {
        address = await getDefaultAddress()
      } catch (e) {}

      // 计算金额
      let totalAmount = 0
      cartItems.forEach(item => {
        totalAmount += item.dishPrice * item.quantity
      })
      const deliveryFee = merchantData.merchant.deliveryFee || 0
      const actualAmount = totalAmount + deliveryFee

      this.setData({
        merchantName: merchantData.merchant.name,
        cartItems,
        address,
        totalAmount: totalAmount.toFixed(2),
        deliveryFee: deliveryFee.toFixed(2),
        actualAmount: actualAmount.toFixed(2)
      })
    } catch (e) {
      console.error(e)
    } finally {
      wx.hideLoading()
    }
  },

  selectAddress() {
    wx.navigateTo({ url: '/pages/address/address?select=1' })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async submitOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }

    if (this.data.cartItems.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }

    wx.showLoading({ title: '提交中' })
    try {
      const order = await submitOrder({
        merchantId: this.data.merchantId,
        addressId: this.data.address.id,
        remark: this.data.remark
      })

      wx.hideLoading()
      // 使用 reLaunch 清空页面栈，防止返回购物车重复下单
      // 传递 fromSubmit=1 标记是从提交订单进入
      wx.reLaunch({ url: `/pages/order-detail/order-detail?id=${order.id}&fromSubmit=1` })
    } catch (e) {
      console.error(e)
      wx.hideLoading()
    }
  }
})
