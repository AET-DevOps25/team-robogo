import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'

export class AIService {
  private static readonly BASE_URL = '/api/proxy/genai'

  /**
   * Check the health of the AI service
   */
  static async checkHealth(): Promise<HealthCheckResponseDTO> {
    try {
      const response = await $fetch<HealthCheckResponseDTO>(`${this.BASE_URL}/health`)
      return response
    } catch (error) {
      console.error('Failed to check health:', error)
      throw error
    }
  }

  /**
   * Get AI content suggestion
   */
  static async getSuggestion(request: SuggestionRequestDTO): Promise<SuggestionResponseDTO> {
    try {
      const response = await $fetch<SuggestionResponseDTO>(`${this.BASE_URL}/suggestion`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: request
      })

      return response
    } catch (error) {
      console.error('Failed to get suggestion:', error)
      throw error
    }
  }

  /**
   * Get AI service info
   */
  static async getServiceInfo(): Promise<any> {
    try {
      const response = await $fetch<any>(`${this.BASE_URL}/`)
      return response
    } catch (error) {
      console.error('Failed to get service info:', error)
      throw error
    }
  }
}
