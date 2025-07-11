// unified type definition file

export interface Screen {
  id: string
  name: string
  status: string
  groupId: string
  currentContent: string
  thumbnailUrl: string
  urlPath: string
}

export interface SlideItem {
  id: number
  name: string
  url: string
}

export interface SlideGroup {
  id: string
  slideIds: number[]
  speed: number
  version: number
  _currentSlideIndex?: number
  _lastSwitchTime?: number
}

// chat related types
export interface ChatMessage {
  role: string
  text: string
}

// API response type
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
}

// common types
export type ScreenStatus = 'online' | 'offline' | 'error'
export type ContentType = 'BLACK_SCREEN' | string

// create related DTO types
export interface CreateScreenDTO {
  name: string
  url?: string
}

export interface CreateSlideGroupDTO {
  name: string
  slideIds?: number[]
  speed?: number
}

export interface SlideImageMetaDTO {
  id: number
  name: string
  contentType: string
}
