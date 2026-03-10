import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    // Mock localStorage
    vi.stubGlobal('localStorage', {
      getItem: vi.fn(),
      setItem: vi.fn(),
      removeItem: vi.fn()
    })
  })

  it('should initialize with empty user info', async () => {
    const { useUserStore } = await import('../../store/user.js')
    const store = useUserStore()
    
    expect(store.token).toBeFalsy()
  })

  it('should have login action', async () => {
    const { useUserStore } = await import('../../store/user.js')
    const store = useUserStore()
    
    expect(typeof store.login).toBe('function')
  })

  it('should have logout action', async () => {
    const { useUserStore } = await import('../../store/user.js')
    const store = useUserStore()
    
    expect(typeof store.logout).toBe('function')
  })
})
