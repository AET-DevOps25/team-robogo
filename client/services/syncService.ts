import { useAuthFetch } from '@/composables/useAuthFetch'
import type { SyncState, SyncResponse } from '@/interfaces/types'

class SlideSyncService {
  private syncStates = new Map<number, SyncState>()
  private syncIntervals = new Map<number, ReturnType<typeof setInterval>>()
  private readonly SYNC_INTERVAL = 1000 // 1秒同步一次
  private authFetchInstance: ReturnType<typeof useAuthFetch> | null = null

  constructor() {
    // 在客户端初始化时获取authFetch
    if (typeof window !== 'undefined') {
      this.authFetchInstance = useAuthFetch()
    }
  }

  // 开始同步指定deck
  startSync(deckId: number, _transitionTime: number, screenId?: string): void {
    if (this.syncIntervals.has(deckId)) {
      return // 已经在同步中
    }

    const syncState: SyncState = {
      deckId,
      currentSlideIndex: 0,
      lastUpdate: new Date().toISOString(),
      isMaster: false,
      screenId
    }

    this.syncStates.set(deckId, syncState)

    // 启动同步定时器
    const interval = setInterval(() => {
      this.syncSlide(deckId)
    }, this.SYNC_INTERVAL)

    this.syncIntervals.set(deckId, interval)
  }

  // 停止同步指定deck
  stopSync(deckId: number): void {
    const interval = this.syncIntervals.get(deckId)
    if (interval) {
      clearInterval(interval)
      this.syncIntervals.delete(deckId)
    }
    this.syncStates.delete(deckId)
  }

  // 同步幻灯片
  private async syncSlide(deckId: number): Promise<void> {
    try {
      if (!this.authFetchInstance) {
        console.error('AuthFetch not initialized')
        return
      }

      const response = await this.authFetchInstance.authFetch<SyncResponse>(
        `/api/proxy/slidedecks/${deckId}/sync`
      )

      if (!response.syncActive) {
        console.error('Sync not active:', response.errorMessage)
        return
      }

      const syncState = this.syncStates.get(deckId)
      if (syncState && response.lastUpdate !== syncState.lastUpdate) {
        // 检测到更新，切换到下一张幻灯片
        syncState.currentSlideIndex = (syncState.currentSlideIndex + 1) % response.slideCount
        syncState.lastUpdate = response.lastUpdate
        syncState.isMaster = response.isMaster

        // 触发幻灯片切换事件
        this.emitSlideChange(deckId, syncState.currentSlideIndex)
      }
    } catch (error) {
      console.error('Failed to sync slide:', error)
    }
  }

  // 触发幻灯片切换事件
  private emitSlideChange(deckId: number, slideIndex: number): void {
    if (typeof window !== 'undefined') {
      const event = new CustomEvent('slideChange', {
        detail: { deckId, slideIndex }
      })
      window.dispatchEvent(event)
    }
  }

  // 获取当前幻灯片索引
  getCurrentSlideIndex(deckId: number): number {
    const syncState = this.syncStates.get(deckId)
    return syncState?.currentSlideIndex || 0
  }

  // 手动切换到指定幻灯片
  setSlideIndex(deckId: number, slideIndex: number): void {
    const syncState = this.syncStates.get(deckId)
    if (syncState) {
      syncState.currentSlideIndex = slideIndex
      syncState.lastUpdate = new Date().toISOString()

      // 通知后端更新lastUpdate
      this.updateLastUpdate(deckId)

      // 触发幻灯片切换事件
      this.emitSlideChange(deckId, slideIndex)
    }
  }

  // 更新后端的lastUpdate
  private async updateLastUpdate(deckId: number): Promise<void> {
    try {
      if (!this.authFetchInstance) {
        console.error('AuthFetch not initialized')
        return
      }

      const syncState = this.syncStates.get(deckId)
      const request = {
        currentSlideIndex: syncState?.currentSlideIndex || 0,
        screenId: syncState?.screenId,
        setAsMaster: false,
        forceUpdate: false
      }

      await this.authFetchInstance.authFetch(`/api/proxy/slidedecks/${deckId}/sync`, {
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
  cleanup(): void {
    for (const [deckId] of this.syncIntervals) {
      this.stopSync(deckId)
    }
  }
}

// 创建单例实例
export const slideSyncService = new SlideSyncService()

// 在页面卸载时清理 - 只在客户端执行
if (typeof window !== 'undefined') {
  window.addEventListener('beforeunload', () => {
    slideSyncService.cleanup()
  })
}
