import type { ScreenContent } from '../interfaces/types'
import { mockSlideDecks } from './mockSlideDecks'

export const mockScreenContentList: ScreenContent[] = [
  {
    id: 1,
    name: 'Screen 1',
    status: 'ONLINE',
    slideDeck: mockSlideDecks[0]
  },
  {
    id: 2,
    name: 'Screen 2',
    status: 'OFFLINE',
    slideDeck: mockSlideDecks[1]
  }
]
