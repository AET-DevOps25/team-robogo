// stores/useScreenStore.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { v4 as uuidv4 } from 'uuid'

export const useScreenStore = defineStore(
  'screen',
  () => {
    // State
    const screens = ref<
      {
        id: string
        name: string
        status: string
        groupId: string
        currentContent: string
        thumbnailUrl: string
        urlPath: string
      }[]
    >([])

    const slideGroups = ref<
      {
        id: string
        slideIds: number[]
        speed: number
        _currentSlideIndex?: number
        _lastSwitchTime?: number
      }[]
    >([])

    const contentList = ref<{ id: number; name: string; url: string }[]>([])
    const slideTimerStarted = ref(false)
    const _timer = ref<ReturnType<typeof setInterval> | null>(null)

    // Actions
    const initStore = () => {
      if (!slideGroups.value.find((g) => g.id === 'None')) {
        slideGroups.value.push({
          id: 'None',
          slideIds: [],
          speed: 5,
          _currentSlideIndex: 0,
          _lastSwitchTime: Date.now()
        })
      }
    }

    const startSlideTimer = () => {
      if (_timer.value) return
      _timer.value = setInterval(() => {
        const now = Date.now()
        for (const group of slideGroups.value) {
          if (!group.slideIds.length) continue
          if (!group._lastSwitchTime) {
            group._lastSwitchTime = now
            group._currentSlideIndex = 0
            continue
          }
          const speedMs = (group.speed || 5) * 1000
          if (now - group._lastSwitchTime >= speedMs) {
            group._lastSwitchTime = now
            group._currentSlideIndex =
              ((group._currentSlideIndex ?? -1) + 1) % group.slideIds.length
          }
        }

        for (const screen of screens.value) {
          const group = slideGroups.value.find((g) => g.id === screen.groupId)
          if (!group || !group.slideIds.length) {
            screen.currentContent = 'BLACK_SCREEN'
            continue
          }
          const id = group.slideIds[group._currentSlideIndex ?? 0]
          screen.currentContent = id.toString()
        }
      }, 1000)
      slideTimerStarted.value = true
    }

    const addScreen = (name: string, url: string) => {
      const id = uuidv4()
      screens.value.push({
        id,
        name,
        status: 'offline',
        groupId: 'None',
        currentContent: 'BLACK_SCREEN',
        thumbnailUrl: url || 'https://via.placeholder.com/300x200?text=Preview',
        urlPath: `/screen/${id}`
      })
    }

    const addGroup = (name: string) => {
      if (!name || slideGroups.value.find((g) => g.id === name)) return
      slideGroups.value.push({
        id: name,
        slideIds: [],
        speed: 5,
        _currentSlideIndex: 0,
        _lastSwitchTime: Date.now()
      })
    }

    const updateScreenGroup = (screenId: string, groupId: string) => {
      const screen = screens.value.find((s) => s.id === screenId)
      if (screen) screen.groupId = groupId
    }

    return {
      // State
      screens,
      slideGroups,
      contentList,
      slideTimerStarted,

      // Actions
      initStore,
      startSlideTimer,
      addScreen,
      addGroup,
      updateScreenGroup
    }
  },
  {
    persist: true
  }
)
