/**
 * 请求工具测试
 * 使用 miniprogram-simulate 进行小程序单元测试
 */

const simulate = require('miniprogram-simulate')

describe('Request Utils', () => {
  let request

  beforeAll(() => {
    // Mock wx.request
    global.wx = {
      request: jest.fn((options) => {
        if (options.url.includes('/success')) {
          options.success({
            statusCode: 200,
            data: { code: 1, data: { message: 'success' } }
          })
        } else if (options.url.includes('/error')) {
          options.fail({ errMsg: 'request:fail' })
        } else if (options.url.includes('/401')) {
          options.success({
            statusCode: 401,
            data: { code: 0, msg: '未授权' }
          })
        }
      }),
      getStorageSync: jest.fn(() => 'mock-token'),
      showToast: jest.fn(),
      navigateTo: jest.fn()
    }
  })

  it('should make successful request', (done) => {
    const mockSuccess = jest.fn()
    
    wx.request({
      url: 'https://api.example.com/success',
      success: (res) => {
        expect(res.statusCode).toBe(200)
        expect(res.data.code).toBe(1)
        done()
      }
    })
  })

  it('should handle request failure', (done) => {
    wx.request({
      url: 'https://api.example.com/error',
      fail: (err) => {
        expect(err.errMsg).toBe('request:fail')
        done()
      }
    })
  })

  it('should handle 401 unauthorized', (done) => {
    wx.request({
      url: 'https://api.example.com/401',
      success: (res) => {
        expect(res.statusCode).toBe(401)
        done()
      }
    })
  })
})
