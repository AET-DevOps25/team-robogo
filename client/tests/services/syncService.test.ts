import { describe, it, expect, vi, beforeEach } from 'vitest'
import { startSync, stopSync, getCurrentSlideIndex, setSlideIndex, cleanup } from '@/services/syncService'
import type { SyncState } from '@/interfaces/types'

describe('SyncService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // 清理之前的同步状态
    cleanup()
  })

  describe('startSync', () => {
    it('should start sync for a deck', () => {
      startSync(1, 5000, 'screen-1')

      expect(getCurrentSlideIndex(1)).toBe(0)
    })

    it('should not start sync if already syncing', () => {
      startSync(1, 5000, 'screen-1')
      startSync(1, 5000, 'screen-1') // Second call should be ignored

      expect(getCurrentSlideIndex(1)).toBe(0)
    })
  })

  describe('stopSync', () => {
    it('should stop sync for a deck', () => {
      startSync(1, 5000, 'screen-1')
      stopSync(1)

      expect(getCurrentSlideIndex(1)).toBe(0)
    })

    it('should handle stopping non-existent sync', () => {
      expect(() => stopSync(999)).not.toThrow()
    })
  })

  describe('setSlideIndex', () => {
    it('should set slide index', () => {
      startSync(1, 5000, 'screen-1')
      setSlideIndex(1, 2)

      expect(getCurrentSlideIndex(1)).toBe(2)
    })

    it('should not set slide index for non-syncing deck', () => {
      setSlideIndex(999, 2)

      expect(getCurrentSlideIndex(999)).toBe(0)
    })
  })

  describe('getCurrentSlideIndex', () => {
    it('should return current slide index', () => {
      startSync(1, 5000, 'screen-1')
      setSlideIndex(1, 1)

      expect(getCurrentSlideIndex(1)).toBe(1)
    })

    it('should return 0 for non-syncing deck', () => {
      expect(getCurrentSlideIndex(999)).toBe(0)
    })
  })

  describe('cleanup', () => {
    it('should cleanup all sync intervals', () => {
      startSync(1, 5000, 'screen-1')
      startSync(2, 5000, 'screen-2')
      cleanup()

      expect(getCurrentSlideIndex(1)).toBe(0)
      expect(getCurrentSlideIndex(2)).toBe(0)
    })
  })
}) 