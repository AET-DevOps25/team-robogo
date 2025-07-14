import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchSlideDecks,
  fetchSlideDeckById,
  createSlideDeck,
  updateSlideDeck,
  addSlideToDeck,
  reorderSlides,
  removeSlideFromDeck,
  deleteSlideDeck,
  fetchSlideById
} from '@/services/slideDeckService'
import type { SlideItem } from '@/interfaces/types'
import { mockSlideDecks } from '@/data/mockSlideDecks'

const mockSlide: SlideItem = {
  id: 101,
  index: 0,
  name: 'Test Slide',
  type: 'image',
  imageMeta: { id: 101, name: 'img1', contentType: 'image/jpeg' }
}

describe('slideDeckService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should fetch all slide decks', async () => {
    const decks = await fetchSlideDecks()
    expect(Array.isArray(decks)).toBe(true)
  })

  it('should fetch a slide deck by id', async () => {
    const deck = await fetchSlideDeckById(1)
    expect(deck).toHaveProperty('id')
    expect(deck).toHaveProperty('slides')
  })

  it('should create a slide deck', async () => {
    const created = await createSlideDeck(mockSlideDecks[0])
    expect(created).toHaveProperty('id')
    expect(created.name).toBe('Test Deck')
  })

  it('should update a slide deck', async () => {
    const updated = await updateSlideDeck(1, { ...mockSlideDecks[0], name: 'Updated Deck' })
    expect(updated).toHaveProperty('id')
    expect(updated.name).toBe('Updated Deck')
  })

  it('should add a slide to deck', async () => {
    // 向 slideDeck 添加 slide
    const result = await addSlideToDeck(1, mockSlide)
    expect(result).toHaveProperty('slides')
  })

  it('should reorder slides in deck', async () => {
    // 重新排序 slideDeck 内 slides
    const result = await reorderSlides(1, [1, 2, 3])
    expect(result).toHaveProperty('slides')
  })

  it('should remove a slide from deck', async () => {
    // 从 slideDeck 移除 slide
    const result = await removeSlideFromDeck(1, 1)
    expect(result).toHaveProperty('slides')
  })

  it('should delete a slide deck', async () => {
    // 删除 slideDeck
    await expect(deleteSlideDeck(1)).resolves.toBeUndefined()
  })

  it('should fetch a single slide by id (polymorphic)', async () => {
    // 获取 slideDeck 下的单个 slide（多态）
    const slide = await fetchSlideById(1, 1)
    expect(slide).toHaveProperty('id')
    expect(slide).toHaveProperty('type')
    // 多态断言
    if (slide.type === 'image') {
      expect(slide).toHaveProperty('imageMeta')
    } else if (slide.type === 'score') {
      expect(slide).toHaveProperty('scores')
      expect(slide).toHaveProperty('categoryId')
    }
  })
})
