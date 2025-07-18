import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ScoreSlideCard from '@/components/ScoreSlideCard.vue'
import { SlideType } from '@/interfaces/types'

// Mock composables
vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: vi.fn((key: string) => key)
  })
}))

describe('ScoreSlideCard', () => {
  let wrapper: any

  const mockScoreSlide = {
    id: 1,
    index: 0,
    name: 'Test Score Slide',
    type: SlideType.SCORE,
    scores: [
      {
        id: 1,
        points: 100,
        time: 120,
        highlight: true,
        team: { id: 1, name: 'Team Alpha' },
        rank: 1
      },
      {
        id: 2,
        points: 85,
        time: 95,
        highlight: false,
        team: { id: 2, name: 'Team Beta' },
        rank: 2
      },
      {
        id: 3,
        points: 70,
        time: 80,
        highlight: false,
        team: { id: 3, name: 'Team Gamma' },
        rank: 3
      }
    ],
    category: {
      id: 1,
      name: 'Test Category',
      competitionId: 1
    }
  }

  const mockImageSlide = {
    id: 2,
    index: 1,
    name: 'Test Image Slide',
    type: SlideType.IMAGE,
    imageMeta: {
      id: 1,
      name: 'test.jpg',
      contentType: 'image/jpeg'
    }
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(ScoreSlideCard, {
      props: {
        item: mockScoreSlide,
        ...props
      }
    })
  }

  describe('rendering', () => {
    it('renders correctly with score slide', () => {
      wrapper = createWrapper()

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.score-slide-card').exists()).toBe(true)
      expect(wrapper.text()).toContain('Test Score Slide')
    })

    it('displays score slide title', () => {
      wrapper = createWrapper()

      const title = wrapper.find('.font-semibold')
      expect(title.exists()).toBe(true)
      expect(title.text()).toContain('Test Score Slide')
    })

    it('renders table with correct headers', () => {
      wrapper = createWrapper()

      const headers = wrapper.findAll('th')
      expect(headers).toHaveLength(5)
      expect(headers[0].text()).toContain('teamName')
      expect(headers[1].text()).toContain('scoreUnit')
      expect(headers[2].text()).toContain('time')
      expect(headers[3].text()).toContain('category')
      expect(headers[4].text()).toContain('rank')
    })

    it('renders all score rows', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(3)
    })

    it('displays score data correctly', () => {
      wrapper = createWrapper()

      const firstRow = wrapper.find('tbody tr')
      const cells = firstRow.findAll('td')

      expect(cells[0].text()).toBe('Team Alpha')
      expect(cells[1].text()).toBe('100')
      expect(cells[2].text()).toBe('120')
      expect(cells[3].text()).toBe('Test Category')
      expect(cells[4].text()).toBe('1')
    })
  })

  describe('highlighting', () => {
    it('applies highlight class to highlighted scores', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')
      const highlightedRow = rows[0] // First score has highlight: true
      const normalRow = rows[1] // Second score has highlight: false

      expect(highlightedRow.classes()).toContain('bg-yellow-100')
      expect(normalRow.classes()).not.toContain('bg-yellow-100')
    })

    it('applies dark mode highlight class', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')
      const highlightedRow = rows[0]

      expect(highlightedRow.classes()).toContain('dark:bg-yellow-900/80')
    })
  })

  describe('non-score slides', () => {
    it('shows fallback message for non-score slides', () => {
      wrapper = createWrapper({ item: mockImageSlide })

      expect(wrapper.text()).toContain('notScoreType')
      expect(wrapper.find('table').exists()).toBe(false)
    })

    it('shows fallback message for slides without scores property', () => {
      const slideWithoutScores = {
        id: 3,
        index: 2,
        name: 'Invalid Slide',
        type: SlideType.SCORE,
        category: { id: 1, name: 'Test Category', competitionId: 1 }
      }

      wrapper = createWrapper({ item: slideWithoutScores })

      expect(wrapper.text()).toContain('notScoreType')
      expect(wrapper.find('table').exists()).toBe(false)
    })
  })

  describe('data display', () => {
    it('displays all score information', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')

      // Check first row (highlighted)
      const firstRowCells = rows[0].findAll('td')
      expect(firstRowCells[0].text()).toBe('Team Alpha')
      expect(firstRowCells[1].text()).toBe('100')
      expect(firstRowCells[2].text()).toBe('120')
      expect(firstRowCells[3].text()).toBe('Test Category')
      expect(firstRowCells[4].text()).toBe('1')

      // Check second row (normal)
      const secondRowCells = rows[1].findAll('td')
      expect(secondRowCells[0].text()).toBe('Team Beta')
      expect(secondRowCells[1].text()).toBe('85')
      expect(secondRowCells[2].text()).toBe('95')
      expect(secondRowCells[3].text()).toBe('Test Category')
      expect(secondRowCells[4].text()).toBe('2')
    })

    it('handles empty scores array', () => {
      const slideWithEmptyScores = {
        ...mockScoreSlide,
        scores: []
      }

      wrapper = createWrapper({ item: slideWithEmptyScores })

      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(0)
    })

    it('handles missing category', () => {
      const slideWithoutCategory = {
        ...mockScoreSlide,
        category: null
      }

      wrapper = createWrapper({ item: slideWithoutCategory })

      const rows = wrapper.findAll('tbody tr')
      const firstRowCells = rows[0].findAll('td')
      expect(firstRowCells[3].text()).toBe('')
    })
  })

  describe('styling', () => {
    it('applies correct CSS classes', () => {
      wrapper = createWrapper()

      expect(wrapper.find('.score-slide-card').exists()).toBe(true)
      expect(wrapper.find('.w-full').exists()).toBe(true)
      expect(wrapper.find('table').exists()).toBe(true)
    })

    it('applies table styling', () => {
      wrapper = createWrapper()

      const table = wrapper.find('table')
      expect(table.classes()).toContain('w-full')
      expect(table.classes()).toContain('text-sm')
      expect(table.classes()).toContain('border-separate')
    })

    it('applies header styling', () => {
      wrapper = createWrapper()

      const headers = wrapper.findAll('th')
      headers.forEach((header: any) => {
        expect(header.classes()).toContain('bg-gray-100')
        expect(header.classes()).toContain('dark:bg-gray-800')
        expect(header.classes()).toContain('font-semibold')
      })
    })

    it('applies row hover effects', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')
      rows.forEach((row: any) => {
        expect(row.classes()).toContain('transition-colors')
        // Only check hover classes for non-highlighted rows
        if (!row.classes().includes('bg-yellow-100')) {
          expect(row.classes()).toContain('hover:bg-gray-50')
          expect(row.classes()).toContain('dark:hover:bg-gray-800')
        } else {
          // For highlighted rows, check for highlight-specific hover classes
          expect(row.classes()).toContain('hover:bg-yellow-200')
          expect(row.classes()).toContain('dark:hover:bg-yellow-800')
        }
      })
    })
  })

  describe('accessibility', () => {
    it('has proper table structure', () => {
      wrapper = createWrapper()

      expect(wrapper.find('thead').exists()).toBe(true)
      expect(wrapper.find('tbody').exists()).toBe(true)
      expect(wrapper.find('table').exists()).toBe(true)
    })

    it('has proper header cells', () => {
      wrapper = createWrapper()

      const headers = wrapper.findAll('th')
      expect(headers.length).toBeGreaterThan(0)

      headers.forEach((header: any) => {
        expect(header.exists()).toBe(true)
      })
    })

    it('has proper data cells', () => {
      wrapper = createWrapper()

      const rows = wrapper.findAll('tbody tr')
      expect(rows.length).toBeGreaterThan(0)

      rows.forEach((row: any) => {
        const cells = row.findAll('td')
        expect(cells.length).toBe(5)
      })
    })
  })

  describe('edge cases', () => {
    it('handles very long team names', () => {
      const slideWithLongNames = {
        ...mockScoreSlide,
        scores: [
          {
            ...mockScoreSlide.scores[0],
            team: { id: 1, name: 'Very Long Team Name That Exceeds Normal Length' }
          }
        ]
      }

      wrapper = createWrapper({ item: slideWithLongNames })

      const firstRow = wrapper.find('tbody tr')
      const teamCell = firstRow.findAll('td')[0]
      expect(teamCell.text()).toBe('Very Long Team Name That Exceeds Normal Length')
    })

    it('handles zero values', () => {
      const slideWithZeroValues = {
        ...mockScoreSlide,
        scores: [
          {
            id: 1,
            points: 0,
            time: 0,
            highlight: false,
            team: { id: 1, name: 'Team Zero' },
            rank: 1
          }
        ]
      }

      wrapper = createWrapper({ item: slideWithZeroValues })

      const firstRow = wrapper.find('tbody tr')
      const cells = firstRow.findAll('td')
      expect(cells[1].text()).toBe('0')
      expect(cells[2].text()).toBe('0')
    })

    it('handles negative values', () => {
      const slideWithNegativeValues = {
        ...mockScoreSlide,
        scores: [
          {
            id: 1,
            points: -10,
            time: -5,
            highlight: false,
            team: { id: 1, name: 'Team Negative' },
            rank: 1
          }
        ]
      }

      wrapper = createWrapper({ item: slideWithNegativeValues })

      const firstRow = wrapper.find('tbody tr')
      const cells = firstRow.findAll('td')
      expect(cells[1].text()).toBe('-10')
      expect(cells[2].text()).toBe('-5')
    })

    it('handles missing team information', () => {
      const slideWithMissingTeam = {
        ...mockScoreSlide,
        scores: [
          {
            id: 1,
            points: 100,
            time: 120,
            highlight: false,
            team: null,
            rank: 1
          }
        ]
      }

      wrapper = createWrapper({ item: slideWithMissingTeam })

      const firstRow = wrapper.find('tbody tr')
      const cells = firstRow.findAll('td')
      expect(cells[0].text()).toBe('')
    })
  })

  describe('internationalization', () => {
    it('uses translation keys for headers', () => {
      wrapper = createWrapper()

      const headers = wrapper.findAll('th')
      expect(headers[0].text()).toContain('teamName')
      expect(headers[1].text()).toContain('scoreUnit')
      expect(headers[2].text()).toContain('time')
      expect(headers[3].text()).toContain('category')
      expect(headers[4].text()).toContain('rank')
    })

    it('uses translation key for fallback message', () => {
      wrapper = createWrapper({ item: mockImageSlide })

      expect(wrapper.text()).toContain('notScoreType')
    })
  })
})
