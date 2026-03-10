const app = getApp()
const { getOrderList } = require('../../api/order')

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

Page({
  data: {
    currentTab: null,
    orders: [],
    loading: false,
    page: 1,
    hasMore: true
  },

  onLoad() {
    this.checkLoginAndLoad()
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
    
    // 检查是否从"我的"页面带了状态筛选
    const filterStatus = app.globalData.orderFilterStatus
    // 注意：filterStatus 可能是 0，所以不能用 if(filterStatus) 判断
    const hasFilter = typeof filterStatus === 'number'
    
    if (hasFilter) {
      // 先清除全局变量，避免重复触发
      app.globalData.orderFilterStatus = null
      // 设置 tab 并刷新订单
      this.setData({ currentTab: filterStatus }, () => {
        if (app.checkLogin()) {
          this.loadOrders(true)
        }
      })
    } else if (app.checkLogin()) {
      this.loadOrders(true)
    }
  },

  onPullDownRefresh() {
    this.loadOrders(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadOrders()
    }
  },

  checkLoginAndLoad() {
    if (!app.checkLogin()) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        showCancel: false,
        success: () => {
          wx.switchTab({ url: '/pages/mine/mine' })
        }
      })
      return
    }
    this.loadOrders(true)
  },

  async loadOrders(refresh = false) {
    if (refresh) {
      this.setData({ page: 1, hasMore: true, orders: [] })
    }

    if (!this.data.hasMore) return

    this.setData({ loading: true })
    try {
      const data = await getOrderList(this.data.currentTab, this.data.page)
      // 格式化订单时间
      const records = (data.records || []).map(order => ({
        ...order,
        createTime: formatTime(order.createTime)
      }))
      const orders = refresh ? records : [...this.data.orders, ...records]
      
      this.setData({
        orders,
        page: this.data.page + 1,
        hasMore: orders.length < data.total
      })
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ loading: false })
    }
  },

  switchTab(e) {
    const status = e.currentTarget.dataset.status
    this.setData({ currentTab: status === '' ? null : parseInt(status) })
    this.loadOrders(true)
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
  }
})
