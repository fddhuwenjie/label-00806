const { getOrderDetail, cancelOrder, confirmOrder, payOrder, deleteOrder } = require('../../api/order')

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return ''
  if (typeof timeStr === 'string' && timeStr.includes('-') && !timeStr.includes('T')) {
    return timeStr
  }
  
  const str = timeStr.replace('T', ' ').replace(/\.\d+/, '')
  const parts = str.split(/[- :]/)
  const date = new Date(parts[0], parts[1] - 1, parts[2], parts[3] || 0, parts[4] || 0, parts[5] || 0)
  
  if (isNaN(date.getTime())) return timeStr
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 解析时间字符串为时间戳（后端返回本地时间）
function parseTime(timeStr) {
  if (!timeStr) return 0
  const str = timeStr.replace('T', ' ').replace(/\.\d+/, '')
  const parts = str.split(/[- :]/)
  const date = new Date(parts[0], parts[1] - 1, parts[2], parts[3] || 0, parts[4] || 0, parts[5] || 0)
  return date.getTime()
}

// 支付超时时间（15分钟）
const PAY_TIMEOUT = 15 * 60 * 1000

Page({
  data: { 
    orderId: null, 
    order: {}, 
    addressInfo: null,
    countdown: '',
    countdownSeconds: 0,
    fromSubmit: false  // 是否从提交订单进入
  },

  countdownTimer: null,

  onLoad(options) {
    this.setData({ 
      orderId: options.id,
      fromSubmit: options.fromSubmit === '1'
    })
    this.loadOrder()
  },

  onUnload() {
    this.clearCountdown()
  },

  clearCountdown() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
      this.countdownTimer = null
    }
  },

  startCountdown(createTime) {
    this.clearCountdown()
    
    const expireTime = parseTime(createTime) + PAY_TIMEOUT
    
    const updateCountdown = () => {
      const now = Date.now()
      const remaining = expireTime - now
      
      if (remaining <= 0) {
        this.clearCountdown()
        this.setData({ countdown: '已超时', countdownSeconds: 0 })
        // 刷新订单状态
        this.loadOrder()
        return
      }
      
      const minutes = Math.floor(remaining / 60000)
      const seconds = Math.floor((remaining % 60000) / 1000)
      this.setData({ 
        countdown: `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`,
        countdownSeconds: Math.floor(remaining / 1000)
      })
    }
    
    updateCountdown()
    this.countdownTimer = setInterval(updateCountdown, 1000)
  },

  async loadOrder() {
    wx.showLoading({ title: '加载中' })
    try {
      const order = await getOrderDetail(this.data.orderId)
      let addressInfo = null
      if (order.addressSnapshot) {
        try { 
          const snapshot = JSON.parse(order.addressSnapshot)
          // 兼容旧格式（name/phone/address）和新格式（contactName/contactPhone/detail）
          addressInfo = {
            contactName: snapshot.contactName || snapshot.name || '',
            contactPhone: snapshot.contactPhone || snapshot.phone || '',
            province: snapshot.province || '',
            city: snapshot.city || '',
            district: snapshot.district || '',
            detail: snapshot.detail || snapshot.address || ''
          }
        } catch (e) {
          console.error('解析地址快照失败:', e)
        }
      }
      // 格式化时间
      order.createTimeFormatted = formatTime(order.createTime)
      order.payTimeFormatted = formatTime(order.payTime)
      order.deliverTimeFormatted = formatTime(order.deliverTime)
      order.completeTimeFormatted = formatTime(order.completeTime)
      this.setData({ order, addressInfo })
      
      // 如果是待支付状态，启动倒计时
      if (order.status === 0 && order.createTime) {
        this.startCountdown(order.createTime)
      } else {
        this.clearCountdown()
        this.setData({ countdown: '', countdownSeconds: 0 })
      }
    } catch (e) {
      console.error(e)
    } finally {
      wx.hideLoading()
    }
  },

  async payOrder() {
    wx.showModal({
      title: '模拟支付',
      content: `确认支付 ¥${this.data.order.actualAmount}？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await payOrder(this.data.orderId)
            wx.showToast({ title: '支付成功', icon: 'success' })
            this.loadOrder()
          } catch (e) {
            console.error(e)
          }
        }
      }
    })
  },

  async cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定取消订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await cancelOrder(this.data.orderId)
            wx.showToast({ title: '已取消', icon: 'success' })
            setTimeout(() => {
              const pages = getCurrentPages()
              if (pages.length > 1) {
                wx.navigateBack()
              } else {
                wx.reLaunch({ url: '/pages/order/order' })
              }
            }, 1500)
          } catch (e) {
            console.error('cancelOrder error:', e)
            wx.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  goToOrderList() {
    // 从提交订单进入时，返回到订单列表页
    wx.reLaunch({ url: '/pages/order/order' })
  },

  async confirmOrder() {
    wx.showModal({
      title: '提示',
      content: '确认已收到商品？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await confirmOrder(this.data.orderId)
            wx.showToast({ title: '已确认收货', icon: 'success' })
            setTimeout(() => {
              // 获取页面栈
              const pages = getCurrentPages()
              if (pages.length > 1) {
                wx.navigateBack()
              } else {
                // 如果没有上一页，跳转到订单列表
                wx.reLaunch({ url: '/pages/order/order' })
              }
            }, 1500)
          } catch (e) {
            console.error('confirmOrder error:', e)
            wx.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onPullDownRefresh() {
    await this.loadOrder()
    wx.stopPullDownRefresh()
  },

  async deleteOrder() {
    wx.showModal({
      title: '提示',
      content: '确定删除此订单吗？删除后不可恢复',
      success: async (res) => {
        if (res.confirm) {
          try {
            await deleteOrder(this.data.orderId)
            wx.showToast({ title: '已删除', icon: 'success' })
            setTimeout(() => {
              const pages = getCurrentPages()
              if (pages.length > 1) {
                wx.navigateBack()
              } else {
                wx.reLaunch({ url: '/pages/order/order' })
              }
            }, 1500)
          } catch (e) {
            console.error('deleteOrder error:', e)
            wx.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      }
    })
  }
})
