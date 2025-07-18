import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { fetchSlideDecks } from '@/services/slideDeckService'
import type { SlideDeck, DeckStartTime } from '@/interfaces/types'

export const useStartTimeStore = defineStore('time', () => {
  const allDecks = ref<SlideDeck[]>([])
  const allStartTime = ref<DeckStartTime[]>([])

  // check if start time for this deck is existed
  function hasStartTime(deckId: number): boolean {
    return allStartTime.value.some(time => time.id === deckId)
  }

  // add start time for new deck
  function addStartTime(deckId: number) {
    if (!hasStartTime(deckId)) {
      const newStartTime: DeckStartTime = {
        id: deckId,
        startTime: Date.now()
      }
      allStartTime.value.push(newStartTime)
    }
  }

  // update an existing start time
  function updateStartTime(deckId: number) {
    const index = allStartTime.value.findIndex(time => time.id === deckId)
    if (index !== -1) {
      allStartTime.value[index].startTime = Date.now()
    } else {
      addStartTime(deckId) 
    }
  }

  //cleanup store
  function deleteAllStartTime() {
    allStartTime.value = []
  }

  // update satrt time for all decks 
  async function updateAllStartTime() {
    deleteAllStartTime()
    allDecks.value = await fetchSlideDecks()
    allDecks.value.forEach(deck => {
      addStartTime(deck.id)
    })
  }

  // get start time of deck[id], while start time is controlled by deck management, each screen needs to get the start time of the deck it is using
  function getStartTime(deckId: number) {
    const item = allStartTime.value.find(time => time.id === deckId)
    return item ? item.startTime : null
  }

  // store in localStorage
  watch(allStartTime, (newVal) => {
    localStorage.setItem('startTimes', JSON.stringify(newVal))
  }, { deep: true })

  return {
    allStartTime,
    addStartTime,
    updateStartTime,
    deleteAllStartTime,
    updateAllStartTime,
    getStartTime
  }
}, {
  persist: {
    key: 'slide-deck-start-times',
    paths: ['allStartTime'],
    storage: localStorage
  }
})