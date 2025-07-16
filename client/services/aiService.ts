import type { SuggestionRequest, SuggestionResponse, HealthCheckResponse } from '@/interfaces/types'
import { useAuthFetch } from '@/composables/useAuthFetch'

const BASE_URL = '/api/proxy/genai'

/**
 * Check the health of the AI service
 */
export async function checkHealth(): Promise<HealthCheckResponse> {
  const { authFetch } = useAuthFetch()
  try {
    const response = await authFetch<HealthCheckResponse>(`${BASE_URL}/health`)
    return response
  } catch (error) {
    console.error('Failed to check health:', error) // eslint-disable-line no-console
    throw error
  }
}

/**
 * Get AI content suggestion
 */
export async function getSuggestion(request: SuggestionRequest): Promise<SuggestionResponse> {
  const { authFetch } = useAuthFetch()
  try {
    const response = await authFetch<SuggestionResponse>(`${BASE_URL}/suggestion`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    })
    return response
  } catch (error) {
    console.error('Failed to get suggestion:', error) // eslint-disable-line no-console
    throw error
  }
}

/**
 * Get AI service info
 */
export async function getServiceInfo(): Promise<any> {
  const { authFetch } = useAuthFetch()
  try {
    const response = await authFetch<any>(`${BASE_URL}/`)
    return response
  } catch (error) {
    console.error('Failed to get service info:', error) // eslint-disable-line no-console
    throw error
  }
}
