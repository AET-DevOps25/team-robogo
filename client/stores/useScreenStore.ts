// stores/useScreenStore.ts
import { defineStore } from 'pinia'
import 'pinia-plugin-persistedstate'
import { ref } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import type { Screen, SlideGroup, SlideItem } from '@/interfaces/types'

export const useScreenStore = defineStore(
  'screen',
  () => {
    // State
    const screens = ref<Screen[]>([])
    const slideGroups = ref<SlideGroup[]>([])
    const slideTimerStarted = ref(false)
    let _timer: ReturnType<typeof setInterval> | null = null

    // Actions
    const initStore = () => {
      if (!slideGroups.value.find(g => g.id === 'None')) {
        slideGroups.value.push({
          id: 'None',
          slideIds: [],
          speed: 5,
          version: 0,
          lastResetAt: Date.now() // ms
        })
      }
    }

    function replaceGroup(g: SlideGroup) {
      const idx = slideGroups.value.findIndex(x => x.id === g.id)
      if (idx !== -1)
        slideGroups.value[idx] = {
          ...g,
          lastResetAt: Date.now()
        }
    }

    function calcIndex(g: SlideGroup) {
      if (!g.slideIds.length) return 0
      const elapsed = Date.now() - g.lastResetAt
      return Math.floor(elapsed / (g.speed * 1000)) % g.slideIds.length
    }

    const startSlideTimer = () => {
      if (_timer) return // ← 已有计时器就别再建
      _timer = setInterval(() => {
        console.log('tick')
        screens.value.forEach(screen => {
          const g = slideGroups.value.find(x => x.id === screen.groupId)
          if (!g || !g.slideIds.length) {
            screen.currentContent = 'BLACK_SCREEN'
          } else {
            const idx = calcIndex(g)
            screen.currentContent = g.slideIds[idx].toString()
          }
        })
      }, 1000)
    }

    /** ▶ Play 按钮或保存成功时 */
    function resetGroup(g: SlideGroup) {
      g.lastResetAt = Date.now()
      // 如需全端同步 → PUT /groups/{id}
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
      if (!name || slideGroups.value.find(g => g.id === name)) return
      slideGroups.value.push({
        id: name,
        slideIds: [],
        speed: 5,
        version: 1, //init version 1
        lastResetAt: Date.now()
      })
    }

    const updateScreenGroup = (screenId: string, groupId: string) => {
      const screen = screens.value.find(s => s.id === screenId)
      if (screen) screen.groupId = groupId
    }

    /** 播放指定分组（从第一张开始） */
    const playGroup = (groupId: string) => {
      const g = slideGroups.value.find(g => g.id === groupId)
      if (!g || !g.slideIds.length) return

      g.lastResetAt = Date.now()

      // 把所有绑定这个分组的屏幕立即切到首张
      screens.value
        .filter(s => s.groupId === groupId)
        .forEach(s => (s.currentContent = g.slideIds[0].toString()))
    }

    /** 播放全部分组 */
    const playAllGroups = () => {
      slideGroups.value.forEach(g => playGroup(g.id))
    }

    return {
      // State
      screens,
      slideGroups,
      slideTimerStarted,
      _timer,

      // Actions
      initStore,
      replaceGroup,
      startSlideTimer,
      addScreen,
      addGroup,
      resetGroup,
      updateScreenGroup,
      playGroup,
      playAllGroups
    }
  },
  {
    persist: {
      // do not persist timer
      paths: ['screens', 'slideGroups']
    }
  }
)
