import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchSlideDecks,
  fetchSlideDeckById,
  createSlideDeck,
  updateSlideDeck,
  reorderSlides,
  deleteSlideDeck
} from '@/services/slideDeckService'
import { mockSlideDecks } from '@/data/mockSlideDecks'

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

  it('should reorder slides in deck', async () => {
    // 重新排序 slideDeck 内 slides
    const result = await reorderSlides(1, [1, 2, 3])
    expect(result).toHaveProperty('slides')
  })

  it('should delete a slide deck', async () => {
    // 删除 slideDeck
    await expect(deleteSlideDeck(1)).resolves.toBeUndefined()
  })
})
