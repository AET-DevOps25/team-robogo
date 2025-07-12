import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'
import { useAuthFetch } from '@/composables/useAuthFetch'

export class AIService {
  private static readonly BASE_URL = '/api/proxy/genai'

  /**
   * Check the health of the AI service
   */
  static async checkHealth(): Promise<HealthCheckResponseDTO> {
    const { authFetch } = useAuthFetch()
    try {
      const response = await authFetch<HealthCheckResponseDTO>(`${this.BASE_URL}/health`)
      return response
    } catch (error) {
      console.error('Failed to check health:', error) // eslint-disable-line no-console
      throw error
    }
  }

  /**
   * Get AI content suggestion
   */
  static async getSuggestion(request: SuggestionRequestDTO): Promise<SuggestionResponseDTO> {
    const { authFetch } = useAuthFetch()
    try {
      const response = await authFetch<SuggestionResponseDTO>(`${this.BASE_URL}/suggestion`, {
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
  static async getServiceInfo(): Promise<any> {
    const { authFetch } = useAuthFetch()
    try {
      const response = await authFetch<any>(`${this.BASE_URL}/`)
      return response
    } catch (error) {
      console.error('Failed to get service info:', error) // eslint-disable-line no-console
      throw error
    }
  }
}
