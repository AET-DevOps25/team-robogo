import { beforeAll, beforeEach, afterEach, afterAll, vi } from 'vitest'
import { setupServer } from 'msw/node'

import { setActivePinia } from 'pinia'
import { createTestingPinia } from '@pinia/testing'
import { handlers } from './mocks/handlers'
import { mockSlides } from '@/data/mockSlides'
import { mockSlideDecks } from '@/data/mockSlideDecks'

const mockScreenContent = {
  id: 1,
  name: 'Screen 1',
  status: 'online',
  slideDeck: mockSlideDecks[0],
  currentContent: '',
  thumbnailUrl: '',
  urlPath: ''
}

// Create MSW server
const server = setupServer(...handlers)

// Mock $fetch globally for tests
vi.stubGlobal('$fetch', async (url: string, options: any = {}) => {
  // Prepare fetch options
  const fetchOptions: RequestInit = {
    method: options.method || 'GET',
    headers: options.headers || {}
  }

  // Handle body - mimic $fetch behavior
  if (options.body !== undefined) {
    if (typeof options.body === 'object' && options.body !== null) {
      // If body is an object, serialize to JSON
      fetchOptions.body = JSON.stringify(options.body)
      // Ensure Content-Type is set
      if (!fetchOptions.headers) {
        fetchOptions.headers = {}
      }
      ;(fetchOptions.headers as Record<string, string>)['Content-Type'] =
        (fetchOptions.headers as Record<string, string>)['Content-Type'] || 'application/json'
    } else {
      // If body is already a string or other type, use directly
      fetchOptions.body = options.body
    }
  }

  const response = await fetch(url, fetchOptions)

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  return await response.json()
})

// 全局 mock useAuthFetch
vi.mock('@/composables/useAuthFetch', () => ({
  useAuthFetch: () => ({
    authFetch: vi.fn((url: string, _options?: any) => {
      if (/\/slide-images\/\d+$/.test(url) && (!_options || _options.method === 'GET')) {
        return new Blob(['mock image content'], { type: 'image/jpeg' })
      }
      if (url.endsWith('/slide-images') && (!_options?.method || _options.method === 'GET')) {
        return [
          { id: 1, name: 'img1', contentType: 'image/jpeg' },
          { id: 2, name: 'img2', contentType: 'image/png' }
        ]
      }
      if (url.includes('/slide-images') && _options && _options.method === 'POST') {
        const formData = _options.body
        if (formData && typeof formData.get === 'function') {
          const file = formData.get('file')
          return {
            id: 99,
            name: file?.name || 'mock.png',
            contentType: file?.type || 'image/png'
          }
        }
        return { id: 99, name: 'mock.png', contentType: 'image/png' }
      }
      if (typeof url === 'string' && url.includes('/suggestion')) {
        const bodyObj = _options && _options.body ? JSON.parse(_options.body) : {}
        if (bodyObj.text === 'error') {
          return Promise.reject(new Error('Mocked error'))
        }
        return Promise.resolve({
          suggestion: `${bodyObj.text || ''} - suggestion from ${bodyObj.service || 'openwebui'}`
        })
      }
      if (typeof url === 'string' && url.includes('/health')) {
        return Promise.resolve({ status: 'healthy', service: 'genai' })
      }
      if ((typeof url === 'string' && url.includes('/service-info')) || url.endsWith('/genai/')) {
        return Promise.resolve({
          name: 'GenAI Service',
          version: '1.0.0',
          status: 'running',
          features: ['suggestion', 'health-check']
        })
      }
      // mock 获取所有 slideDecks
      if (url.endsWith('/slidedecks') && (!_options?.method || _options.method === 'GET')) {
        return mockSlideDecks
      }
      // mock 获取单个 slideDeck
      if (/\/slidedecks\/\d+$/.test(url) && (!_options?.method || _options.method === 'GET')) {
        return mockSlideDecks[0]
      }
      // mock 创建/更新 slideDeck
      if (url.endsWith('/slidedecks') && _options?.method === 'POST') {
        return JSON.parse(_options.body)
      }
      if (/\/slidedecks\/\d+$/.test(url) && _options?.method === 'PUT') {
        return JSON.parse(_options.body)
      }
      // mock addSlideToDeck, reorderSlides
      if (
        /\/slidedecks\/\d+\/slides$/.test(url) ||
        /\/slidedecks\/\d+\/slides\/reorder$/.test(url)
      ) {
        return mockSlideDecks[0]
      }
      // mock 删除 slideDeck
      if (/\/slidedecks\/\d+$/.test(url) && _options?.method === 'DELETE') {
        return undefined
      }
      // mock 获取单个 slide 或移除 slide
      if (/\/slidedecks\/\d+\/slides\/\d+$/.test(url)) {
        const match = url.match(/slidedecks\/(\d+)\/slides\/(\d+)/)
        if (match) {
          const [, _deckId, slideId] = match
          if (_options?.method === 'DELETE') {
            // 返回 mockSlideDecks[0]
            return mockSlideDecks[0]
          }
          // GET 返回单个 slide
          const slide = mockSlides.find(s => String(s.id) === slideId)
          return slide
        }
      }

      // mock assignSlideDeck (必须放在 /screens/:id 之前)
      if (/\/screens\/\d+\/assign-slide-deck\/\d+$/.test(url) && _options?.method === 'POST') {
        const match = url.match(/\/screens\/(\d+)\/assign-slide-deck\/(\w+)/)
        if (match) {
          const [, , deckId] = match
          const deck =
            mockSlideDecks.find(d => String(d.id) === String(deckId)) || mockSlideDecks[0]
          return {
            id: 1,
            name: 'Screen 1',
            status: 'online',
            slideDeck: deck,
            currentContent: '',
            thumbnailUrl: '',
            urlPath: ''
          }
        }

        // fallback
        return {
          id: 1,
          name: 'Screen 1',
          status: 'online',
          slideDeck: mockSlideDecks[0],
          currentContent: '',
          thumbnailUrl: '',
          urlPath: ''
        }
      }
      // mock 获取所有 screens
      if (url.endsWith('/screens') && (!_options?.method || _options.method === 'GET')) {
        return [mockScreenContent]
      }
      // mock 获取单个 screen
      if (/\/screens\/\d+$/.test(url) && (!_options?.method || _options.method === 'GET')) {
        return mockScreenContent
      }
      // mock 创建 screen
      if (url.endsWith('/screens') && _options?.method === 'POST') {
        return { ...mockScreenContent, ...JSON.parse(_options.body) }
      }
      // mock 更新 screen
      if (/\/screens\/\d+$/.test(url) && _options?.method === 'PUT') {
        return { ...mockScreenContent, ...JSON.parse(_options.body) }
      }
      // mock 删除 screen
      if (/\/screens\/\d+$/.test(url) && _options?.method === 'DELETE') {
        return undefined
      }
      // mock fetchScreenContent
      if (
        /\/screens\/\d+\/content$/.test(url) &&
        (!_options?.method || _options.method === 'GET')
      ) {
        return mockScreenContent
      }
      // mock updateScreenStatus
      if (/\/screens\/\d+\/status/.test(url) && _options?.method === 'PUT') {
        return { ...mockScreenContent, status: url.match(/status=([^&]+)/)?.[1] || 'online' }
      }

      return {}
    })
  })
}))

vi.mock('#app', () => ({
  useNuxtApp: () => ({
    $config: {
      public: {
        aiServiceUrl: 'http://mock-ai-service'
      }
    }
  })
}))

// Start server before all tests
beforeAll(() => server.listen())

// Reset handlers after each test
afterEach(() => server.resetHandlers())

// Close server after all tests
afterAll(() => server.close())

beforeEach(() => {
  /**
   * createTestingPinia 会：
   * 1) 返回一个 Pinia 实例
   * 2) 自动 setActivePinia(...)
   * 3) 把 action 默认 stub 成 vi.fn（可通过选项关闭）
   */
  setActivePinia(
    createTestingPinia({
      stubActions: true, // 如果想测试 action 的真实实现可设为 false
      createSpy: vi.fn // 让 Vitest 生成 spy
    })
  )
})
