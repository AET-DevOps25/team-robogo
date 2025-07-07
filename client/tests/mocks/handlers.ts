import { http, HttpResponse } from 'msw'
import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'

export const handlers = [
  // Health check endpoint
  http.get('/api/proxy/genai/health', () => {
    return HttpResponse.json<HealthCheckResponseDTO>({
      status: 'healthy',
      service: 'genai'
    })
  }),

  // Get suggestion endpoint
  http.post('/api/proxy/genai/suggestion', async ({ request }) => {
    try {
      const body = (await request.json()) as SuggestionRequestDTO

      // Simulate different request scenarios
      if (body.text === 'error') {
        return HttpResponse.json({ error: 'Invalid request' }, { status: 400 })
      }

      if (body.text === 'timeout') {
        // Simulate timeout
        await new Promise(resolve => setTimeout(resolve, 5000))
      }

      return HttpResponse.json<SuggestionResponseDTO>({
        suggestion: `This is AI suggestion content for "${body.text}". Service type: ${body.service || 'openwebui'}`
      })
    } catch (error) {
      return HttpResponse.json({ error: 'Internal server error' }, { status: 500 })
    }
  }),

  // Get service info endpoint
  http.get('/api/proxy/genai/', () => {
    return HttpResponse.json({
      name: 'GenAI Service',
      version: '1.0.0',
      status: 'running',
      features: ['suggestion', 'health-check']
    })
  }),

  // Simulate network error handler
  http.get('/api/proxy/genai/error', () => {
    return HttpResponse.json({ error: 'Internal server error' }, { status: 500 })
  })
]
