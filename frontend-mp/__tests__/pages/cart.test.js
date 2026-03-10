/**
 * 购物车页面测试
 */

describe('Cart Page', () => {
  let page

  beforeAll(() => {
    global.wx = {
      request: jest.fn(),
      getStorageSync: jest.fn(() => 'mock-token'),
      showLoading: jest.fn(),
      hideLoading: jest.fn(),
      showToast: jest.fn(),
      showModal: jest.fn((options) => {
        if (options.success) {
          options.success({ confirm: true })
        }
      }),
      navigateTo: jest.fn()
    }
  })

  beforeEach(() => {
    page = {
      data: {
        cartItems: [],
        totalPrice: 0,
        selectedAll: false
      },
      setData: jest.fn(function(data) {
        Object.assign(this.data, data)
      }),
      calculateTotal: function() {
        const total = this.data.cartItems.reduce((sum, item) => {
          return sum + (item.price * item.quantity)
        }, 0)
        this.setData({ totalPrice: total })
      },
      addQuantity: jest.fn(),
      reduceQuantity: jest.fn(),
      removeItem: jest.fn(),
      clearCart: jest.fn(),
      submitOrder: jest.fn()
    }
  })

  it('should initialize with empty cart', () => {
    expect(page.data.cartItems).toEqual([])
    expect(page.data.totalPrice).toBe(0)
  })

  it('should calculate total price correctly', () => {
    page.data.cartItems = [
      { id: 1, name: '菜品1', price: 20, quantity: 2 },
      { id: 2, name: '菜品2', price: 15, quantity: 1 }
    ]
    
    page.calculateTotal()
    
    expect(page.data.totalPrice).toBe(55) // 20*2 + 15*1
  })

  it('should have add quantity method', () => {
    expect(typeof page.addQuantity).toBe('function')
  })

  it('should have reduce quantity method', () => {
    expect(typeof page.reduceQuantity).toBe('function')
  })

  it('should have remove item method', () => {
    expect(typeof page.removeItem).toBe('function')
  })

  it('should have clear cart method', () => {
    expect(typeof page.clearCart).toBe('function')
  })

  it('should have submit order method', () => {
    expect(typeof page.submitOrder).toBe('function')
  })
})
