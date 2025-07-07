import { describe, it, expect, vi } from 'vitest'
import { AIService } from '@/services/aiService'
import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'

describe('AIService', () => {
  describe('checkHealth', () => {
    it('应该成功获取健康检查信息', async () => {
      const result = await AIService.checkHealth()
      
      expect(result).toBeDefined()
      expect(result.status).toBe('healthy')
      expect(result.service).toBe('genai')
    })

    it('应该处理健康检查失败的情况', async () => {
      // 这里可以通过修改handler来模拟失败场景
      // 或者直接mock fetch来测试错误处理
      const originalFetch = global.fetch
      global.fetch = vi.fn().mockResolvedValueOnce({
        ok: false,
        status: 500
      })

      await expect(AIService.checkHealth()).rejects.toThrow('HTTP error! status: 500')
      
      global.fetch = originalFetch
    })
  })

  describe('getSuggestion', () => {
    it('应该成功获取AI建议', async () => {
      const request: SuggestionRequestDTO = {
        text: '如何提高代码质量？',
        service: 'openwebui'
      }

      const result = await AIService.getSuggestion(request)
      
      expect(result).toBeDefined()
      expect(result.suggestion).toContain('如何提高代码质量？')
      expect(result.suggestion).toContain('openwebui')
    })

    it('应该使用默认服务当未指定服务时', async () => {
      const request: SuggestionRequestDTO = {
        text: '测试默认服务'
      }

      const result = await AIService.getSuggestion(request)
      
      expect(result).toBeDefined()
      expect(result.suggestion).toContain('测试默认服务')
      expect(result.suggestion).toContain('openwebui') // 默认服务
    })

    it('应该处理错误请求', async () => {
      const request: SuggestionRequestDTO = {
        text: 'error' // 这会触发我们在handler中设置的错误响应
      }

      await expect(AIService.getSuggestion(request)).rejects.toThrow('HTTP error! status: 400')
    })

    it('应该处理网络错误', async () => {
      const originalFetch = global.fetch
      global.fetch = vi.fn().mockRejectedValueOnce(new Error('Network error'))

      const request: SuggestionRequestDTO = {
        text: '测试网络错误'
      }

      await expect(AIService.getSuggestion(request)).rejects.toThrow('Network error')
      
      global.fetch = originalFetch
    })
  })

  describe('getServiceInfo', () => {
    it('应该成功获取服务信息', async () => {
      const result = await AIService.getServiceInfo()
      
      expect(result).toBeDefined()
      expect(result.name).toBe('GenAI Service')
      expect(result.version).toBe('1.0.0')
      expect(result.status).toBe('running')
      expect(result.features).toEqual(['suggestion', 'health-check'])
    })

    it('应该处理服务信息获取失败', async () => {
      const originalFetch = global.fetch
      global.fetch = vi.fn().mockResolvedValueOnce({
        ok: false,
        status: 503
      })

      await expect(AIService.getServiceInfo()).rejects.toThrow('HTTP error! status: 503')
      
      global.fetch = originalFetch
    })
  })

  describe('错误处理', () => {
    it('应该正确处理JSON解析错误', async () => {
      const originalFetch = global.fetch
      global.fetch = vi.fn().mockResolvedValueOnce({
        ok: true,
        json: vi.fn().mockRejectedValueOnce(new Error('Invalid JSON'))
      })

      await expect(AIService.checkHealth()).rejects.toThrow('Invalid JSON')
      
      global.fetch = originalFetch
    })

    it('应该记录错误日志', async () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      const originalFetch = global.fetch
      global.fetch = vi.fn().mockRejectedValueOnce(new Error('Test error'))

      await expect(AIService.checkHealth()).rejects.toThrow('Test error')
      
      expect(consoleSpy).toHaveBeenCalledWith('AI服务健康检查失败:', expect.any(Error))
      
      consoleSpy.mockRestore()
      global.fetch = originalFetch
    })
  })
}) 