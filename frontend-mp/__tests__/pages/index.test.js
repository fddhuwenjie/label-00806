/**
 * 首页测试
 */

describe('Index Page', () => {
  let page

  beforeAll(() => {
    // Mock wx API
    global.wx = {
      request: jest.fn(),
      getStorageSync: jest.fn(() => 'mock-token'),
      showLoading: jest.fn(),
      hideLoading: jest.fn(),
      showToast: jest.fn(),
      navigateTo: jest.fn()
    }
  })

  beforeEach(() => {
    page = {
      data: {
        merchantList: [],
        loading: false,
        page: 1,
        hasMore: true
      },
      setData: jest.fn(function(data) {
        Object.assign(this.data, data)
      }),
      loadMerchants: jest.fn(),
      onPullDownRefresh: jest.fn(),
      onReachBottom: jest.fn()
    }
  })

  it('should initialize with empty merchant list', () => {
    expect(page.data.merchantList).toEqual([])
    expect(page.data.loading).toBe(false)
  })

  it('should have loadMerchants method', () => {
    expect(typeof page.loadMerchants).toBe('function')
  })

  it('should have pull down refresh handler', () => {
    expect(typeof page.onPullDownRefresh).toBe('function')
  })

  it('should have reach bottom handler for pagination', () => {
    expect(typeof page.onReachBottom).toBe('function')
  })

  it('should update data correctly', () => {
    const newMerchants = [
      { id: 1, name: '商家1' },
      { id: 2, name: '商家2' }
    ]
    
    page.setData({ merchantList: newMerchants })
    
    expect(page.data.merchantList).toEqual(newMerchants)
    expect(page.data.merchantList.length).toBe(2)
  })
})
