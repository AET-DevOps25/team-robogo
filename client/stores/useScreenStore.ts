// stores/useScreenStore.ts
import { defineStore } from 'pinia'
import { v4 as uuidv4 } from 'uuid'


export const useScreenStore = defineStore('screen', {
  
  state: () => ({
    screens: [] as {
      id: string,
      name: string,
      status: string,
      groupId: string,
      currentContent: string,
      thumbnailUrl: string,
      urlPath: string,
    }[],
    slideGroups: [] as {
      id: string,
      slideIds: number[],
      speed: number,
      _currentSlideIndex?: number,
      _lastSwitchTime?: number
    }[],
    contentList: [] as { id: number, name: string, url: string }[],
    slideTimerStarted: false,
    _timer: null as ReturnType<typeof setInterval> | null
  }),

  actions: {
    initStore() {
  if (!this.slideGroups.find(g => g.id === 'None')) {
    this.slideGroups.push({
      id: 'None',
      slideIds: [], 
      speed: 5,
      _currentSlideIndex: 0,
      _lastSwitchTime: Date.now(),
    })
  }
},
    startSlideTimer() {
      if (this._timer) return
      this._timer = setInterval(() => {
        const now = Date.now()
        for (const group of this.slideGroups) {
          if (!group.slideIds.length) continue
          if (!group._lastSwitchTime) {
            group._lastSwitchTime = now
            group._currentSlideIndex = 0
            continue
          }
          const speedMs = (group.speed || 5) * 1000
          if (now - group._lastSwitchTime >= speedMs) {
            group._lastSwitchTime = now
            group._currentSlideIndex = ((group._currentSlideIndex ?? -1) + 1) % group.slideIds.length
          }
        }

        for (const screen of this.screens) {
          const group = this.slideGroups.find(g => g.id === screen.groupId)
          if (!group || !group.slideIds.length) {
            screen.currentContent = 'BLACK_SCREEN'
            continue
          }
          const id = group.slideIds[group._currentSlideIndex ?? 0]
          screen.currentContent = id.toString()
        }
      }, 1000)
      this.slideTimerStarted = true
    },

   


    addScreen(name: string, url: string) {
      const id = uuidv4()
      this.screens.push({
        id,
        name,
        status: 'offline',
        groupId: 'None',
        currentContent: 'BLACK_SCREEN',
        thumbnailUrl: url || 'https://via.placeholder.com/300x200?text=Preview',
        urlPath: `/screen/${id}`,
      })
    },

    addGroup(name: string) {
      if (!name || this.slideGroups.find(g => g.id === name)) return
      this.slideGroups.push({
        id: name,
        slideIds: [],
        speed: 5,
        _currentSlideIndex: 0,
        _lastSwitchTime: Date.now(),
      })
    },

    updateScreenGroup(screenId: string, groupId: string) {
      const screen = this.screens.find(s => s.id === screenId)
      if (screen) screen.groupId = groupId
    },

  },
   persist: {
    key: 'screen',                              // localStorage 键名
    paths: ['screens', 'slideGroups'
      // , 'contentList'
    ]
  }
})
