// unified type definition file

// Category
export interface Category {
  id: number
  name: string
  competitionId: number
}

// ImageSlideMeta
export interface ImageSlideMeta {
  id: number
  name: string
  contentType: string
}

// Team
export interface Team {
  id: number
  name: string
}

// ScoreDTO
export interface Score {
  id: number
  points: number
  time: number
  highlight: boolean
  team: Team
  rank: number
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
      category: Category
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
  slides: SlideItem[] | null
  transitionTime: number
  version: number
  lastUpdate: string // ISO 8601 format datetime string
}

// ScreenContent
export interface ScreenContent {
  id: number
  name: string
  status: string
  slideDeck: SlideDeck | null
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
export type ScreenStatus = 'ONLINE' | 'OFFLINE' | 'ERROR'
export type ContentType = 'BLACK_SCREEN' | string
export interface SyncState {
  deckId: number
  currentSlideIndex: number
  lastUpdate: string
  isMaster: boolean
  screenId?: string
}

export interface SyncResponse {
  deckId: number
  currentSlideIndex: number
  slideCount: number
  transitionTime: number
  lastUpdate: string
  version: number
  isMaster: boolean
  lastUpdateScreenId?: string
  syncActive: boolean
  errorMessage?: string
}
