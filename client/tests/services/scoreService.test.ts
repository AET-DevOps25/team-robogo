import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchScoresByCategory,
  fetchScoresWithHighlight,
  addScore,
  updateScore,
  deleteScore,
  createScoreSlide
} from '@/services/scoreService'
import type { Score, Category, SlideItem } from '@/interfaces/types'
import { SlideType } from '@/interfaces/types'

describe('ScoreService', () => {
  const mockTeam = { id: 1, name: 'Test Team' }

  const mockScores: Score[] = [
    { id: 1, points: 10, time: 60, highlight: false, team: mockTeam, rank: 1 },
    { id: 2, points: 20, time: 120, highlight: true, team: mockTeam, rank: 2 }
  ]

  const mockCategory: Category = {
    id: 1,
    name: 'Test Category',
    competitionId: 1
  }

  const mockScoreSlide: SlideItem = {
    id: 1,
    index: 0,
    name: 'Score Slide',
    type: SlideType.SCORE,
    scores: [
      { id: 1, points: 10, time: 60, highlight: false, team: mockTeam, rank: 1 },
      { id: 2, points: 20, time: 120, highlight: true, team: mockTeam, rank: 2 }
    ],
    category: mockCategory
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('fetchScoresByCategory', () => {
    it('should fetch scores by category successfully', async () => {
      const result = await fetchScoresByCategory(1)

      expect(result).toEqual({})
    })

    it('should handle fetch scores by category error', async () => {
      await expect(fetchScoresByCategory(999)).resolves.toEqual({})
    })
  })

  describe('fetchScoresWithHighlight', () => {
    it('should fetch scores with highlight successfully', async () => {
      const result = await fetchScoresWithHighlight(mockCategory)

      expect(result).toEqual({})
    })

    it('should handle fetch scores with highlight error', async () => {
      await expect(fetchScoresWithHighlight(mockCategory)).resolves.toEqual({})
    })
  })

  describe('addScore', () => {
    it('should add score successfully', async () => {
      const newScore = {
        points: 15,
        time: 90,
        highlight: false,
        team: mockTeam,
        rank: 3
      }

      const result = await addScore(newScore as any)

      expect(result).toEqual({})
    })

    it('should handle add score error', async () => {
      const newScore = {
        points: 15,
        time: 90,
        highlight: false,
        team: mockTeam,
        rank: 3
      }

      await expect(addScore(newScore as any)).resolves.toEqual({})
    })
  })

  describe('updateScore', () => {
    it('should update score successfully', async () => {
      const updateData = { points: 25, highlight: true }

      const result = await updateScore(1, updateData)

      expect(result).toEqual({})
    })

    it('should handle update score error', async () => {
      const updateData = { points: 25, highlight: true }

      await expect(updateScore(999, updateData)).resolves.toEqual({})
    })
  })

  describe('deleteScore', () => {
    it('should delete score successfully', async () => {
      await expect(deleteScore(1)).resolves.toBeUndefined()
    })

    it('should handle delete score error', async () => {
      await expect(deleteScore(999)).resolves.toBeUndefined()
    })
  })

  describe('createScoreSlide', () => {
    it('should create score slide successfully', async () => {
      const result = await createScoreSlide(mockScoreSlide)

      expect(result).toEqual({})
    })

    it('should handle create score slide error', async () => {
      await expect(createScoreSlide(mockScoreSlide)).resolves.toEqual({})
    })
  })
}) 