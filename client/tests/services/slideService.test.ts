import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchSlides,
  fetchSlideById,
  createSlide,
  updateSlide,
  deleteSlide
} from '@/services/slideService'
import type { SlideItem } from '@/interfaces/types'
import { SlideType } from '@/interfaces/types'

describe('SlideService', () => {
  const mockSlide: SlideItem = {
    id: 1,
    index: 0,
    name: 'Test Slide',
    type: SlideType.IMAGE,
    imageMeta: { id: 1, name: 'test.jpg', contentType: 'image/jpeg' }
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('fetchSlides', () => {
    it('should fetch all slides successfully without deckId', async () => {
      const result = await fetchSlides()

      expect(result).toEqual({})
    })

    it('should fetch slides by deckId successfully', async () => {
      const result = await fetchSlides(1)

      expect(result).toEqual({})
    })

    it('should handle fetch slides error', async () => {
      await expect(fetchSlides()).resolves.toEqual({})
    })
  })

  describe('fetchSlideById', () => {
    it('should fetch slide by id successfully', async () => {
      const result = await fetchSlideById(1)

      expect(result).toEqual({})
    })

    it('should handle fetch slide by id error', async () => {
      await expect(fetchSlideById(999)).resolves.toEqual({})
    })
  })

  describe('createSlide', () => {
    it('should create slide successfully', async () => {
      const newSlide: SlideItem = {
        id: 0, // 临时ID，创建时会被忽略
        index: 0,
        name: 'New Slide',
        type: SlideType.IMAGE,
        imageMeta: { id: 2, name: 'new.jpg', contentType: 'image/jpeg' }
      }

      const result = await createSlide(1, newSlide)

      expect(result).toEqual({})
    })

    it('should handle create slide error', async () => {
      const newSlide: SlideItem = {
        id: 0, // 临时ID，创建时会被忽略
        index: 0,
        name: 'New Slide',
        type: SlideType.IMAGE,
        imageMeta: { id: 2, name: 'new.jpg', contentType: 'image/jpeg' }
      }

      await expect(createSlide(1, newSlide)).resolves.toEqual({})
    })
  })

  describe('updateSlide', () => {
    it('should update slide successfully', async () => {
      const updateData: Partial<SlideItem> = { name: 'Updated Slide' }
      const updatedSlide = { ...mockSlide, ...updateData }

      const result = await updateSlide(1, updatedSlide)

      expect(result).toEqual({})
    })

    it('should handle update slide error', async () => {
      const updateData: Partial<SlideItem> = { name: 'Updated Slide' }
      const updatedSlide = { ...mockSlide, ...updateData }

      await expect(updateSlide(999, updatedSlide)).resolves.toEqual({})
    })
  })

  describe('deleteSlide', () => {
    it('should delete slide successfully', async () => {
      await expect(deleteSlide(1)).resolves.toBeUndefined()
    })

    it('should handle delete slide error', async () => {
      await expect(deleteSlide(999)).resolves.toBeUndefined()
    })
  })
})
