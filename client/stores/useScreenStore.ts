// stores/useScreenStore.ts
import { defineStore } from 'pinia'
import 'pinia-plugin-persistedstate'
import { ref } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import type { Screen, SlideGroup } from '@/interfaces/types'

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

    // replace a group and automatically replay from the first slide
    function replaceGroup(g: SlideGroup) {
      const idx = slideGroups.value.findIndex(x => x.id === g.id)
      if (idx !== -1)
        slideGroups.value[idx] = {
          ...g,
          lastResetAt: Date.now()
        }
    }
    // calculate current slide index of a group
    function calcIndex(g: SlideGroup) {
      if (!g.slideIds.length) return 0
      const elapsed = Date.now() - g.lastResetAt
      return Math.floor(elapsed / (g.speed * 1000)) % g.slideIds.length
    }
    // set _timer and allocate current slide for each group
    const startSlideTimer = () => {
      if (_timer) return // do not setup a _timer twice
      _timer = setInterval(() => {
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

    /** play from the first slide of the group */
    function resetGroup(g: SlideGroup) {
      g.lastResetAt = Date.now()
    }
    // create a new screen, default: black screen, no group allocation
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
    // create a new group, play from first slide immediately
    const addGroup = (name: string) => {
      if (!name || slideGroups.value.find(g => g.id === name)) return
      slideGroups.value.push({
        id: name,
        slideIds: [],
        speed: 5,
        version: 1, // init version 1
        lastResetAt: Date.now()
      })
    }
    // update the group allocation of a screen
    const updateScreenGroup = (screenId: string, groupId: string) => {
      const screen = screens.value.find(s => s.id === screenId)
      if (screen) screen.groupId = groupId
    }

    /** play a group from the first slide */
    const playGroup = (groupId: string) => {
      const g = slideGroups.value.find(g => g.id === groupId)
      if (!g || !g.slideIds.length) return

      g.lastResetAt = Date.now()

      // change current slide to the first slide, for all screens playing this group
      screens.value
        .filter(s => s.groupId === groupId)
        .forEach(s => (s.currentContent = g.slideIds[0].toString()))
    }

    /** play from the first slide, all groups */
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
