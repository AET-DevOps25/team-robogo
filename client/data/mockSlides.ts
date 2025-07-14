import type { SlideItem } from '../interfaces/types'

export const mockSlides: SlideItem[] = [
  {
    id: 1,
    index: 0,
    name: 'Slide A',
    type: 'image',
    imageMeta: { id: 1, name: 'Slide A', contentType: 'image/png' }
  },
  {
    id: 2,
    index: 1,
    name: 'Slide B',
    type: 'image',
    imageMeta: { id: 2, name: 'Slide B', contentType: 'image/png' }
  },
  {
    id: 3,
    index: 2,
    name: 'Slide C',
    type: 'score',
    scores: [
      { points: 10, time: 60, highlight: false },
      { points: 20, time: 120, highlight: true }
    ],
    categoryId: 100
  },
  {
    id: 4,
    index: 3,
    name: 'Slide D',
    type: 'image',
    imageMeta: { id: 4, name: 'Slide D', contentType: 'image/png' }
  },
  {
    id: 5,
    index: 4,
    name: 'Slide E',
    type: 'score',
    scores: [{ points: 15, time: 90, highlight: false }],
    categoryId: 101
  }
]
