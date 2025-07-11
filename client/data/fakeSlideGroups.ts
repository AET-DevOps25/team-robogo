export interface SlideGroup {
  id: string
  slideIds: number[]
  speed: number
  version: number               // ★ 单调递增
  _currentSlideIndex?: number   // 运行时字段，不持久化
  _lastSwitchTime?: number
}

export const fakeSlideGroups: SlideGroup[] = [
  { id: 'Group-A', slideIds: [1, 2, 3], speed: 5, version: 0 },
  { id: 'Group-B', slideIds: [4, 5],    speed: 7, version: 0 }
]
