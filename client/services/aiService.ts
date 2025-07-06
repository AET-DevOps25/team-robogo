import type {
  SuggestionRequestDTO,
  SuggestionResponseDTO,
  HealthCheckResponseDTO
} from '@/interfaces/dto'
import type { ApiResponse } from '@/interfaces/types'

export class AIService {
  private static readonly BASE_URL = '/api/proxy/genai'

  /**
   * 检查AI服务健康状态
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
   * 获取AI内容建议
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
      console.error('获取AI建议失败:', error)
      throw error
    }
  }

  /**
   * 获取AI服务信息
   */
  static async getServiceInfo(): Promise<any> {
    try {
      const response = await fetch(`${this.BASE_URL}/`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return await response.json()
    } catch (error) {
      console.error('获取AI服务信息失败:', error)
      throw error
    }
  }
}
