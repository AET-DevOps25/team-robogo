import { describe, it, expect, vi } from 'vitest'
import { AIService } from '@/services/aiService'
import type { SuggestionRequestDTO } from '@/interfaces/dto'

// mock useAuthFetch
vi.mock('@/composables/useAuthFetch', () => ({
  useAuthFetch: vi.fn(() => ({
    authFetch: (url: string, options: { body: string }) => {
      if (typeof url === 'string' && url.includes('/suggestion')) {
        const bodyObj = options && options.body ? JSON.parse(options.body) : {}
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
      return Promise.resolve({})
    }
  }))
}))

// mock useNuxtApp
vi.mock('#app', () => ({
  useNuxtApp: () => ({
    $config: {
      public: {
        aiServiceUrl: 'http://mock-ai-service'
      }
    }
  })
}))

describe('AIService', () => {
  describe('checkHealth', () => {
    it('should successfully get health check information', async () => {
      const result = await AIService.checkHealth()

      expect(result).toBeDefined()
      expect(result.status).toBe('healthy')
      expect(result.service).toBe('genai')
    })
  })

  describe('getSuggestion', () => {
    it('should successfully get AI suggestion', async () => {
      const request: SuggestionRequestDTO = {
        text: 'How to improve code quality?',
        service: 'openwebui'
      }

      const result = await AIService.getSuggestion(request)

      expect(result).toBeDefined()
      expect(result.suggestion).toContain('How to improve code quality?')
      expect(result.suggestion).toContain('openwebui')
    })

    it('should use default service when no service is specified', async () => {
      const request: SuggestionRequestDTO = {
        text: 'Test default service'
      }

      const result = await AIService.getSuggestion(request)

      expect(result).toBeDefined()
      expect(result.suggestion).toContain('Test default service')
      expect(result.suggestion).toContain('openwebui') // default service
    })

    it('should handle error requests', async () => {
      const request: SuggestionRequestDTO = {
        text: 'error' // This will trigger the error response we set in the handler
      }

      await expect(AIService.getSuggestion(request)).rejects.toThrow()
    })
  })

  describe('getServiceInfo', () => {
    it('should successfully get service info', async () => {
      const result = await AIService.getServiceInfo()

      expect(result).toBeDefined()
      expect(result.name).toBe('GenAI Service')
      expect(result.version).toBe('1.0.0')
      expect(result.status).toBe('running')
      expect(result.features).toEqual(['suggestion', 'health-check'])
    })
  })

  describe('error handling', () => {
    it('should log error messages', async () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      const request: SuggestionRequestDTO = {
        text: 'error' // This will trigger the error response
      }

      await expect(AIService.getSuggestion(request)).rejects.toThrow()
      expect(consoleSpy).toHaveBeenCalledWith('Failed to get suggestion:', expect.any(Error))

      consoleSpy.mockRestore()
    })
  })
})
