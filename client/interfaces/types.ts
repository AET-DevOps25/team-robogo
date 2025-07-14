// unified type definition file

// ImageSlideMeta
export interface ImageSlideMeta {
  id: number
  name: string
  contentType: string
}

// Score
export interface Score {
  points: number
  time: number
  highlight: boolean
}

// SlideItem → 多态联合类型
export type SlideItem =
  | {
      id: number
      index: number
      name: string
      type: 'image'
      imageMeta: ImageSlideMeta
    }
  | {
      id: number
      index: number
      name: string
      type: 'score'
      scores: Score[]
      categoryId: number
    }
  | {
      id: number
      index: number
      name: string
      type: string // 其它类型
    }

// SlideDeck
export interface SlideDeck {
  id: number
  name: string
  competitionId: number
  slides: SlideItem[]
  transitionTime: number
  version: number
}

// ScreenContent
export interface ScreenContent {
  id: number
  name: string
  status: string
  slideDeck: SlideDeck
  currentContent: string
  thumbnailUrl?: string
  urlPath?: string
}

// AI服务相关接口
export interface SuggestionRequest {
  text: string
  service?: 'openwebui' | 'openai'
}
export interface SuggestionResponse {
  suggestion: string
}
export interface HealthCheckResponse {
  status: string
  service: string
}

// chat related types
export interface ChatMessage {
  role: string
  text: string
}

// common types
export type ScreenStatus = 'online' | 'offline' | 'error'
export type ContentType = 'BLACK_SCREEN' | string
