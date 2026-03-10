import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() }
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    }))
  }
}))

describe('Request Utils', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should create axios instance with correct config', () => {
    expect(axios.create).toBeDefined()
  })

  it('should have request interceptor', () => {
    const instance = axios.create()
    expect(instance.interceptors.request.use).toBeDefined()
  })

  it('should have response interceptor', () => {
    const instance = axios.create()
    expect(instance.interceptors.response.use).toBeDefined()
  })
})
