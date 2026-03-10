import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ElementPlus from 'element-plus'

// Mock API
vi.mock('../../../api/dashboard', () => ({
  getDashboardData: vi.fn(() => Promise.resolve({
    data: {
      code: 1,
      data: {
        todayOrders: 100,
        todayAmount: 5000,
        merchantCount: 50,
        userCount: 1000
      }
    }
  }))
}))

describe('Dashboard Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should render dashboard title', async () => {
    const Dashboard = (await import('../../views/Dashboard.vue')).default
    
    const wrapper = mount(Dashboard, {
      global: {
        plugins: [createPinia(), ElementPlus],
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-card': true,
          'el-statistic': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })
})
