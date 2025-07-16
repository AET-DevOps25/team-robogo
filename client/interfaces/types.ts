// unified type definition file

// Category
export interface Category {
  id: number
  name: string
  competitionId: number
  categoryScoring: string
}

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
  teamId: number
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
  id: number    //for new deck: use -1 to post and get correct id from backend
  name: string
  competitionId: number
  slides: SlideItem[] | null
  transitionTime: number
  version: number
}

// only local 
export interface LocalSlideDeck extends SlideDeck {
  isLocalOnly?: boolean // only local
  lastResetAt: number  // -1 means stopped/ not initialized
}


// ScreenContent
export interface ScreenContent {
  id: number
  name: string
  status: string
  slideDeck: SlideDeck | null //This is allowd by backend
  currentContent: number  //slide.id
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
