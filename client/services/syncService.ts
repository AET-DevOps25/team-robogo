import { useAuthFetch } from '@/composables/useAuthFetch'
import type { SyncState, SyncResponse } from '@/interfaces/types'

// 状态管理
const syncStates = new Map<number, SyncState>()
const syncIntervals = new Map<number, ReturnType<typeof setInterval>>()
const SYNC_INTERVAL = 500 // 500ms同步一次
let authFetchInstance: ReturnType<typeof useAuthFetch> | null = null

// 初始化authFetch
function initAuthFetch() {
  if (typeof window !== 'undefined' && !authFetchInstance) {
    authFetchInstance = useAuthFetch()
  }
}

// 开始同步指定deck
export function startSync(deckId: number, _transitionTime: number, screenId?: string): void {
  if (syncIntervals.has(deckId)) {
    return // 已经在同步中
  }

  initAuthFetch()

  const syncState: SyncState = {
    deckId,
    currentSlideIndex: 0,
    lastUpdate: new Date().toISOString(),
    isMaster: false,
    screenId
  }

  syncStates.set(deckId, syncState)

  // 启动同步定时器
  const interval = setInterval(() => {
    syncSlide(deckId)
  }, SYNC_INTERVAL)

  syncIntervals.set(deckId, interval)
}

// 停止同步指定deck
export function stopSync(deckId: number): void {
  const interval = syncIntervals.get(deckId)
  if (interval) {
    clearInterval(interval)
    syncIntervals.delete(deckId)
  }
  syncStates.delete(deckId)
}

// 同步幻灯片
async function syncSlide(deckId: number): Promise<void> {
  try {
    if (!authFetchInstance) {
      console.error('AuthFetch not initialized')
      return
    }

    const response = await authFetchInstance.authFetch<SyncResponse>(
      `/api/proxy/slidedecks/${deckId}/sync`
    )

    if (!response.syncActive) {
      console.error('Sync not active:', response.errorMessage)
      return
    }

    const syncState = syncStates.get(deckId)
    if (syncState && response.lastUpdate !== syncState.lastUpdate) {
      // 检测到更新，切换到下一张幻灯片
      syncState.currentSlideIndex = (syncState.currentSlideIndex + 1) % response.slideCount
      syncState.lastUpdate = response.lastUpdate
      syncState.isMaster = response.isMaster

      // 触发幻灯片切换事件
      emitSlideChange(deckId, syncState.currentSlideIndex)
    }
  } catch (error) {
    console.error('Failed to sync slide:', error)
  }
}

// 触发幻灯片切换事件
function emitSlideChange(deckId: number, slideIndex: number): void {
  if (typeof window !== 'undefined') {
    const event = new CustomEvent('slideChange', {
      detail: { deckId, slideIndex }
    })
    window.dispatchEvent(event)
  }
}

// 获取当前幻灯片索引
export function getCurrentSlideIndex(deckId: number): number {
  const syncState = syncStates.get(deckId)
  return syncState?.currentSlideIndex || 0
}

// 手动切换到指定幻灯片
export function setSlideIndex(deckId: number, slideIndex: number): void {
  const syncState = syncStates.get(deckId)
  if (syncState) {
    syncState.currentSlideIndex = slideIndex
    syncState.lastUpdate = new Date().toISOString()

    // 通知后端更新lastUpdate
    updateLastUpdate(deckId)

    // 触发幻灯片切换事件
    emitSlideChange(deckId, slideIndex)
  }
}

// 更新后端的lastUpdate
async function updateLastUpdate(deckId: number): Promise<void> {
  try {
    if (!authFetchInstance) {
      console.error('AuthFetch not initialized')
      return
    }

    const syncState = syncStates.get(deckId)
    const request = {
      currentSlideIndex: syncState?.currentSlideIndex || 0,
      screenId: syncState?.screenId,
      setAsMaster: false,
      forceUpdate: false
    }

    await authFetchInstance.authFetch(`/api/proxy/slidedecks/${deckId}/sync`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    })
  } catch (error) {
    console.error('Failed to update lastUpdate:', error)
  }
}

// 清理所有同步
export function cleanup(): void {
  for (const [deckId] of syncIntervals) {
    stopSync(deckId)
  }
}

// 在页面卸载时清理 - 只在客户端执行
if (typeof window !== 'undefined') {
  window.addEventListener('beforeunload', () => {
    cleanup()
  })
}
