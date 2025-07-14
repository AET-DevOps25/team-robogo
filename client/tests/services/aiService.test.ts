import { describe, it, expect, vi } from 'vitest'
import { checkHealth, getSuggestion, getServiceInfo } from '@/services/aiService'
import type { SuggestionRequest } from '@/interfaces/types'

describe('AIService', () => {
  describe('checkHealth', () => {
    it('should successfully get health check information', async () => {
      const result = await checkHealth()

      expect(result).toBeDefined()
      expect(result.status).toBe('healthy')
      expect(result.service).toBe('genai')
    })
  })

  describe('getSuggestion', () => {
    it('should successfully get AI suggestion', async () => {
      const request: SuggestionRequest = {
        text: 'How to improve code quality?',
        service: 'openwebui'
      }

      const result = await getSuggestion(request)

      expect(result).toBeDefined()
      expect(result.suggestion).toContain('How to improve code quality?')
      expect(result.suggestion).toContain('openwebui')
    })

    it('should use default service when no service is specified', async () => {
      const request: SuggestionRequest = {
        text: 'Test default service'
      }

      const result = await getSuggestion(request)

      expect(result).toBeDefined()
      expect(result.suggestion).toContain('Test default service')
      expect(result.suggestion).toContain('openwebui') // default service
    })

    it('should handle error requests', async () => {
      const request: SuggestionRequest = {
        text: 'error' // This will trigger the error response we set in the handler
      }

      await expect(getSuggestion(request)).rejects.toThrow()
    })
  })

  describe('getServiceInfo', () => {
    it('should successfully get service info', async () => {
      const result = await getServiceInfo()

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

      const request: SuggestionRequest = {
        text: 'error' // This will trigger the error response
      }

      await expect(getSuggestion(request)).rejects.toThrow()
      expect(consoleSpy).toHaveBeenCalledWith('Failed to get suggestion:', expect.any(Error))

      consoleSpy.mockRestore()
    })
  })
})
