// 统一类型定义文件

export interface Screen {
  id: string
  name: string
  status: string
  groupId: string
  currentContent: string
  thumbnailUrl: string
  urlPath: string
}

export interface Slide {
  id: number
  name: string
  url: string
}

export interface SlideGroup {
  id: string
  slideIds: number[]
  speed: number
  _currentSlideIndex?: number
  _lastSwitchTime?: number
}

export interface SlideItem {
  id: number
  name: string
  url: string
}

// 聊天相关类型
export interface ChatMessage {
  role: string
  text: string
}

// API 响应类型
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
}
