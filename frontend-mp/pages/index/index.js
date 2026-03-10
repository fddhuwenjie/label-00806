const { getMerchantList } = require('../../api/merchant')

let searchTimer = null
const PAGE_SIZE = 10

Page({
  data: {
    merchants: [],
    keyword: '',
    loading: false,
    refreshing: false,
    page: 1,
    hasMore: true,
    showScrollbar: false
  },

  onLoad() {
    this.loadMerchants(true)
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
  },

  onPullDownRefresh() {
    this.loadMerchants(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 下拉刷新（scroll-view）
  onRefresh() {
    this.setData({ refreshing: true })
    this.loadMerchants(true).finally(() => {
      this.setData({ refreshing: false })
    })
  },

  // 加载更多
  loadMore() {
    if (this.data.loading || !this.data.hasMore) return
    this.loadMerchants(false)
  },

  async loadMerchants(isRefresh = false) {
    if (this.data.loading) return
    
    const page = isRefresh ? 1 : this.data.page
    
    this.setData({ loading: true })
    
    try {
      const result = await getMerchantList({
        keyword: this.data.keyword,
        page,
        pageSize: PAGE_SIZE
      })
      
      // 兼容不同的返回格式
      const records = result.records || result || []
      const total = result.total || records.length
      
      const merchants = isRefresh ? records : [...this.data.merchants, ...records]
      const hasMore = merchants.length < total
      
      this.setData({
        merchants,
        page: page + 1,
        hasMore
      })
      
      // 判断是否需要显示滚动条（内容超出屏幕时显示）
      this.checkScrollbar()
    } catch (e) {
      console.error('加载商家列表失败:', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onSearchInput(e) {
    const keyword = e.detail.value
    this.setData({ keyword })
    
    // 防抖：300ms 后执行搜索
    if (searchTimer) {
      clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
      this.loadMerchants(true)
    }, 300)
  },

  // 搜索确认
  onSearch() {
    this.loadMerchants(true)
  },

  // 清除搜索
  clearSearch() {
    this.setData({ keyword: '' })
    this.loadMerchants(true)
  },

  goMerchant(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/merchant/merchant?id=${id}` })
  },

  // 检查是否需要显示滚动条
  checkScrollbar() {
    const query = wx.createSelectorQuery()
    query.select('.merchant-list').boundingClientRect()
    query.selectAll('.merchant-item').boundingClientRect()
    query.exec((res) => {
      if (res[0] && res[1]) {
        const containerHeight = res[0].height
        // 计算所有商家项的总高度
        let contentHeight = 0
        res[1].forEach(item => {
          contentHeight += item.height + 24 // 24rpx 是 margin-bottom
        })
        // 内容超出容器时显示滚动条
        this.setData({
          showScrollbar: contentHeight > containerHeight
        })
      }
    })
  }
})
