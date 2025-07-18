import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchScreens,
  fetchScreenById,
  createScreen,
  updateScreen,
  deleteScreen,
  assignSlideDeck,
  fetchScreenContent,
  updateScreenStatus
} from '@/services/screenService'
import type { ScreenContent } from '@/interfaces/types'
import { mockSlideDecks } from '@/data/mockSlideDecks'

const mockScreen: ScreenContent = {
  id: 1,
  name: 'Screen 1',
  status: 'ONLINE',
  slideDeck: mockSlideDecks[0]
}

describe('screenService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should fetch all screens', async () => {
    const screens = await fetchScreens()
    expect(Array.isArray(screens)).toBe(true)
    expect(screens[0]).toHaveProperty('slideDeck')
    expect(screens[0].slideDeck).toHaveProperty('slides')
  })

  it('should fetch a screen by id', async () => {
    const screen = await fetchScreenById(1)
    expect(screen).toHaveProperty('id')
    expect(screen).toHaveProperty('slideDeck')
    expect(screen.slideDeck).toHaveProperty('slides')
  })

  it('should create a screen', async () => {
    const created = await createScreen(mockScreen)
    expect(created).toHaveProperty('id')
    expect(created.name).toBe('Screen 1')
  })

  it('should update a screen', async () => {
    const updated = await updateScreen(1, { ...mockScreen, name: 'Updated Screen' })
    expect(updated).toHaveProperty('id')
    expect(updated.name).toBe('Updated Screen')
  })

  it('should delete a screen', async () => {
    await expect(deleteScreen(1)).resolves.toBeUndefined()
  })

  it('should assign a slideDeck to screen', async () => {
    const result = await assignSlideDeck(1, mockSlideDecks[0].id)
    expect(result).toHaveProperty('slideDeck')
    expect(result.slideDeck).toHaveProperty('slides')
  })

  it('should fetch screen content', async () => {
    const content = await fetchScreenContent(1)
    expect(content).toHaveProperty('slideDeck')
    expect(content.slideDeck).toHaveProperty('slides')
  })

  it('should update screen status', async () => {
    const updated = await updateScreenStatus(1, 'OFFLINE')
    expect(updated).toHaveProperty('status')
    expect(updated.status).toBe('OFFLINE')
  })
})
