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
      const response = await fetch(`${this.BASE_URL}/health`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return await response.json()
    } catch (error) {
      console.error('AI服务健康检查失败:', error)
      throw error
    }
  }

  /**
   * Get AI content suggestion
   */
  static async getSuggestion(request: SuggestionRequestDTO): Promise<SuggestionResponseDTO> {
    try {
      const response = await fetch(`${this.BASE_URL}/suggestion`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
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
      const response = await fetch(`${this.BASE_URL}/`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return await response.json()
    } catch (error) {
      console.error('Failed to get service info:', error)
      throw error
    }
  }
}
