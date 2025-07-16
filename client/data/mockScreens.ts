import { mockSlideDecks } from './mockSlideDecks'
import type {  ScreenContent } from '@/interfaces/types'

export const mockScreenContentList: ScreenContent[] = [
  {
    id: 1,
    name: 'Screen 1',
    status: 'online',
    slideDeck: mockSlideDecks[0],
    currentContent: '',
    thumbnailUrl: '',
    urlPath: ''
  },
  {
    id: 2,
    name: 'Screen 2',
    status: 'offline',
    slideDeck: mockSlideDecks[1],
    currentContent: '',
    thumbnailUrl: '',
    urlPath: ''
  }
]
