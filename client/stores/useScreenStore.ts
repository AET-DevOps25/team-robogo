// stores/useScreenStore.ts
import { defineStore } from 'pinia'
import 'pinia-plugin-persistedstate'
import { ref } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import type { ScreenContent, SlideDeck, LocalSlideDeck } from '@/interfaces/types'

export const useScreenStore = defineStore(
  'screen',
  () => {
    // State
    const screens = ref<ScreenContent[]>([])
    const slideDecks = ref<LocalSlideDeck[]>([])
    const slideTimerStarted = ref(false)
    let _timer: ReturnType<typeof setInterval> | null = null

    // Actions
    /**
     * Shows a predefined uneditable empty 'Deck'
     */
    const initStore = () => {
      if (!slideDecks.value.find(g => g.name === 'None')) {
        slideDecks.value.push({
          id: -1,
          name: 'None',
          slides: null,
          competitionId: -1, //this is not belong to any competition
          transitionTime: -1,
          version: 0,
          isLocalOnly: true,
          lastResetAt: -1 // ms
        } as SlideDeck & { isLocalOnly: boolean; lastResetAt: number })
      }
    }

    // replace a group and automatically replay from the first slide
    function replaceGroup(g: LocalSlideDeck) {
      const idx = slideDecks.value.findIndex(x => x.id === g.id)
      if (idx !== -1)
        slideDecks.value[idx] = {
          ...g,
          lastResetAt: Date.now()
        }
    }
    //TODO: fix the logic
    // calculate current slide index of a group
    function calcIndex(ld: LocalSlideDeck) {
      if (!Array.isArray(ld.slides) || ld.slides.length === 0 || ld.lastResetAt) return 0
      const elapsed = Date.now() - ld.lastResetAt
      return Math.floor(elapsed / (ld.transitionTime * 1000)) % ld.slides.length
    }
    // set _timer and allocate current slide for each group
    const startSlideTimer = () => {
      if (_timer) return // do not setup a _timer twice
      _timer = setInterval(() => {
        screens.value.forEach(screen => {
          if (
            !screen.slideDeck ||
            !screen.slideDeck.slides ||
            screen.slideDeck.slides.length === 0
          ) {
            screen.currentContent = -1
            return
          }

          const d = slideDecks.value.find(x => x.id === screen.slideDeck?.id)
          if (!d || !d.slides || d.slides.length === 0) {
            screen.currentContent = -1
            return
          }

          const idx = calcIndex(d)
          screen.currentContent = d.slides[idx].id
        })
      }, 1000)
    }

    /** play from the first slide of the group */
    function resetDeck(g: LocalSlideDeck) {
      g.lastResetAt = Date.now()
    }
    // create a new screen, default: black screen, no group allocation
    // TODO: change to api
    const addScreen = (screenName: string) => {
      screens.value.push({
        id: -1, //
        name: screenName,
        status: 'offline',
        slideDeck: null,
        currentContent: -1 //'BLACK_SCREEN'
        // urlPath: `/screen/${id}`
      })
    }
    // create a new deck in pinia, play from first slide immediately
    const addDeck = (deck: SlideDeck) => {
      slideDecks.value.push({
        ...deck,
        lastResetAt: Date.now()
      })
    }
    // update the deck assignment of a screen
    const updateScreenDeck = (screenId: number, DeckId: number) => {
      const screen = screens.value.find(s => s.id === screenId)
      const updatedDeck = slideDecks.value.find(d => d.id === DeckId)
      if (screen && updatedDeck) screen.slideDeck = updatedDeck //update currentContent() of this screen
    }

    /** play a deck from the first slide */
    const playDeck = (DeckId: number) => {
      const d = slideDecks.value.find(g => g.id === DeckId)
      if (!d || !Array.isArray(d.slides) || d.slides.length === 0) return //if deck is not existed or empty, do not replay (meaningless)

      d.lastResetAt = Date.now()

      // change current slide to the first slide, for all screens playing this group
      screens.value
        .filter(s => s.slideDeck?.id === DeckId)
        .forEach(s => (s.currentContent = d.slides![0].id))
    }

    /** play from the first slide, all groups */
    const playAllDecks = () => {
      slideDecks.value.forEach(d => playDeck(d.id))
    }

    return {
      // State
      screens,
      slideDecks,
      slideTimerStarted,
      _timer,

      // Actions
      initStore,
      replaceGroup,
      startSlideTimer,
      addScreen,
      addDeck,
      resetDeck,
      updateScreenDeck,
      playDeck,
      playAllDecks
    }
  },
  {
    persist: {
      // do not persist timer
      paths: ['screens', 'slideDecks']
    }
  }
)
