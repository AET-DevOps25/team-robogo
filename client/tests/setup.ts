import { beforeAll, afterEach, afterAll, vi } from 'vitest'
import { setupServer } from 'msw/node'
import { handlers } from './mocks/handlers'

// Create MSW server
const server = setupServer(...handlers)

// Mock $fetch globally for tests
vi.stubGlobal('$fetch', async (url: string, options: any = {}) => {
  // Prepare fetch options
  const fetchOptions: RequestInit = {
    method: options.method || 'GET',
    headers: options.headers || {},
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
      (fetchOptions.headers as Record<string, string>)['Content-Type'] = 
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

// Start server before all tests
beforeAll(() => server.listen())

// Reset handlers after each test
afterEach(() => server.resetHandlers())

// Close server after all tests
afterAll(() => server.close()) 