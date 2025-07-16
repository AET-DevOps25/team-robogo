import { type SlideItem, SlideType } from '../interfaces/types'

export const mockSlides: SlideItem[] = [
  {
    id: 1,
    index: 0,
    name: 'Slide A',
    type: SlideType.IMAGE,
    imageMeta: { id: 1, name: 'Slide A', contentType: 'image/png' }
  },
  {
    id: 2,
    index: 1,
    name: 'Slide B',
    type: SlideType.IMAGE,
    imageMeta: { id: 2, name: 'Slide B', contentType: 'image/png' }
  },
  {
    id: 3,
    index: 2,
    name: 'Slide C',
    type: SlideType.SCORE,
    scores: [
      { points: 10, time: 60, highlight: false },
      { points: 20, time: 120, highlight: true }
    ],
    category: { id: 100, name: 'Category 1', competitionId: 1, categoryScoring: 'FLL_ROBOT_GAME' }
  },
  {
    id: 4,
    index: 3,
    name: 'Slide D',
    type: SlideType.IMAGE,
    imageMeta: { id: 4, name: 'Slide D', contentType: 'image/png' }
  },
  {
    id: 5,
    index: 4,
    name: 'Slide E',
    type: SlideType.SCORE,
    scores: [{ points: 15, time: 90, highlight: false }],
    category: { id: 101, name: 'Category 2', competitionId: 2, categoryScoring: 'FLL_ROBOT_GAME' }
  }
]
