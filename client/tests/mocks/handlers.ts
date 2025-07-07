import { http, HttpResponse } from 'msw'
import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'

export const handlers = [
  // 健康检查接口
  http.get('/api/proxy/genai/health', () => {
    return HttpResponse.json<HealthCheckResponseDTO>({
      status: 'healthy',
      service: 'genai'
    })
  }),

  // 获取建议接口
  http.post('/api/proxy/genai/suggestion', async ({ request }) => {
    const body = await request.json() as SuggestionRequestDTO
    
    // 模拟不同的请求场景
    if (body.text === 'error') {
      return HttpResponse.json(
        { error: 'Invalid request' },
        { status: 400 }
      )
    }
    
    if (body.text === 'timeout') {
      // 模拟超时
      await new Promise(resolve => setTimeout(resolve, 5000))
    }
    
    return HttpResponse.json<SuggestionResponseDTO>({
      suggestion: `这是针对 "${body.text}" 的AI建议内容。服务类型: ${body.service || 'openwebui'}`
    })
  }),

  // 获取服务信息接口
  http.get('/api/proxy/genai/', () => {
    return HttpResponse.json({
      name: 'GenAI Service',
      version: '1.0.0',
      status: 'running',
      features: ['suggestion', 'health-check']
    })
  }),

  // 模拟网络错误的处理器
  http.get('/api/proxy/genai/error', () => {
    return HttpResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    )
  })
] 