import { mockSlides } from './mockSlides'
import type { SlideDeck } from '@/interfaces/types'

export const mockSlideDecks: SlideDeck[] = [
  {
    id: 1,
    name: 'Test Deck',
    competitionId: 1,
    slides: mockSlides.slice(0, 3), // 取前3个slide
    transitionTime: 5,
    version: 0
  },
  {
    id: 2,
    name: 'Another Deck',
    competitionId: 2,
    slides: [mockSlides[3], mockSlides[4]], // 取第4、5个slide
    transitionTime: 7,
    version: 0
  }
]
