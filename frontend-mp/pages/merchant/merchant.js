const app = getApp()
const { getMerchantDetail } = require('../../api/merchant')
const { getCartList, addCart, updateCart } = require('../../api/cart')

Page({
  data: {
    merchantId: '',
    merchant: {},
    categories: [],
    currentCate: 0,
    scrollId: '',
    cartNum: 0,
    cartTotal: '0.00',
    canPay: false,
    payText: '去结算'
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ merchantId: options.id })
      this.loadData()
    }
  },

  onShow() {
    if (this.data.merchantId) {
      this.loadCart()
    }
  },

  // 加载商家数据
  async loadData() {
    wx.showLoading({ title: '加载中' })
    try {
      const res = await getMerchantDetail(this.data.merchantId)
      this.setData({
        merchant: res.merchant || res,
        categories: res.categories || []
      })
      wx.setNavigationBarTitle({ title: this.data.merchant.name || '商家' })
      await this.loadCart()
    } catch (err) {
      console.error('loadData error:', err)
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
    wx.hideLoading()
  },

  // 加载购物车
  async loadCart() {
    if (!app.checkLogin()) {
      this.updateCartUI([])
      return
    }
    try {
      const list = await getCartList(this.data.merchantId) || []
      this.updateCartUI(list)
    } catch (err) {
      console.error(err)
    }
  },

  // 更新购物车UI
  updateCartUI(cartList) {
    let totalNum = 0
    let totalPrice = 0
    
    const categories = this.data.categories.map(cat => ({
      ...cat,
      dishes: (cat.dishes || []).map(dish => {
        const cartItem = cartList.find(c => c.dishId === dish.id)
        const num = cartItem ? cartItem.quantity : 0
        totalNum += num
        totalPrice += dish.price * num
        return {
          ...dish,
          num: num,
          cartId: cartItem ? cartItem.id : null
        }
      })
    }))

    const minPrice = this.data.merchant.minPrice || 0
    const canPay = totalNum > 0 && totalPrice >= minPrice
    let payText = '去结算'
    if (totalNum === 0) {
      payText = `¥${minPrice}起送`
    } else if (!canPay) {
      payText = `差¥${(minPrice - totalPrice).toFixed(2)}起送`
    }

    this.setData({
      categories: categories,
      cartNum: totalNum,
      cartTotal: totalPrice.toFixed(2),
      canPay: canPay,
      payText: payText
    })
  },

  // 点击分类
  onTapCate(e) {
    const index = e.currentTarget.dataset.index
    this.setData({
      currentCate: index,
      scrollId: 'cate' + index
    })
  },

  // 加一个
  onPlus(e) {
    if (!app.checkLogin()) {
      // 记住当前商家ID，登录后返回
      app.globalData.pendingMerchantId = this.data.merchantId
      wx.showModal({
        title: '提示',
        content: '请先登录后再添加商品',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.switchTab({ url: '/pages/mine/mine' })
          }
        }
      })
      return
    }
    const dishId = e.currentTarget.dataset.id
    
    addCart({
      merchantId: this.data.merchantId,
      dishId: dishId,
      quantity: 1
    }).then(() => {
      return this.loadCart()
    }).catch(err => {
      console.error(err)
    })
  },

  // 减一个
  onMinus(e) {
    const { cartid, num } = e.currentTarget.dataset
    if (!cartid) return
    
    updateCart({
      id: cartid,
      quantity: num - 1
    }).then(() => {
      return this.loadCart()
    }).catch(err => {
      console.error(err)
    })
  },

  // 去结算
  onPay() {
    if (!this.data.canPay) {
      if (this.data.cartNum === 0) {
        wx.showToast({ title: '请先选择商品', icon: 'none' })
      } else {
        wx.showToast({ title: this.data.payText, icon: 'none' })
      }
      return
    }
    wx.navigateTo({
      url: '/pages/cart/cart?merchantId=' + this.data.merchantId + '&checkout=1'
    })
  }
})
