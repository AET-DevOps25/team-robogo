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

// Slide类型枚举，和后端保持一致
export enum SlideType {
  IMAGE = 'IMAGE',
  SCORE = 'SCORE'
}

// SlideItem → 多态联合类型
export type SlideItem =
  | {
      id: number
      index: number
      name: string
      type: SlideType.IMAGE
      imageMeta: ImageSlideMeta
    }
  | {
      id: number
      index: number
      name: string
      type: SlideType.SCORE
      scores: Score[]
      categoryId: number
    }
  | {
      id: number
      index: number
      name: string
      type: SlideType // 其它类型
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

// LoginRequest
export interface LoginRequest {
  username: string
  password: string
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
